# DefaultApi

All URIs are relative to *http://localhost:8080/api*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**solicitudesGet**](DefaultApi.md#solicitudesGet) | **GET** /solicitudes | Listar solicitudes |
| [**solicitudesIdAsignacionPatch**](DefaultApi.md#solicitudesIdAsignacionPatch) | **PATCH** /solicitudes/{id}/asignacion | Asignar responsable administrativo |
| [**solicitudesIdClasificacionPatch**](DefaultApi.md#solicitudesIdClasificacionPatch) | **PATCH** /solicitudes/{id}/clasificacion | Clasificar solicitud |
| [**solicitudesIdEstadoPatch**](DefaultApi.md#solicitudesIdEstadoPatch) | **PATCH** /solicitudes/{id}/estado | Actualizar estado de la solicitud (atención/cierre) |
| [**solicitudesIdGet**](DefaultApi.md#solicitudesIdGet) | **GET** /solicitudes/{id} | Obtener solicitud por ID |
| [**solicitudesIdHistorialGet**](DefaultApi.md#solicitudesIdHistorialGet) | **GET** /solicitudes/{id}/historial | Consultar historial de la solicitud |
| [**solicitudesIdPrioridadPatch**](DefaultApi.md#solicitudesIdPrioridadPatch) | **PATCH** /solicitudes/{id}/prioridad | Priorizar solicitud |
| [**solicitudesPost**](DefaultApi.md#solicitudesPost) | **POST** /solicitudes | Registrar una nueva solicitud |


<a id="solicitudesGet"></a>
# **solicitudesGet**
> List&lt;Solicitud&gt; solicitudesGet()

Listar solicitudes

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    try {
      List<Solicitud> result = apiInstance.solicitudesGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#solicitudesGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;Solicitud&gt;**](Solicitud.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Lista de solicitudes |  -  |
| **400** | Parámetros inválidos |  -  |
| **403** | No autorizado |  -  |
| **404** | Recurso no encontrado |  -  |

<a id="solicitudesIdAsignacionPatch"></a>
# **solicitudesIdAsignacionPatch**
> Solicitud solicitudesIdAsignacionPatch(id, asignacionRequest)

Asignar responsable administrativo

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Integer id = 56; // Integer | 
    AsignacionRequest asignacionRequest = new AsignacionRequest(); // AsignacionRequest | 
    try {
      Solicitud result = apiInstance.solicitudesIdAsignacionPatch(id, asignacionRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#solicitudesIdAsignacionPatch");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Integer**|  | |
| **asignacionRequest** | [**AsignacionRequest**](AsignacionRequest.md)|  | |

### Return type

[**Solicitud**](Solicitud.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Responsable asignado |  -  |
| **400** | Datos inválidos |  -  |
| **403** | No autorizado |  -  |
| **404** | Solicitud o responsable no encontrado |  -  |

<a id="solicitudesIdClasificacionPatch"></a>
# **solicitudesIdClasificacionPatch**
> Solicitud solicitudesIdClasificacionPatch(id, clasificacionRequest)

Clasificar solicitud

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Integer id = 56; // Integer | 
    ClasificacionRequest clasificacionRequest = new ClasificacionRequest(); // ClasificacionRequest | 
    try {
      Solicitud result = apiInstance.solicitudesIdClasificacionPatch(id, clasificacionRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#solicitudesIdClasificacionPatch");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Integer**|  | |
| **clasificacionRequest** | [**ClasificacionRequest**](ClasificacionRequest.md)|  | |

### Return type

[**Solicitud**](Solicitud.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Solicitud clasificada |  -  |
| **400** | Datos inválidos |  -  |
| **403** | No autorizado |  -  |
| **404** | Solicitud no encontrada |  -  |

<a id="solicitudesIdEstadoPatch"></a>
# **solicitudesIdEstadoPatch**
> Solicitud solicitudesIdEstadoPatch(id, cambioEstadoRequest)

Actualizar estado de la solicitud (atención/cierre)

Permite gestionar la solicitud y realizar su cierre cambiando el estado.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Integer id = 56; // Integer | 
    CambioEstadoRequest cambioEstadoRequest = new CambioEstadoRequest(); // CambioEstadoRequest | 
    try {
      Solicitud result = apiInstance.solicitudesIdEstadoPatch(id, cambioEstadoRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#solicitudesIdEstadoPatch");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Integer**|  | |
| **cambioEstadoRequest** | [**CambioEstadoRequest**](CambioEstadoRequest.md)|  | |

### Return type

[**Solicitud**](Solicitud.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Estado actualizado |  -  |
| **400** | Transición inválida o datos incorrectos |  -  |
| **403** | No autorizado |  -  |
| **404** | Solicitud no encontrada |  -  |

<a id="solicitudesIdGet"></a>
# **solicitudesIdGet**
> Solicitud solicitudesIdGet(id)

Obtener solicitud por ID

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Integer id = 56; // Integer | 
    try {
      Solicitud result = apiInstance.solicitudesIdGet(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#solicitudesIdGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Integer**|  | |

### Return type

[**Solicitud**](Solicitud.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Solicitud encontrada |  -  |
| **400** | ID inválido |  -  |
| **403** | No autorizado |  -  |
| **404** | Solicitud no encontrada |  -  |

<a id="solicitudesIdHistorialGet"></a>
# **solicitudesIdHistorialGet**
> List&lt;HistorialMovimiento&gt; solicitudesIdHistorialGet(id)

Consultar historial de la solicitud

Retorna la trazabilidad (quién, cuándo y qué acción se realizó).

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Integer id = 56; // Integer | 
    try {
      List<HistorialMovimiento> result = apiInstance.solicitudesIdHistorialGet(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#solicitudesIdHistorialGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Integer**|  | |

### Return type

[**List&lt;HistorialMovimiento&gt;**](HistorialMovimiento.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Historial obtenido |  -  |
| **400** | ID inválido |  -  |
| **403** | No autorizado |  -  |
| **404** | Solicitud no encontrada |  -  |

<a id="solicitudesIdPrioridadPatch"></a>
# **solicitudesIdPrioridadPatch**
> Solicitud solicitudesIdPrioridadPatch(id, prioridadRequest)

Priorizar solicitud

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Integer id = 56; // Integer | 
    PrioridadRequest prioridadRequest = new PrioridadRequest(); // PrioridadRequest | 
    try {
      Solicitud result = apiInstance.solicitudesIdPrioridadPatch(id, prioridadRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#solicitudesIdPrioridadPatch");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Integer**|  | |
| **prioridadRequest** | [**PrioridadRequest**](PrioridadRequest.md)|  | |

### Return type

[**Solicitud**](Solicitud.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Prioridad asignada |  -  |
| **400** | Datos inválidos |  -  |
| **403** | No autorizado |  -  |
| **404** | Solicitud no encontrada |  -  |

<a id="solicitudesPost"></a>
# **solicitudesPost**
> Solicitud solicitudesPost(solicitudCreate)

Registrar una nueva solicitud

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    SolicitudCreate solicitudCreate = new SolicitudCreate(); // SolicitudCreate | 
    try {
      Solicitud result = apiInstance.solicitudesPost(solicitudCreate);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#solicitudesPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **solicitudCreate** | [**SolicitudCreate**](SolicitudCreate.md)|  | |

### Return type

[**Solicitud**](Solicitud.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Solicitud creada exitosamente |  -  |
| **400** | Datos inválidos |  -  |
| **403** | No autorizado |  -  |
| **404** | Recurso no encontrado |  -  |

