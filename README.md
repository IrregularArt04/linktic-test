## Justificacion de decisiones
1. Se utilizó PostgreSQL para priorizar el rendimiento y tiempos de respuesta en escenarios con consultas de alto volumen.

2. El endpoint de compras se establece en el microservicio de inventario ya que es el encargado de gestionar dicho apartado, por lo tanto es el mas indicado para gestionar esta labor al ser una operacion que afecta directamente el inventario.

3. Se utiliza un APIKEY estatico debido a que al ser una aplicacion mono usuario, no es necesario implementar un sistema de autenticación mas robusto como lo es JWT. En caso de haber múltiples usuarios con inicio de sesion incluido, se implementaría un sistema de autenticación mas robusto como el mencionado.

4. Se utiliza Antigravity como apoyo para la creacion de diagramas de interaccion y como copiloto de desarrollo, para mejorar la productividad y calidad del codigo. Tambien se agregan las skills "canvas-design", "frontend-design", "theme-factory" y "web-artifacts-builder"para mejorar la calidad visual y diseño de la aplicacion al igual que la experiencia del usuario.

5. Se implementaron pruebas unitarias y de integracion para asegurar la calidad y funcionalidad del codigo. 

6. se utliza docker y docker-compose para facilitar el despliegue de la aplicacion y sus dependencias creando 3 contenedores y una red para su ejecucion y comunicacion.

7. Se utiliza Swagger como documentacion de la API la cual se puede consultar en http://localhost:808X/swagger-ui.html en caso de ejecutar en debug.

8. Se utiliza lombok para disminuir las lineas de codigo en los getter y setter de las entidades y DTO.

9. Se utiliza pinia en el front para el manejo de estados, especialmente para el APIKEY y las url de los microservicios.

10. Se utiliza axios para la comunicacion entre el front y los microservicios.

11. En Application.properties de los microservicios se configura el APIKEY y en el caso del microservicio de inventario se añade la propiedad de url del servicio de productos para realizar la comunicacion.

## Endpoints de los Microservicios (Formato JSON)

A continuación se detallan los endpoints disponibles en los microservicios de **Productos** (puerto `8081`) e **Inventario** (puerto `8082`), incluyendo sus métodos HTTP, rutas, cabeceras de seguridad y payloads requeridos:

