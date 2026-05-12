

# HistorialMovimiento


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **Integer** |  |  |
|**solicitudId** | **Integer** |  |  |
|**accion** | [**AccionEnum**](#AccionEnum) |  |  |
|**realizadoPor** | **String** | Quién realizó la acción |  |
|**fecha** | **OffsetDateTime** | Cuándo se realizó |  |
|**detalle** | **String** | Qué acción se realizó / observación |  [optional] |



## Enum: AccionEnum

| Name | Value |
|---- | -----|
| REGISTRO | &quot;REGISTRO&quot; |
| CLASIFICACION | &quot;CLASIFICACION&quot; |
| PRIORIZACION | &quot;PRIORIZACION&quot; |
| ASIGNACION | &quot;ASIGNACION&quot; |
| CAMBIO_ESTADO | &quot;CAMBIO_ESTADO&quot; |
| CIERRE | &quot;CIERRE&quot; |



