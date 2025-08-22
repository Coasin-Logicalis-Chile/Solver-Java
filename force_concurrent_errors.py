#!/usr/bin/env python3
"""
Script para Forzar ConcurrentModificationException en Servidor Java
==================================================================

Este script está diseñado específicamente para provocar errores Java
que aparezcan en los logs del servidor, no solo crashes de red.
"""

import asyncio
import aiohttp
import argparse
import time
import random
from datetime import datetime
import sys
import logging
from pathlib import Path

def setup_logging():
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    log_filename = f"force_errors_{timestamp}.log"
    
    logs_dir = Path("java_crash_logs")
    logs_dir.mkdir(exist_ok=True)
    
    logging.basicConfig(
        level=logging.DEBUG,
        format='%(asctime)s - %(levelname)s - %(message)s',
        handlers=[
            logging.FileHandler(logs_dir / log_filename, encoding='utf-8'),
            logging.StreamHandler(sys.stdout)
        ]
    )
    
    logger = logging.getLogger(__name__)
    logger.info(f"INICIANDO SCRIPT PARA FORZAR ERRORES JAVA - Log: {logs_dir / log_filename}")
    return logger

class JavaErrorForcer:
    def __init__(self, base_url: str = "http://localhost:6050"):
        self.base_url = base_url.rstrip('/')
        self.logger = logging.getLogger(self.__class__.__name__)
        self.session = None
        self.total_requests = 0
        self.errors_found = 0
        
    async def __aenter__(self):
        connector = aiohttp.TCPConnector(
            limit=200,
            limit_per_host=100,
            ttl_dns_cache=0,
            use_dns_cache=False,
            keepalive_timeout=1
        )
        
        timeout = aiohttp.ClientTimeout(total=30, connect=5)
        self.session = aiohttp.ClientSession(
            connector=connector,
            timeout=timeout,
            headers={
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'User-Agent': 'JavaErrorForcer/1.0'
            }
        )
        return self
    
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        if self.session and not self.session.closed:
            await self.session.close()

    async def make_request(self, endpoint: str, method: str = "GET", data: dict = None):
        """Hacer request y capturar respuesta completa"""
        try:
            self.total_requests += 1
            
            if method == "GET":
                async with self.session.get(f"{self.base_url}{endpoint}", params=data) as response:
                    response_text = await response.text()
                    return response.status, response_text
            elif method == "POST":
                async with self.session.post(f"{self.base_url}{endpoint}", json=data) as response:
                    response_text = await response.text()
                    return response.status, response_text
                    
        except Exception as e:
            self.logger.error(f"Error en request {endpoint}: {str(e)}")
            return None, str(e)

    async def attack_attachment_controller(self):
        """Atacar específicamente SnAttachmentController que sabemos tiene ArrayList problemáticos"""
        self.logger.info("ATACANDO SnAttachmentController - ArrayList vulnerable")
        
        # Estos endpoints sabemos que usan ArrayList en líneas 175-176
        vulnerable_endpoints = [
            "/api/Attachment",
            "/api/attachments/test_id_1",
            "/api/attachments/test_id_2", 
            "/api/attachments/test_id_3"
        ]
        
        async def attack_worker():
            while True:
                tasks = []
                
                # Crear muchos requests simultáneos al mismo endpoint
                for _ in range(50):  # 50 requests simultáneos
                    endpoint = random.choice(vulnerable_endpoints)
                    tasks.append(self.make_request(endpoint, "GET"))
                
                # Ejecutar todos al mismo tiempo para forzar modificación concurrente
                results = await asyncio.gather(*tasks, return_exceptions=True)
                
                # Analizar respuestas
                for i, result in enumerate(results):
                    if isinstance(result, tuple):
                        status, content = result
                        await self.analyze_response(status, content, vulnerable_endpoints[i % len(vulnerable_endpoints)])
                
                await asyncio.sleep(0.1)  # Pausa muy corta
        
        # Lanzar múltiples workers
        workers = [asyncio.create_task(attack_worker()) for _ in range(20)]
        
        # Correr por 2 minutos
        await asyncio.sleep(120)
        
        for worker in workers:
            worker.cancel()

    async def attack_with_data_modification(self):
        """Intentar provocar modificaciones en listas mientras se iteran"""
        self.logger.info("ATACANDO con modificación de datos")
        
        async def data_modifier():
            while True:
                # Requests que podrían modificar datos
                modification_requests = [
                    ("/api/SysUser", "GET", {"query": "active=true"}),
                    ("/api/Incident", "GET", {"state": "1"}),
                    ("/api/ScRequest", "GET", {"active": "true"}),
                    ("/api/ScTask", "GET", {"state": "open"}),
                    ("/api/Company", "GET", {}),
                    ("/api/Location", "GET", {}),
                    ("/api/Department", "GET", {}),
                ]
                
                tasks = []
                for endpoint, method, params in modification_requests:
                    # Múltiples requests al mismo endpoint
                    for _ in range(30):
                        tasks.append(self.make_request(endpoint, method, params))
                
                results = await asyncio.gather(*tasks, return_exceptions=True)
                
                for result in results:
                    if isinstance(result, tuple):
                        status, content = result
                        await self.analyze_response(status, content, "data_modification")
                
                await asyncio.sleep(0.05)
        
        workers = [asyncio.create_task(data_modifier()) for _ in range(15)]
        await asyncio.sleep(120)
        
        for worker in workers:
            worker.cancel()

    async def attack_servicenow_endpoints(self):
        """Atacar endpoints ServiceNow que usan RestTemplate con colecciones"""
        self.logger.info("ATACANDO endpoints ServiceNow")
        
        async def servicenow_worker():
            while True:
                # Endpoints ServiceNow que sabemos existen
                endpoints = [
                    "/api/SysUser",
                    "/api/SysGroup", 
                    "/api/Incident",
                    "/api/ScRequest",
                    "/api/ScTask",
                    "/api/Attachment",
                    "/api/Journal",
                    "/api/Company"
                ]
                
                tasks = []
                
                # Crear storm de requests
                for endpoint in endpoints:
                    for _ in range(25):  # 25 requests por endpoint
                        # Agregar parámetros que podrían causar iteración sobre listas
                        params = {
                            "limit": random.randint(1, 100),
                            "offset": random.randint(0, 50),
                            "query": f"id={random.randint(1, 1000)}",
                            "fields": "sys_id,name,state,active",
                            "sysparm_query": f"active=true^state={random.randint(1,5)}"
                        }
                        tasks.append(self.make_request(endpoint, "GET", params))
                
                results = await asyncio.gather(*tasks, return_exceptions=True)
                
                for result in results:
                    if isinstance(result, tuple):
                        status, content = result
                        await self.analyze_response(status, content, "servicenow")
                
                await asyncio.sleep(0.1)
        
        workers = [asyncio.create_task(servicenow_worker()) for _ in range(25)]
        await asyncio.sleep(120)
        
        for worker in workers:
            worker.cancel()

    async def analyze_response(self, status, content, endpoint_info):
        """Analizar respuesta en busca de errores Java específicos"""
        if not content:
            return
            
        content_lower = content.lower()
        
        # Buscar patrones de errores Java
        java_error_patterns = [
            "concurrentmodificationexception",
            "java.util.concurrentmodificationexception", 
            "checkforcomodification",
            "arraylist$itr.checkforcomodification",
            "iterator",
            "java.lang.illegalstateexception",
            "basicauthenticationinterceptor",
            "authenticationinterceptor",
            "internal server error",
            "500",
            "error",
            "exception",
            "stack trace",
            "java.lang",
            "org.springframework",
            "at com.logicalis"
        ]
        
        found_errors = []
        for pattern in java_error_patterns:
            if pattern in content_lower:
                found_errors.append(pattern)
        
        if found_errors:
            self.errors_found += 1
            self.logger.error(f"ERRORES JAVA ENCONTRADOS en {endpoint_info}!")
            self.logger.error(f"Status: {status}")
            self.logger.error(f"Errores detectados: {found_errors}")
            self.logger.error(f"Contenido (primeros 1000 chars): {content[:1000]}")
            
            if len(content) > 1000:
                self.logger.error(f"... (truncado, total: {len(content)} caracteres)")
        
        elif status and status >= 500:
            self.logger.warning(f"Error HTTP {status} en {endpoint_info}")
            if len(content) > 0:
                self.logger.warning(f"Respuesta: {content[:500]}")

    async def run_comprehensive_attack(self):
        """Ejecutar todos los ataques simultáneamente"""
        self.logger.info("INICIANDO ATAQUE COMPREHENSIVO PARA FORZAR ERRORES JAVA")
        self.logger.info(f"Objetivo: {self.base_url}")
        
        start_time = time.time()
        
        # Lanzar todos los ataques simultáneamente
        attack_tasks = [
            asyncio.create_task(self.attack_attachment_controller()),
            asyncio.create_task(self.attack_with_data_modification()),
            asyncio.create_task(self.attack_servicenow_endpoints())
        ]
        
        try:
            await asyncio.gather(*attack_tasks, return_exceptions=True)
        except Exception as e:
            self.logger.error(f"Error durante ataque: {e}")
        
        end_time = time.time()
        duration = end_time - start_time
        
        self.logger.info("=" * 80)
        self.logger.info("RESULTADOS DEL ATAQUE PARA FORZAR ERRORES JAVA")
        self.logger.info("=" * 80)
        self.logger.info(f"Duración: {duration:.1f} segundos")
        self.logger.info(f"Total de requests: {self.total_requests}")
        self.logger.info(f"Errores Java encontrados: {self.errors_found}")
        
        if self.errors_found > 0:
            self.logger.error(f"EXITO: Se encontraron {self.errors_found} errores Java!")
            return 0
        else:
            self.logger.warning("No se encontraron errores Java específicos")
            self.logger.info("Revisa los logs del servidor Spring Boot para errores internos")
            return 1

