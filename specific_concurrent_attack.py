#!/usr/bin/env python3
"""
Script específico para generar ConcurrentModificationException
Este script ataca directamente endpoints conocidos que usan ArrayList sin sincronización
"""

import asyncio
import aiohttp
import time
import random
from concurrent.futures import ThreadPoolExecutor
import logging

# Configuración de logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Configuración del servidor
BASE_URL = "http://localhost:6050"

# Endpoints específicos conocidos por usar ArrayList
VULNERABLE_ENDPOINTS = [
    "/api/Incident",
    "/api/Company",
    "/api/SysUser",
    "/api/SysGroup",
    "/api/Journal", 
    "/api/Attachment",
    "/api/ScRequest",
    "/api/ScTask",
]

# Configuración del ataque
CONCURRENT_REQUESTS = 200  # Más concurrencia
DURATION_SECONDS = 30
TIMEOUT_SECONDS = 5

class ConcurrentAttacker:
    def __init__(self):
        self.session = None
        self.error_count = 0
        self.timeout_count = 0
        self.success_count = 0
        self.concurrent_errors = []
    
    async def make_request(self, endpoint, method="GET", params=None):
        """Hace una request a un endpoint específico"""
        try:
            url = f"{BASE_URL}{endpoint}"
            
            # Parámetros aleatorios para forzar diferentes paths de código
            if not params:
                params = {
                    "limit": random.randint(1, 100),
                    "offset": random.randint(0, 50),
                    "query": f"id={random.randint(1, 1000)}",
                    "fields": "sys_id,name,state,active",
                    "sysparm_query": f"active=true^state={random.randint(1, 5)}"
                }
            
            # Usar timeout muy corto para forzar timeout y interrumpir operaciones
            async with self.session.request(method, url, params=params, timeout=aiohttp.ClientTimeout(total=TIMEOUT_SECONDS)) as response:
                content = await response.text()
                
                # Buscar indicios de errores Java en la respuesta
                java_errors = [
                    "ConcurrentModificationException",
                    "ArrayList$Itr",
                    "java.util.NoSuchElementException",
                    "java.lang.ArrayIndexOutOfBoundsException",
                    "java.util.ConcurrentModificationException",
                    "Internal Server Error",
                    "500",
                    "Iterator"
                ]
                
                for error in java_errors:
                    if error in content:
                        self.concurrent_errors.append({
                            'endpoint': endpoint,
                            'status': response.status,
                            'error': error,
                            'content': content[:500],  # Primeros 500 chars
                            'time': time.time()
                        })
                        logger.error(f"¡ERRORES JAVA ENCONTRADOS en {endpoint}!")
                        logger.error(f"Status: {response.status}")
                        logger.error(f"Errores detectados: {[e for e in java_errors if e in content]}")
                        logger.error(f"Contenido (primeros 500 chars): {content[:500]}")
                        break
                
                if response.status == 200:
                    self.success_count += 1
                else:
                    self.error_count += 1
                    
        except asyncio.TimeoutError:
            self.timeout_count += 1
            logger.debug(f"Timeout en {endpoint}")
        except Exception as e:
            self.error_count += 1
            error_msg = str(e)
            # Buscar errores de concurrencia en las excepciones
            if any(term in error_msg for term in ["ConcurrentModificationException", "Iterator", "ArrayList"]):
                self.concurrent_errors.append({
                    'endpoint': endpoint,
                    'exception': error_msg,
                    'time': time.time()
                })
                logger.error(f"¡EXCEPCIÓN DE CONCURRENCIA!: {error_msg}")
            logger.debug(f"Error en request {endpoint}: {e}")
    
    async def attack_endpoint(self, endpoint):
        """Ataca un endpoint específico con múltiples métodos"""
        tasks = []
        
        # GET requests con diferentes parámetros
        for _ in range(10):
            tasks.append(self.make_request(endpoint, "GET"))
        
        # POST requests (si es aplicable)
        if endpoint in ["/api/Incident", "/api/Company"]:
            for _ in range(5):
                tasks.append(self.make_request(endpoint, "POST", params={"data": f"test_{random.randint(1, 1000)}"}))
        
        await asyncio.gather(*tasks, return_exceptions=True)
    
    async def massive_concurrent_attack(self):
        """Ataque masivo concurrente a todos los endpoints vulnerables"""
        logger.info("🚨 Iniciando ataque masivo para forzar ConcurrentModificationException...")
        
        # Configurar connector con muchas conexiones
        connector = aiohttp.TCPConnector(
            limit=500,
            limit_per_host=200,
            keepalive_timeout=1,
            enable_cleanup_closed=True
        )
        
        async with aiohttp.ClientSession(connector=connector) as session:
            self.session = session
            
            start_time = time.time()
            while time.time() - start_time < DURATION_SECONDS:
                
                # Crear tareas para todos los endpoints
                tasks = []
                for _ in range(CONCURRENT_REQUESTS):
                    endpoint = random.choice(VULNERABLE_ENDPOINTS)
                    tasks.append(self.attack_endpoint(endpoint))
                
                # Ejecutar todas las tareas concurrentemente
                await asyncio.gather(*tasks, return_exceptions=True)
                
                # Small delay para evitar saturar completamente
                await asyncio.sleep(0.1)
        
        # Mostrar resultados
        self.print_results()
    
    def print_results(self):
        """Imprime los resultados del ataque"""
        total_requests = self.success_count + self.error_count + self.timeout_count
        
        print(f"\n{'='*50}")
        print(f"🎯 RESULTADOS DEL ATAQUE DE CONCURRENCIA")
        print(f"{'='*50}")
        print(f"📊 Total de requests: {total_requests}")
        print(f"✅ Exitosas: {self.success_count}")
        print(f"❌ Con error: {self.error_count}")  
        print(f"⏰ Timeouts: {self.timeout_count}")
        print(f"🐛 Errores de concurrencia capturados: {len(self.concurrent_errors)}")
        
        if self.concurrent_errors:
            print(f"\n🔍 DETALLES DE ERRORES JAVA ENCONTRADOS:")
            for i, error in enumerate(self.concurrent_errors[:10], 1):  # Mostrar solo los primeros 10
                print(f"\n--- Error {i} ---")
                if 'endpoint' in error:
                    print(f"Endpoint: {error['endpoint']}")
                if 'status' in error:
                    print(f"Status: {error['status']}")
                if 'error' in error:
                    print(f"Error detectado: {error['error']}")
                if 'exception' in error:
                    print(f"Excepción: {error['exception']}")
                if 'content' in error:
                    print(f"Contenido: {error['content']}")
        
        # Calcular tasa de éxito
        success_rate = (self.success_count / total_requests * 100) if total_requests > 0 else 0
        print(f"\n📈 Tasa de éxito: {success_rate:.1f}%")
        
        if success_rate < 10:
            print("🔥 ¡API COMPLETAMENTE INESTABLE BAJO CARGA!")
        elif len(self.concurrent_errors) > 0:
            print("🎯 ¡ERRORES DE CONCURRENCIA DETECTADOS!")
        
        print("="*50)

async def main():
    """Función principal"""
    print("🚨 INICIANDO ATAQUE ESPECÍFICO PARA CAPTURAR ConcurrentModificationException")
    print("⚠️  Este script está diseñado para forzar errores de concurrencia en ArrayList")
    print("📝 Monitorea cuidadosamente los logs del servidor Spring Boot\n")
    
    attacker = ConcurrentAttacker()
    await attacker.massive_concurrent_attack()
    
    print("\n💡 RECOMENDACIONES PARA VERIFICAR ERRORES:")
    print("1. Revisa los logs de Spring Boot inmediatamente después de este ataque")
    print("2. Busca en los logs: ConcurrentModificationException, ArrayList$Itr, Iterator")
    print("3. Si no ves errores, incrementa el logging de la aplicación")
    print("4. Considera habilitar DEBUG level para paquetes java.util")

if __name__ == "__main__":
    asyncio.run(main())
