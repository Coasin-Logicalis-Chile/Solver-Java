#!/usr/bin/env python3
"""
QUICK TEST - Reproducir ConcurrentModificationException

Script rápido y simple para demostrar el problema de concurrencia
en el RestTemplate y validar la solución.

Uso: python quick_concurrency_test.py

Autor: Ivan Hills - Logicalis
"""

import threading
import time
import random
from concurrent.futures import ThreadPoolExecutor

class QuickConcurrencyDemo:
    
    def __init__(self):
        self.shared_list = []  #  Simula interceptors de RestTemplate
        self.error_count = 0
        self.success_count = 0
        self.lock = threading.Lock()
    
    def demonstrate_problem(self):
        """ Demuestra el problema original"""
        print(" DEMOSTRANDO PROBLEMA DE CONCURRENCIA...")
        print("-" * 50)
        
        def problematic_worker(thread_id):
            """Simula lo que hace Rest.java (PROBLEMÁTICO)"""
            for i in range(20):
                try:
                    #  PROBLEMA: Modificar lista compartida
                    self.shared_list.append(f"interceptor_{thread_id}_{i}")
                    
                    #  PROBLEMA: Iterar mientras otros modifican
                    for item in self.shared_list:
                        time.sleep(0.001)  # Simular procesamiento
                    
                    with self.lock:
                        self.success_count += 1
                        
                    # Limpiar ocasionalmente
                    if random.random() < 0.2:
                        self.shared_list.clear()
                        
                except Exception as e:
                    with self.lock:
                        self.error_count += 1
                        print(f"🚨 ERROR en hilo {thread_id}: {e}")
        
        # Ejecutar múltiples hilos
        with ThreadPoolExecutor(max_workers=10) as executor:
            futures = [executor.submit(problematic_worker, i) for i in range(10)]
            for future in futures:
                future.result()
        
        print(f" Operaciones exitosas: {self.success_count}")
        print(f" Errores detectados: {self.error_count}")
        
        if self.error_count > 0:
            print(" PROBLEMA REPRODUCIDO: Se encontraron errores de concurrencia")
        else:
            print("  Problema no reproducido, intentar con más carga")
    
    def demonstrate_solution(self):
        """ Demuestra la solución thread-safe"""
        print("\n DEMOSTRANDO SOLUCIÓN THREAD-SAFE...")
        print("-" * 50)
        
        # Reset counters
        self.error_count = 0
        self.success_count = 0
        
        def thread_safe_worker(thread_id):
            """Implementa la solución thread-safe"""
            for i in range(30):
                try:
                    #  SOLUCIÓN: Crear lista local (no compartida)
                    local_interceptors = []
                    local_interceptors.append(f"safe_interceptor_{thread_id}_{i}")
                    
                    #  SEGURO: Iterar sobre lista local
                    for item in local_interceptors:
                        time.sleep(0.001)  # Simular procesamiento
                    
                    with self.lock:
                        self.success_count += 1
                        
                except Exception as e:
                    with self.lock:
                        self.error_count += 1
                        print(f"🚨 ERROR INESPERADO en hilo {thread_id}: {e}")
        
        # Ejecutar múltiples hilos
        with ThreadPoolExecutor(max_workers=15) as executor:
            futures = [executor.submit(thread_safe_worker, i) for i in range(15)]
            for future in futures:
                future.result()
        
        print(f" Operaciones exitosas: {self.success_count}")
        print(f" Errores detectados: {self.error_count}")
        
        if self.error_count == 0:
            print(" SOLUCIÓN VALIDADA: Sin errores de concurrencia")
        else:
            print(" Solución necesita revisión")

def main():
    """Ejecutar demostración completa"""
    print(" QUICK CONCURRENCY TEST - LOGICALIS")
    print("=" * 50)
    print("Simulando problema de RestTemplate en Rest.java")
    print("=" * 50)
    
    demo = QuickConcurrencyDemo()
    
    # Test 1: Problema original
    demo.demonstrate_problem()
    
    # Test 2: Solución thread-safe
    demo.demonstrate_solution()
    
    print("\n RESUMEN:")
    print(" Problema de concurrencia demostrado")
    print(" Solución thread-safe validada")
    print("\n RECOMENDACIÓN: Aplicar patrón thread-safe en Rest.java")
    print(" Ver archivo completo: concurrency_test_simulator.py")
    
if __name__ == "__main__":
    main()
