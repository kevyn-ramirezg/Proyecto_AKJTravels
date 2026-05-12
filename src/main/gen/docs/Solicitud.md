

# Solicitud


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **Integer** |  |  |
|**solicitanteId** | **String** |  |  |
|**descripcion** | **String** |  |  |
|**origen** | [**OrigenEnum**](#OrigenEnum) |  |  |
|**tipo** | [**TipoEnum**](#TipoEnum) |  |  [optional] |
|**prioridad** | [**PrioridadEnum**](#PrioridadEnum) |  |  [optional] |
|**estado** | [**EstadoEnum**](#EstadoEnum) |  |  |
|**responsableId** | **Integer** |  |  [optional] |
|**fechaRegistro** | **OffsetDateTime** |  |  |



## Enum: OrigenEnum

| Name | Value |
|---- | -----|
| WEB | &quot;WEB&quot; |
| CORREO | &quot;CORREO&quot; |
| PRESENCIAL | &quot;PRESENCIAL&quot; |



## Enum: TipoEnum

| Name | Value |
|---- | -----|
| HOMOLOGACION | &quot;HOMOLOGACION&quot; |
| CUPO | &quot;CUPO&quot; |
| CANCELACION | &quot;CANCELACION&quot; |



## Enum: PrioridadEnum

| Name | Value |
|---- | -----|
| ALTA | &quot;ALTA&quot; |
| MEDIA | &quot;MEDIA&quot; |
| BAJA | &quot;BAJA&quot; |



## Enum: EstadoEnum

| Name | Value |
|---- | -----|
| REGISTRADA | &quot;REGISTRADA&quot; |
| CLASIFICADA | &quot;CLASIFICADA&quot; |
| PRIORIZADA | &quot;PRIORIZADA&quot; |
| ASIGNADA | &quot;ASIGNADA&quot; |
| EN_GESTION | &quot;EN_GESTION&quot; |
| CERRADA | &quot;CERRADA&quot; |
| RECHAZADA | &quot;RECHAZADA&quot; |



