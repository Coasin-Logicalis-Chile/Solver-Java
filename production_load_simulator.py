#!/usr/bin/env python3
"""
SIMULADOR DE CARGA DE PRODUCCIÓN

Este script simula el tráfico HTTP real observado en los logs de producción
para reproducir las condiciones exactas que causan ConcurrentModificationException.

Basado en los logs reales:
- http-nio-6050-exec-54, exec-62, exec-51, etc.
- Operaciones ServiceNow concurrentes
- Múltiples tipos de requests simultáneos

Autor: Ivan Hills - Logicalis Production Load Testing
"""

import requests
import threading
import time
import random
import json
from datetime import datetime
from concurrent.futures import ThreadPoolExecutor, as_completed
from urllib3.exceptions import InsecureRequestWarning
import urllib3

# Disable SSL warnings for testing
urllib3.disable_warnings(InsecureRequestWarning)

class ProductionLoadSimulator:
    """Simula carga de producción basada en logs reales"""
    
    def __init__(self, base_url="http://localhost:6050", max_workers=20):
        self.base_url = base_url
        self.max_workers = max_workers
        self.results = {
            'successful_requests': 0,
            'failed_requests': 0,
            'concurrent_errors': 0,
            'timeout_errors': 0,
            'total_requests': 0
        }
        self.lock = threading.Lock()
        
        # Endpoints observados en los logs de producción
        self.endpoints = [
            "/api/incidents/{incident_id}",
            "/api/journals/{journal_id}", 
            "/api/task-slas/{task_sla_id}",
            "/api/sc-tasks",
            "/api/attachments/{attachment_id}/file",
            "/api/sys-users/{user_id}"
        ]
        
        # IDs reales observados en los logs
        self.test_ids = [
            "INC1851512", "INC1851513", "INC1851514",
            "76002cef333eae10d26161a9ed5c7b89",
            "ef7f906b47b66e10420f904d416d439b", 
            "df00a02f47b66e10420f904d416d4374",
            "TASK1498794", "TASK1496110"
        ]
    
    def log_result(self, result_type, thread_id, endpoint, status_code=None, error=None):
        """Log resultado de request"""
        with self.lock:
            self.results['total_requests'] += 1
            self.results[f'{result_type}_requests'] += 1
            
            if error and 'concurrent' in str(error).lower():
                self.results['concurrent_errors'] += 1
                print(f" CONCURRENT ERROR - Hilo {thread_id}: {endpoint} - {error}")
            elif error and 'timeout' in str(error).lower():
                self.results['timeout_errors'] += 1
                print(f"TIMEOUT - Hilo {thread_id}: {endpoint}")
            elif result_type == 'failed':
                print(f" ERROR - Hilo {thread_id}: {endpoint} - Status: {status_code}")
    
    def create_request_worker(self, thread_id, num_requests=30):
        """Worker que simula un hilo HTTP como http-nio-6050-exec-XX"""
        
        def execute_requests():
            for i in range(num_requests):
                try:
                    # Simular diferentes tipos de operaciones como en los logs
                    endpoint = random.choice(self.endpoints)
                    test_id = random.choice(self.test_ids)
                    url = self.base_url + endpoint.format(
                        incident_id=test_id,
                        journal_id=test_id, 
                        task_sla_id=test_id,
                        attachment_id=test_id,
                        user_id=test_id
                    )
                    
                    # Simular diferentes métodos HTTP como en producción
                    method = random.choices(['GET', 'PUT', 'POST'], weights=[40, 40, 20])[0]
                    
                    headers = {
                        'Content-Type': 'application/json',
                        'User-Agent': f'SpringBoot-RestTemplate-Thread-{thread_id}',
                        'X-Thread-ID': f'http-nio-6050-exec-{thread_id}'
                    }
                    
                    # Payload para requests PUT/POST
                    payload = {
                        'integrationId': test_id,
                        'timestamp': datetime.now().isoformat(),
                        'threadId': thread_id,
                        'operation': f'{method}_{endpoint}'
                    } if method in ['PUT', 'POST'] else None
                    
                    start_time = time.time()
                    
                    try:
                        # Ejecutar request HTTP
                        response = requests.request(
                            method=method,
                            url=url,
                            headers=headers,
                            json=payload,
                            timeout=10,
                            verify=False  # Para testing local
                        )
                        
                        request_time = time.time() - start_time
                        
                        if response.status_code < 400:
                            self.log_result('successful', thread_id, endpoint, response.status_code)
                        else:
                            self.log_result('failed', thread_id, endpoint, response.status_code)
                            
                    except requests.exceptions.ConnectionError as e:
                        # Esto es normal si el servidor no está corriendo
                        if 'concurrent' in str(e).lower() or 'modification' in str(e).lower():
                            self.log_result('failed', thread_id, endpoint, error=f"ConcurrentError: {e}")
                        else:
                            self.log_result('failed', thread_id, endpoint, error=f"Connection: {e}")
                    
                    except requests.exceptions.Timeout:
                        self.log_result('failed', thread_id, endpoint, error="Timeout")
                    
                    except Exception as e:
                        self.log_result('failed', thread_id, endpoint, error=str(e))
                    
                    # Simular delay entre requests como en producción
                    time.sleep(random.uniform(0.1, 0.5))
                    
                except Exception as e:
                    print(f"🔥 ERROR CRÍTICO en hilo {thread_id}: {e}")
        
        return execute_requests
    
    def simulate_production_load(self, duration_seconds=60, concurrent_threads=15):
        """Simular carga de producción por tiempo determinado"""
        print(f" INICIANDO SIMULACIÓN DE CARGA DE PRODUCCIÓN")
        print(f"  Duración: {duration_seconds} segundos")
        print(f" Hilos concurrentes: {concurrent_threads}")
        print(f" Endpoints objetivo: {len(self.endpoints)}")
        print("-" * 60)
        
        start_time = time.time()
        requests_per_thread = max(1, duration_seconds // 2)  # Calcular requests por hilo
        
        # Ejecutar carga concurrente
        with ThreadPoolExecutor(max_workers=concurrent_threads) as executor:
            # Crear workers que simulan hilos HTTP de Spring Boot
            futures = []
            for thread_id in range(concurrent_threads):
                worker = self.create_request_worker(f"exec-{thread_id+50}", requests_per_thread)
                future = executor.submit(worker)
                futures.append(future)
            
            # Monitorear progreso
            completed = 0
            for future in as_completed(futures, timeout=duration_seconds + 30):
                completed += 1
                try:
                    future.result()
                    print(f" Hilo {completed}/{concurrent_threads} completado")
                except Exception as e:
                    print(f" Error en hilo {completed}: {e}")
        
        total_time = time.time() - start_time
        
        # Mostrar resultados
        print(f"\n RESULTADOS DE SIMULACIÓN DE PRODUCCIÓN:")
        print(f"  Tiempo total: {total_time:.2f} segundos")
        print(f" Total requests: {self.results['total_requests']}")
        print(f" Requests exitosos: {self.results['successful_requests']}")
        print(f" Requests fallidos: {self.results['failed_requests']}")
        print(f" Errores de concurrencia: {self.results['concurrent_errors']}")
        print(f" Timeouts: {self.results['timeout_errors']}")
        
        if self.results['total_requests'] > 0:
            success_rate = (self.results['successful_requests'] / self.results['total_requests']) * 100
            print(f" Tasa de éxito: {success_rate:.1f}%")
            print(f" Throughput: {self.results['total_requests'] / total_time:.1f} req/sec")
        
        # Análisis de concurrencia
        if self.results['concurrent_errors'] > 0:
            print(f"\n ANÁLISIS DE CONCURRENCIA:")
            print(f"    Se detectaron {self.results['concurrent_errors']} errores de concurrencia")
            print(f"    Esto representa un {(self.results['concurrent_errors'] / self.results['total_requests']) * 100:.1f}% del tráfico")
            print(f"    RECOMENDACIÓN: Implementar solución thread-safe urgente")
        else:
            print(f"\n CONCURRENCIA: No se detectaron errores específicos de concurrencia")
            print(f"    NOTA: El servidor puede no estar corriendo o ya tiene la solución aplicada")
    
    def generate_curl_commands(self):
        """Generar comandos curl para testing manual"""
        print(f"\n COMANDOS CURL PARA TESTING MANUAL:")
        print("-" * 50)
        
        for i, endpoint in enumerate(self.endpoints[:3]):  # Solo mostrar algunos
            test_id = self.test_ids[i % len(self.test_ids)]
            url = self.base_url + endpoint.format(
                incident_id=test_id,
                journal_id=test_id,
                task_sla_id=test_id,
                attachment_id=test_id,
                user_id=test_id
            )
            
            print(f"# Test {i+1}: {endpoint}")
            print(f"curl -X GET \"{url}\" \\")
            print(f"  -H \"Content-Type: application/json\" \\")
            print(f"  -H \"X-Thread-ID: manual-test-{i}\"")
            print()

def run_concurrent_curl_test():
    """Ejecutar múltiples curl commands concurrentemente"""
    print("EJECUTANDO TEST CONCURRENTE CON CURL...")
    
    # Comandos curl que se ejecutarán concurrentemente
    curl_commands = [
        'curl -X GET "http://localhost:6050/api/incidents/INC1851512" -H "Content-Type: application/json" --max-time 5 -s',
        'curl -X PUT "http://localhost:6050/api/incidents/INC1851513" -H "Content-Type: application/json" -d \'{"integrationId":"INC1851513"}\' --max-time 5 -s',
        'curl -X GET "http://localhost:6050/api/journals/76002cef333eae10d26161a9ed5c7b89" -H "Content-Type: application/json" --max-time 5 -s',
        'curl -X PUT "http://localhost:6050/api/task-slas/ef7f906b47b66e10420f904d416d439b" -H "Content-Type: application/json" -d \'{"sysId":"ef7f906b47b66e10420f904d416d439b"}\' --max-time 5 -s',
        'curl -X GET "http://localhost:6050/api/sc-tasks" -H "Content-Type: application/json" --max-time 5 -s'
    ]
    
    def execute_curl(command, thread_id):
        """Ejecutar comando curl"""
        import subprocess
        try:
            result = subprocess.run(command, shell=True, capture_output=True, text=True, timeout=10)
            print(f" Curl-{thread_id}: Status {result.returncode}")
            if result.stderr and 'concurrent' in result.stderr.lower():
                print(f" CONCURRENT ERROR detectado en curl-{thread_id}")
        except subprocess.TimeoutExpired:
            print(f" Timeout en curl-{thread_id}")
        except Exception as e:
            print(f" Error en curl-{thread_id}: {e}")
    
    # Ejecutar múltiples curl concurrentemente
    with ThreadPoolExecutor(max_workers=10) as executor:
        for round_num in range(3):  # 3 rondas de requests
            print(f"\n Ronda {round_num + 1}/3")
            futures = []
            for i, cmd in enumerate(curl_commands):
                thread_id = f"{round_num}-{i}"
                future = executor.submit(execute_curl, cmd, thread_id)
                futures.append(future)
            
            # Esperar que terminen
            for future in as_completed(futures, timeout=15):
                try:
                    future.result()
                except Exception as e:
                    print(f" Error en curl execution: {e}")
            
            time.sleep(1)  # Pausa entre rondas

def main():
    """Función principal"""
    print(" SIMULADOR DE CARGA DE PRODUCCIÓN - LOGICALIS")
    print("=" * 60)
    print("Reproduce las condiciones de carga observadas en los logs")
    print("para identificar errores de concurrencia en RestTemplate")
    print("=" * 60)
    
    simulator = ProductionLoadSimulator()
    
    print("\nOPCIONES DE TESTING:")
    print("1. Simulación de carga HTTP (requiere servidor corriendo)")
    print("2. Generar comandos curl para testing manual")  
    print("3. Ejecutar test concurrente con curl")
    
    choice = input("\nSeleccionar opción (1-3): ").strip()
    
    if choice == "1":
        duration = int(input("Duración en segundos (default 30): ") or 30)
        threads = int(input("Hilos concurrentes (default 10): ") or 10)
        simulator.simulate_production_load(duration, threads)
        
    elif choice == "2":
        simulator.generate_curl_commands()
        
    elif choice == "3":
        run_concurrent_curl_test()
        
    else:
        print(" Ejecutando simulación básica...")
        simulator.simulate_production_load(duration_seconds=30, concurrent_threads=10)
    
    print(f"\n SIGUIENTE PASO: Verificar logs de la aplicación para:")
    print(f"    ConcurrentModificationException")
    print(f"    Errores en BasicAuthenticationInterceptor")
    print(f"    ArrayList$Itr.checkForComodification")
    
if __name__ == "__main__":
    main()

"""
INSTRUCCIONES DE USO:

1. Con servidor Spring Boot corriendo:
   python production_load_simulator.py
   (Seleccionar opción 1)

2. Para generar comandos curl:
   python production_load_simulator.py  
   (Seleccionar opción 2)

3. Para test rápido con curl:
   python production_load_simulator.py
   (Seleccionar opción 3)

4. Ejecutar en paralelo con otras herramientas:
   # Terminal 1
   python production_load_simulator.py
   
   # Terminal 2  
   tail -f /path/to/logs/application.log | grep -i concurrent

RESULTADO ESPERADO:
- Con código original: Errores de ConcurrentModificationException
- Con solución aplicada: Sin errores de concurrencia

Autor: Ivan Hills - Logicalis Production Testing
"""