{
  "microservices": {
    "productos": {
      "port": 8081,
      "base_path": "/api/productos",
      "headers": {
        "X-API-KEY": "IJSKLs548dojklsfuxna884suzxHSU"
      },
      "endpoints": [
        {
          "method": "POST",
          "path": "/",
          "description": "Crea un nuevo producto en el catálogo maestro.",
          "request_body": {
            "codigoBarras": "string (max 25, alfanumérico, único)",
            "nombre": "string (max 150)",
            "precio": "number (máx. 10 enteros y 2 decimales)",
            "descripcion": "string (max 1000)"
          },
          "response": {
            "status": 201,
            "body": {
              "id": 1,
              "codigoBarras": "BARCODE12345",
              "nombre": "Detergente Líquido Premium",
              "precio": 12500.50,
              "descripcion": "Detalles del detergente"
            }
          }
        },
        {
          "method": "GET",
          "path": "/",
          "description": "Retorna una lista con todos los productos registrados en formato DTO.",
          "response": {
            "status": 200,
            "body": [
              {
                "id": 1,
                "codigoBarras": "BARCODE12345",
                "nombre": "Detergente Líquido Premium",
                "precio": 12500.50,
                "descripcion": "Detalles del detergente"
              }
            ]
          }
        },
        {
          "method": "GET",
          "path": "/{id}",
          "description": "Busca y retorna un producto por su ID único. Retorna 404 si no es encontrado.",
          "response": {
            "status": 200,
            "body": {
              "id": 1,
              "codigoBarras": "BARCODE12345",
              "nombre": "Detergente Líquido Premium",
              "precio": 12500.50,
              "descripcion": "Detalles del detergente"
            },
            "status_error": {
              "404": "Not Found"
            }
          }
        }
      ]
    },
    "inventario": {
      "port": 8082,
      "base_path": "/api/inventario",
      "headers": {
        "X-API-KEY": "IJSKLs548dojklsfuxna884suzxHSU"
      },
      "endpoints": [
        {
          "method": "GET",
          "path": "/{productoId}",
          "description": "Busca y retorna el registro de inventario (stock) asociado al ID del producto especificado. Retorna 404 si no existe.",
          "response": {
            "status": 200,
            "body": {
              "id": 10,
              "producto": 1,
              "cantidad": 25,
              "actualizadoEn": "2026-06-22T03:00:00"
            },
            "status_error": {
              "404": "Not Found (sin inventario registrado)"
            }
          }
        },
        {
          "method": "PUT",
          "path": "/{productoId}",
          "query_parameters": {
            "cantidad": "integer (cantidad total a fijar)"
          },
          "description": "Actualiza o inicializa el stock disponible del inventario para un producto específico.",
          "response": {
            "status": 200,
            "body": {
              "id": 10,
              "producto": 1,
              "cantidad": 50,
              "actualizadoEn": "2026-06-22T03:45:00"
            }
          }
        },
        {
          "method": "POST",
          "path": "/compra",
          "description": "Procesa el flujo de compra: valida existencia del producto, comprueba stock local, descuenta la cantidad y registra la compra.",
          "request_body": {
            "productoId": "long",
            "cantidad": "integer"
          },
          "response": {
            "status": 200,
            "body": "Compra procesada con éxito para el producto: Detergente Líquido Premium",
            "status_error": {
              "400": "Bad Request (stock insuficiente o producto no existe)"
            }
          }
        }
      ]
    }
  }
}
```

## Diagramas de Interacción (Mermaid)

A continuación se presentan los diagramas de secuencia que explican la comunicación entre la interfaz de usuario (`front-vue`) y los microservicios de **Productos** e **Inventario**:

<!-- DIAGRAMA 1: Registro de Producto (InsertProducts) -->
### 1. Registro de Producto (InsertProducts)
Este diagrama describe el flujo de creación de un nuevo producto en el catálogo maestro a través del formulario `InsertProducts.vue`. El microservicio de productos valida la API Key mediante el filtro de seguridad y registra el producto autogenerando el ID en la base de datos PostgreSQL.

```mermaid
sequenceDiagram
    autonumber
    actor Usuario
    participant Form as InsertProducts.vue (Puerto 5173/5174)
    participant MicroProd as MS Productos (Puerto 8081)
    database DB as Base de Datos (PostgreSQL)

    Usuario->>Form: Diligencia campos (Código de Barras, Nombre, Precio, Descripción)
    Usuario->>Form: Envía el formulario (Submit)
    
    Note over Form: Valida campos localmente<br/>(caracteres válidos, longitud, formato de precio)
    
    Form->>MicroProd: POST /api/productos (Header: X-API-KEY, Body: ProductoDTO)
    activate MicroProd
    
    Note over MicroProd: ApiKeyFilter intercepta petición<br/>y valida encabezado X-API-KEY
    
    MicroProd->>DB: INSERT INTO productos (codigo_barras, nombre, precio, descripcion, creado_en)
    DB-->>MicroProd: Fila registrada con ID autogenerado
    
    MicroProd-->>Form: JSON con ProductoDTO creado (201 Created)
    deactivate MicroProd
    
    Form-->>Usuario: Muestra banner Toast verde de éxito (con detalles del producto) y limpia el formulario
```

<!-- DIAGRAMA 2: Consulta de Catálogo y Detalle de Stock -->
### 2. Consulta de Catálogo y Detalle de Stock
Este diagrama detalla cómo el frontend carga el listado de productos de forma segura y cómo solicita la información detallada del catálogo y del inventario (stock) al abrir la ventana modal de gestión.

```mermaid
sequenceDiagram
    autonumber
    actor Usuario
    participant FrontVue as Aplicación Vue (Puerto 5173/5174)
    participant MicroProd as MS Productos (Puerto 8081)
    participant MicroInv as MS Inventario (Puerto 8082)
    database DB as Base de Datos (PostgreSQL)

    %% Carga del listado principal
    Usuario->>FrontVue: Accede a la pestaña "Ver Inventario"
    FrontVue->>MicroProd: GET /api/productos (Header: X-API-KEY)
    MicroProd->>DB: Consultar todos los productos
    DB-->>MicroProd: Lista de productos
    MicroProd-->>FrontVue: JSON Array con productos (200 OK)
    FrontVue-->>Usuario: Muestra la tabla del catálogo maestro

    %% Apertura del modal
    Usuario->>FrontVue: Clic en botón "Ver Stock" de un producto
    activate FrontVue
    FrontVue->>MicroProd: GET /api/productos/{id} (Header: X-API-KEY)
    MicroProd->>DB: Consultar producto por ID
    DB-->>MicroProd: Fila de producto
    MicroProd-->>FrontVue: JSON Producto (200 OK)
    
    FrontVue->>MicroInv: GET /api/inventario/{productId} (Header: X-API-KEY)
    alt Registro de inventario existe
        MicroInv->>DB: Consultar stock por producto_id
        DB-->>MicroInv: Fila de inventario
        MicroInv-->>FrontVue: JSON Inventario (200 OK)
    else Registro no existe (404)
        MicroInv->>DB: Consultar stock por producto_id (No encontrado)
        MicroInv-->>FrontVue: 404 Not Found (Bypass a stock = 0 en Front)
    end
    deactivate FrontVue
    FrontVue-->>Usuario: Abre el modal y muestra info del producto + stock actual
