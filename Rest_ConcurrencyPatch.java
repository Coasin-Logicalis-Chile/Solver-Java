/**
 * PARCHE DE CONCURRENCIA PARA Rest.java
 * 
 * INSTRUCCIONES DE APLICACIÓN:
 * 1. Reemplazar el método restTemplateServiceNow() en Rest.java
 * 2. Cambiar todas las referencias "this.restTemplate.getInterceptors().add(...)" 
 *    por "RestTemplate safeRestTemplate = restTemplateServiceNow();"
 * 
 * Autor: Ivan Hills
 * Fecha: Agosto 2025
 */

// MÉTODO ORIGINAL (PROBLEMÁTICO):
/*
public RestTemplate restTemplateServiceNow() {
    this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));
    return restTemplate;
}
*/

// ✅ MÉTODO CORREGIDO (THREAD-SAFE):
public RestTemplate restTemplateServiceNow() {
    // Crear nueva instancia para este hilo/request (thread-safe)
    RestTemplate threadSafeRestTemplate = new RestTemplate();
    
    // Copiar configuración de la plantilla base
    threadSafeRestTemplate.setRequestFactory(baseRestTemplate.getRequestFactory());
    threadSafeRestTemplate.setMessageConverters(baseRestTemplate.getMessageConverters());
    threadSafeRestTemplate.setErrorHandler(baseRestTemplate.getErrorHandler());
    
    // Agregar interceptor a la NUEVA instancia (thread-safe)
    threadSafeRestTemplate.getInterceptors().add(
        new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword())
    );
    
    return threadSafeRestTemplate;
}

// EJEMPLO DE CORRECCIÓN EN OTROS MÉTODOS:
// 
// ANTES (PROBLEMÁTICO):
// this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(App.SNUser(), App.SNPassword()));
// ResponseEntity<String> response = restTemplate.getForEntity(endPoint, String.class);
//
// DESPUÉS (CORREGIDO):
// RestTemplate safeRestTemplate = restTemplateServiceNow();
// ResponseEntity<String> response = safeRestTemplate.getForEntity(endPoint, String.class);

/*
 * LÍNEAS ESPECÍFICAS QUE DEBEN CAMBIARSE EN Rest.java:
 * 
 * Línea 59:  this.restTemplate.getInterceptors().add(...) -> RestTemplate safeRestTemplate = restTemplateServiceNow();
 * Línea 65:  this.restTemplate.getInterceptors().add(...) -> RestTemplate safeRestTemplate = restTemplateServiceNow();
 * Línea 81:  restTemplate.getInterceptors().add(...) -> RestTemplate safeRestTemplate = restTemplateServiceNow();
 * Línea 100: restTemplate.getInterceptors().add(...) -> RestTemplate safeRestTemplate = restTemplateServiceNow();
 * Línea 144: restTemplate.getInterceptors().add(...) -> RestTemplate safeRestTemplate = restTemplateServiceNow();
 * Línea 306: restTemplate.getInterceptors().add(...) -> RestTemplate safeRestTemplate = restTemplateServiceNow();
 * Línea 386: restTemplate.getInterceptors().add(...) -> RestTemplate safeRestTemplate = restTemplateServiceNow();
 * Línea 414: restTemplate.getInterceptors().add(...) -> RestTemplate safeRestTemplate = restTemplateServiceNow();
 * Línea 451: restTemplate.getInterceptors().add(...) -> RestTemplate safeRestTemplate = restTemplateServiceNow();
 * Línea 485: restTemplate.getInterceptors().add(...) -> RestTemplate safeRestTemplate = restTemplateServiceNow();
 * 
 * Y cambiar todas las referencias subsecuentes de "restTemplate" por "safeRestTemplate" 
 * en esos mismos métodos.
 */