async def main():
    parser = argparse.ArgumentParser(description="Forzar errores ConcurrentModificationException")
    parser.add_argument("--url", default="http://localhost:6050", help="URL del servidor Java")
    
    args = parser.parse_args()
    
    logger = setup_logging()
    
    logger.warning("OBJETIVO: Forzar ConcurrentModificationException y errores Java similares")
    logger.warning("ESTRATEGIA: Ataques simultáneos a endpoints con ArrayList vulnerables")
    
    try:
        async with JavaErrorForcer(args.url) as forcer:
            result = await forcer.run_comprehensive_attack()
            return result
                
    except KeyboardInterrupt:
        logger.info("Ataque interrumpido por usuario")
        return 1
    except Exception as e:
        logger.error(f"Error durante ejecución: {e}")
        return 1

if __name__ == "__main__":
    exit_code = asyncio.run(main())
    
    print("\n" + "="*60)
    print("INSTRUCCIONES PARA VERIFICAR ERRORES EN EL SERVIDOR:")
    print("="*60)
    print("1. Revisa los logs de tu aplicación Spring Boot")
    print("2. Busca en la consola donde está corriendo Spring Boot")
    print("3. Busca términos como:")
    print("   - ConcurrentModificationException")
    print("   - checkForComodification") 
    print("   - ArrayList$Itr")
    print("   - java.util.ConcurrentModificationException")
    print("4. Si no ves errores en consola, habilita logging debug:")
    print("   logging.level.com.logicalis=DEBUG en application.properties")
    print("="*60)
    
    sys.exit(exit_code)