```

<!-- DIAGRAMA 3: Actualización de Stock (Fijar Cantidad) -->
### 3. Actualización de Stock (Fijar Cantidad)
Este flujo representa la acción de fijar directamente una nueva cantidad de inventario para un producto específico desde el modal.

```mermaid
sequenceDiagram
    autonumber
    actor Usuario
    participant FrontVue as Aplicación Vue (Puerto 5173/5174)
    participant MicroInv as MS Inventario (Puerto 8082)
    database DB as Base de Datos (PostgreSQL)

    Usuario->>FrontVue: Modifica el stock e introduce la nueva cantidad
    Usuario->>FrontVue: Clic en "Fijar Cantidad"
    FrontVue->>MicroInv: PUT /api/inventario/{productId}?cantidad={cantidad} (Header: X-API-KEY)
    alt Registro de inventario existe
        MicroInv->>DB: UPDATE inventarios SET cantidad = X, actualizado_en = NOW() WHERE producto_id = id
        DB-->>MicroInv: Confirmación de actualización
    else Registro de inventario no existe
        MicroInv->>DB: INSERT INTO inventarios (producto_id, cantidad, actualizado_en) VALUES (id, X, NOW())
        DB-->>MicroInv: Confirmación de inserción
    end
    MicroInv-->>FrontVue: JSON con InventarioEntity actualizado (200 OK)
    FrontVue-->>Usuario: Muestra notificación de éxito y refresca el stock en pantalla
```

<!-- DIAGRAMA 4: Simulación de Venta / Compra de Producto -->
### 4. Simulación de Venta / Compra de Producto
Este diagrama de interacción describe el flujo orquestado de compra. El microservicio de inventario valida los datos de forma síncrona consultando al microservicio de productos antes de realizar las transacciones en su base de datos local.

```mermaid
sequenceDiagram
    autonumber
    actor Usuario
    participant FrontVue as Aplicación Vue (Puerto 5173/5174)
    participant MicroInv as MS Inventario (Puerto 8082)
    participant MicroProd as MS Productos (Puerto 8081)
    database DB as Base de Datos (PostgreSQL)

    Usuario->>FrontVue: Especifica la cantidad a comprar y da clic en "Comprar / Vender"
    FrontVue->>MicroInv: POST /api/inventario/compra (Header: X-API-KEY, Body: {productoId, cantidad})
    activate MicroInv
    
    %% Comunicación inter-servicio
    Note over MicroInv,MicroProd: Comunicación Síncrona via WebClient (con Retry & Timeout)
    MicroInv->>MicroProd: GET /api/productos/{productoId} (Header: X-API-KEY)
    MicroProd->>DB: Consultar existencia y precio del producto
    DB-->>MicroProd: Datos del producto
    MicroProd-->>MicroInv: JSON ProductoDTO con precio (200 OK)

    alt Producto no existe en catálogo maestro
        Note over MicroInv: Lanza excepción (Producto no existe)
        MicroInv-->>FrontVue: 400 Bad Request ("El producto no existe en el catálogo maestro")
    else Producto existe
        MicroInv->>DB: Consultar stock local del producto
        DB-->>MicroInv: Registro de inventario actual

        alt Stock local es insuficiente
            Note over MicroInv: Lanza excepción (Stock insuficiente)
            MicroInv-->>FrontVue: 400 Bad Request ("Stock insuficiente para realizar la compra.")
        else Stock disponible
            %% Descontar e insertar compra
            MicroInv->>DB: UPDATE inventarios SET cantidad = cantidad - X WHERE producto_id = id
            MicroInv->>DB: INSERT INTO compras (idproducto, cantidad_comprada, precio_unitario) VALUES (id, X, precio_maestro)
            DB-->>MicroInv: Confirmación de transacción
            MicroInv-->>FrontVue: String ("Compra procesada con éxito...") (200 OK)
            deactivate MicroInv
            FrontVue-->>Usuario: Muestra alerta de éxito de compra y descuenta el stock
        end
    end
```


