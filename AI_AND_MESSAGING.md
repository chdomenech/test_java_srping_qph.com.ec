# Documentación de IA y Mensajería

## Implementación de Inteligencia Artificial

### 1. Recomendaciones de Productos

**Endpoint**: `GET /api/ai/recommendations?customerId={id}`

**Algoritmo Implementado**:

1. **Análisis de Historial de Compras**:
   - Se obtienen todas las facturas del cliente
   - Se cuenta la frecuencia de compra de cada producto
   - Se identifican los productos más comprados

2. **Generación de Recomendaciones**:
   - Se seleccionan los top 3 productos más comprados del cliente
   - Para cada producto top, se buscan productos similares en rango de precio (±20%)
   - Se filtran productos con stock disponible
   - Se limitan a 5 recomendaciones máximo

3. **Razón de Recomendación**:
   - Si el cliente tiene historial: "Based on your purchase history"
   - Si no tiene historial: "Based on popular products"

**Ejemplo de Respuesta**:
```json
{
  "products": [
    {
      "id": 1,
      "code": "PROD001",
      "name": "Laptop Dell Inspiron",
      "price": 2500.00,
      "taxRate": 18.00,
      "stock": 10
    }
  ],
  "reason": "Based on your purchase history"
}
```

### 2. Detección de Anomalías

**Endpoint**: `GET /api/ai/anomaly-score?invoiceId={id}`

**Algoritmo Implementado**:

1. **Cálculo de Promedio Histórico**:
   - Se obtienen todas las facturas del cliente (excepto la actual)
   - Se calcula el promedio de montos totales
   - Se requiere al menos 2 facturas para el análisis

2. **Cálculo de Desviación**:
   - Se calcula la diferencia absoluta entre el monto actual y el promedio
   - Se calcula el porcentaje de desviación
   - Se normaliza a un score de 0-1 (donde 1 es máxima anomalía)

3. **Clasificación de Riesgo**:
   - **Score > 0.5**: Alta desviación - Alerta roja
   - **Score > 0.3**: Desviación moderada - Alerta amarilla
   - **Score ≤ 0.3**: Sin anomalías significativas

**Ejemplo de Respuesta**:
```json
{
  "score": 0.75,
  "explanation": "High deviation detected: Current amount (5000.00) is 100.0% different from average (2500.00)"
}
```

**Casos de Uso**:
- Detección de fraudes potenciales
- Identificación de errores de entrada
- Alertas para revisión manual

## Implementación de Mensajería (RabbitMQ)

### Arquitectura de Eventos

**Exchange**: `invoice.exchange` (Topic Exchange)
**Routing Key**: `invoice.created`

### Evento: InvoiceCreated

**Publicación Automática**:
- Se publica automáticamente al crear una factura
- Contiene toda la información relevante de la factura

**Estructura del Evento**:
```json
{
  "invoiceId": 1,
  "customerId": 1,
  "providerId": 1,
  "total": 2950.00,
  "issueDate": "2024-01-15T10:30:00",
  "items": [
    {
      "productId": 1,
      "productCode": "PROD001",
      "quantity": 2,
      "unitPrice": 2500.00,
      "lineTotal": 5900.00
    }
  ]
}
```

### Consumidores Implementados

#### 1. Stock Update Consumer

**Queue**: `stock.update.queue`

**Funcionalidad**:
- Escucha eventos de facturas creadas
- Actualiza el stock de productos automáticamente
- Resta la cantidad vendida del stock disponible
- Previene stock negativo (establece a 0 si es necesario)
- Registra logs de todas las actualizaciones

**Flujo**:
1. Recibe evento `InvoiceCreated`
2. Obtiene la factura de la base de datos
3. Para cada item de la factura:
   - Obtiene el producto
   - Calcula nuevo stock: `stock_actual - cantidad_vendida`
   - Actualiza el producto en la base de datos
4. Registra el resultado

**Manejo de Errores**:
- Si la factura no existe: lanza excepción
- Si el stock queda negativo: establece a 0 y registra warning
- Errores se registran en logs para debugging

#### 2. PDF Generation Consumer

**Queue**: `pdf.generation.queue`

**Funcionalidad**:
- Escucha eventos de facturas creadas
- Simula generación asíncrona de PDF
- En producción, podría generar y almacenar PDFs en S3/almacenamiento

**Flujo**:
1. Recibe evento `InvoiceCreated`
2. Registra inicio de generación
3. Simula procesamiento (en producción: generaría PDF real)
4. Registra finalización

**Extensión Futura**:
- Generación real de PDF con JasperReports
- Almacenamiento en sistema de archivos o S3
- Notificación al usuario cuando esté listo
- Cache de PDFs generados

### Configuración RabbitMQ

**Exchange Type**: Topic
- Permite routing flexible basado en patrones
- Facilita agregar nuevos consumidores sin modificar código existente

**Queues Durables**: Todas las colas son durables
- Sobreviven reinicios del servidor
- Garantizan entrega de mensajes

**Message Converter**: Jackson2JsonMessageConverter
- Serializa/deserializa objetos Java a JSON
- Facilita debugging y compatibilidad

### Ventajas de la Arquitectura

1. **Desacoplamiento**: Los servicios no dependen directamente entre sí
2. **Escalabilidad**: Fácil agregar nuevos consumidores
3. **Resiliencia**: Si un consumidor falla, el mensaje puede reintentarse
4. **Asincronía**: Operaciones pesadas no bloquean la creación de facturas
5. **Trazabilidad**: Todos los eventos se registran en logs

### Mejoras Futuras

1. **Dead Letter Queue**: Para mensajes que fallan múltiples veces
2. **Retry Policy**: Reintentos automáticos con backoff exponencial
3. **Idempotencia**: Prevenir procesamiento duplicado de eventos
4. **Event Sourcing**: Almacenar todos los eventos para auditoría
5. **Saga Pattern**: Para transacciones distribuidas complejas

## Integración Frontend

### Recomendaciones

- Se muestran en el formulario de factura cuando se selecciona un cliente
- Botones para agregar productos recomendados directamente
- Panel informativo con la razón de la recomendación

### Detección de Anomalías

- Se ejecuta automáticamente después de crear una factura
- Muestra alerta visual según el score:
  - Rojo: Score > 0.5 (alta anomalía)
  - Amarillo: Score > 0.3 (moderada)
  - Azul: Score ≤ 0.3 (normal)
- Permite al usuario decidir si proceder o cancelar

## Métricas y Observabilidad

### Logs Implementados

- Inicio y fin de procesamiento de eventos
- Actualizaciones de stock (antes/después)
- Errores y excepciones
- Warnings para casos especiales (stock negativo)

### Métricas Sugeridas (Futuro)

- Tiempo de procesamiento de eventos
- Tasa de éxito/fallo de consumidores
- Latencia de mensajes
- Tamaño de colas
- Throughput de eventos




