#!/usr/bin/env python3
"""
TEST REMOTO DE API - ConcurrentModificationException
==================================================

Test simple para verificar si el endpoint remoto esta funcionando
y si genera errores de concurrencia.
"""

import requests
import time
import threading
from concurrent.futures import ThreadPoolExecutor
import urllib3

# Deshabilitar warnings de SSL
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# URL del servidor remoto
REMOTE_URL = "https://solver-dev.westus2.cloudapp.azure.com:8443/api/v1/findPaginatedIncidentsByFilters"

def test_single_request():
    """Hacer una sola request para verificar conectividad"""
    try:
        print("Testing single request...")
        response = requests.get(
            REMOTE_URL,
            params={
                "page": 0,
                "size": 10,
                "filter": "test"
            },
            timeout=10,
            verify=False
        )
        print(f"Status: {response.status_code}")
        print(f"Content length: {len(response.text)} chars")
        if response.status_code >= 400:
            print(f"Error content: {response.text[:500]}")
        return response.status_code
    except Exception as e:
        print(f"Exception: {str(e)}")
        return None

def concurrent_request(request_id):
    """Request concurrente para provocar ConcurrentModificationException"""
    try:
        response = requests.get(
            REMOTE_URL,
            params={
                "page": 0,
                "size": 50,
                "filter": f"concurrent_test_{request_id}",
                "assignedTo": request_id % 100,
                "company": (request_id % 10) + 1,
                "state": str((request_id % 5) + 1)
            },
            timeout=15,
            verify=False
        )
        return {
            "id": request_id,
            "status": response.status_code,
            "size": len(response.text),
            "error": None
        }
    except Exception as e:
        return {
            "id": request_id,
            "status": None,
            "size": 0,
            "error": str(e)
        }

def test_concurrent_requests(num_requests=50):
    """Test de requests concurrentes"""
    print(f"\nTesting {num_requests} concurrent requests...")
    
    results = []
    start_time = time.time()
    
    with ThreadPoolExecutor(max_workers=20) as executor:
        futures = [executor.submit(concurrent_request, i) for i in range(num_requests)]
        for future in futures:
            results.append(future.result())
    
    end_time = time.time()
    
    # Analizar resultados
    status_counts = {}
    errors_500 = 0
    errors_502 = 0
    success_200 = 0
    exceptions = 0
    
    for result in results:
        if result["error"]:
            exceptions += 1
        elif result["status"]:
            status_counts[result["status"]] = status_counts.get(result["status"], 0) + 1
            if result["status"] == 500:
                errors_500 += 1
            elif result["status"] == 502:
                errors_502 += 1
            elif result["status"] == 200:
                success_200 += 1
    
    print(f"\nRESULTADOS ({end_time - start_time:.2f}s):")
    print(f"Total requests: {num_requests}")
    print(f"Successful (200): {success_200}")
    print(f"Server errors (500): {errors_500}")
    print(f"Gateway errors (502): {errors_502}")
    print(f"Exceptions: {exceptions}")
    print(f"\nStatus code distribution:")
    for status, count in sorted(status_counts.items()):
        print(f"  {status}: {count}")
    
    # Evaluacion
    print(f"\n{'='*60}")
    if errors_500 > 0:
        print("⚠️  PROBLEMA DETECTADO!")
        print(f"Se encontraron {errors_500} errores 500 - Posible ConcurrentModificationException")
        print("El codigo aun NO ha sido desplegado o el problema persiste")
    elif errors_502 > num_requests * 0.8:  # Mas del 80% son 502
        print("🔧 SERVIDOR NO DISPONIBLE")
        print("La mayoria son errores 502 - Problema de infraestructura/proxy")
        print("No se puede determinar si el fix funciona")
    elif success_200 > 0:
        print("✅ SERVIDOR FUNCIONANDO")
        print(f"Se obtuvieron {success_200} respuestas exitosas")
        print("No se detectaron errores 500 de concurrencia")
    else:
        print("❓ RESULTADO INCIERTO")
        print("No hay suficientes datos para determinar el estado")

if __name__ == "__main__":
    print("TESTING REMOTE API FOR CONCURRENCY ISSUES")
    print(f"Target: {REMOTE_URL}")
    print("="*60)
    
    # Test de conectividad
    single_status = test_single_request()
    
    if single_status is not None:
        # Test concurrente
        test_concurrent_requests(50)
    else:
        print("Cannot proceed with concurrent test - server unreachable")
