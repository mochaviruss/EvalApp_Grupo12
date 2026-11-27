# Documentación técnica – EvalApp

## Contexto

Una escuela de formación (caso anonimizado) evalúa a sus alumnos con rúbricas en papel, muchas veces en terreno. EvalApp permite que el docente o instructor aplique la rúbrica desde el teléfono: elige el curso, el alumno o el grupo, marca el nivel de desempeño de cada criterio y la app calcula la nota. Los alumnos, las rúbricas y las evaluaciones se guardan en un microservicio propio.

## Arquitectura

```
                 ┌──────────── Retrofit ────────────┐
                 │                                  │
App Android (Compose + MVVM)                 Microservicio Kotlin + Spring Boot  ──  Base de datos H2
     │                                       (alumnos, rúbricas, evaluaciones)
     ├── SQLite en el teléfono (solo usuarios y documentos)
     └── Retrofit ──> API externa Open-Meteo (clima)
```

### App: MVVM

- **View** (`ui/screens`, `ui/components`): pantallas en Compose. Solo muestran el estado y llaman funciones del ViewModel.
- **ViewModel** (`viewmodel`): guarda el estado en un `MutableStateFlow` y lo muestra como `StateFlow`. Valida los datos y llama al Repository con `viewModelScope.launch`. Se crea con su `Factory` para entregarle el Repository.
- **Repository** (`data`): el ViewModel le pide los datos y el Repository los trae. `AlumnoRepository`, `RubricaRepository` y `EvaluacionRepository` usan `ApiService` (microservicio). `UsuarioRepository` usa `UsuarioDao` (SQLite).
- **ApiService / DAO** (`data`): `ApiService` y `ClimaApiService` son las interfaces de Retrofit y `RetrofitClient` las crea. `UsuarioDao` es la interfaz para los usuarios y `UsuarioDaoSQLite` la implementa con SQLite.
- **Model** (`model`): data class de los datos y los UiState de cada pantalla.
- **Logic** (`logic`): funciones puras que no dependen de Android (nota, puntaje por nivel, clima y validaciones). Por eso se pueden probar con JUnit.

### Paquetes de la app

| Paquete | Qué tiene |
|---|---|
| `data` | `ApiService`, `ClimaApiService`, `RetrofitClient`, los Repository, `UsuarioDao`, `UsuarioDaoSQLite`, `BaseDatos` (SQLite) y `DocumentoRepository` |
| `logic` | `CalculadoraNota`, `DescripcionClima`, `Validaciones` |
| `model` | `Alumno`, `Rubrica`, `Criterio`, `Nivel`, `Evaluacion`, `CriterioEvaluado`, `Clima` y los UiState |
| `navigation` | `AppNavigation` con todas las rutas y los ViewModel |
| `ui/screens` | Una pantalla por archivo |
| `ui/components` | `MenuApp` (menú lateral, barra de arriba y de abajo), `EvaluacionCard`, `DocumentoCard` |
| `viewmodel` | `AlumnoViewModel`, `RubricaViewModel`, `EvaluarViewModel`, `EvaluacionViewModel`, `ClimaViewModel` y los de usuario y documentos |

### Microservicio

Está en su propio repositorio (`EvalApp_Microservicio_Grupo12`). Está hecho en Kotlin, igual que la app, con Spring Boot 3.5, Spring Data JPA, validación y base de datos H2 en archivo. Se construye con Gradle (`build.gradle.kts`, Gradle 8.14) y se abre en Android Studio igual que la app. Se ejecuta con la tarea `bootRun`.

Paquetes del microservicio (`src/main/kotlin/cl/evalapp/microservicio`):

| Paquete | Qué tiene |
|---|---|
| `model` | Entidades JPA `Alumno`, `Rubrica`, `Evaluacion` y las clases `@Embeddable` `Criterio`, `Nivel`, `CriterioEvaluado` |
| `repository` | Interfaces `JpaRepository` de cada entidad |
| `service` | Lógica: listar, buscar, crear, actualizar y eliminar |
| `controller` | `@RestController` con los endpoints `/api/alumnos`, `/api/rubricas` y `/api/evaluaciones` |
| `config` | `DatosIniciales`: crea los datos de ejemplo la primera vez |

Las listas de criterios y niveles se guardan con `@ElementCollection` en tablas aparte (`rubrica_criterios`, `rubrica_niveles`, `evaluacion_criterios`, `evaluacion_niveles`). `@OrderColumn` guarda el orden.

## Navegación

Barra de abajo: Inicio, Evaluar, Historial, Rúbricas, Alumnos. En el menú lateral también están Mis documentos, Cargar documento, Editar mis datos y Cerrar sesión.

Rutas: `bienvenida`, `login`, `registro`, `inicio`, `evaluar`, `aplicar_rubrica`, `historial`, `detalle_evaluacion`, `rubricas`, `editar_rubrica`, `alumnos`, `editar_usuario`, `documentos`, `cargar`, `visor`, `cerrar_sesion`.

## Cómo se evalúa a un alumno

1. En Inicio se toca **Evaluar alumno**. `EvaluarViewModel.nuevaEvaluacion()` deja el formulario vacío.
2. En **EvaluarScreen** se elige el curso (`seleccionarCurso`), se marcan uno o varios alumnos (`cambiarSeleccionAlumno`) y se elige la rúbrica (`seleccionarRubrica`). Al elegir la rúbrica se copian sus criterios y niveles a la evaluación, todos sin nivel.
3. **Aplicar rúbrica** revisa con `validarSeleccion()` que haya alumnos y rúbrica, y abre **AplicarRubricaScreen**.
4. En cada criterio se elige un nivel. `cambiarNivel()` calcula los puntos con `CalculadoraNota.calcularPuntajeCriterio(porcentaje, puntajeMaximo)` y `calcularResultado()` suma los puntos y saca la nota al tiro.
5. Cada criterio tiene su comentario (`cambiarComentario`) y abajo está el comentario general (`cambiarRetroalimentacion`).
6. El botón Enviar se activa cuando `criteriosEvaluados` es igual a la cantidad de criterios. `enviar()` valida otra vez y crea una `Evaluacion` por cada alumno elegido. Si es un grupo, el campo `grupo` guarda los nombres de todos.
7. `EvaluacionRepository.guardar()` envía cada evaluación al microservicio: si es nueva (`id = 0`) usa `crearEvaluacion` (POST) y si se está editando usa `actualizarEvaluacion` (PUT).
8. Se muestra un Toast con el resultado y se vuelve al Inicio. Si el microservicio no responde, se muestra el error y no se pierde lo evaluado.

## Persistencia local y remota

| Dato | Dónde se guarda | Cómo |
|---|---|---|
| Alumnos | Microservicio (H2) | `ApiService` con Retrofit: GET, POST, PUT y DELETE |
| Rúbricas | Microservicio (H2) | `ApiService` con Retrofit: GET, POST, PUT y DELETE |
| Evaluaciones | Microservicio (H2) | `ApiService` con Retrofit: GET, POST, PUT y DELETE |
| Usuarios y documentos | SQLite en el teléfono | `BaseDatos` (`SQLiteOpenHelper`) |

- Cada pantalla pide los datos al entrar (`LaunchedEffect`) y después de crear, editar o eliminar vuelve a pedir la lista, así siempre muestra lo que tiene el microservicio.
- Si el microservicio no responde, el ViewModel atrapa el error con `try/catch` y la pantalla muestra "Revisa que el microservicio esté encendido".
- Cada evaluación guarda una copia de los criterios y niveles de la rúbrica. Si después se edita o elimina la rúbrica, las evaluaciones hechas no cambian.

Tablas SQLite (`evalapp.db`, versión 2):

| Tabla | Columnas |
|---|---|
| usuario | id, nombre, correo, clave, rol |
| documento | id, usuario_id, titulo, tipo, uri |

## API externa vs microservicio

| | API externa (Open-Meteo) | Microservicio propio |
|---|---|---|
| Quién la hizo | Otra empresa | Nosotros, en Spring Boot |
| Qué hace la app | Solo lee (GET) | Lee, crea, actualiza y elimina (CRUD) |
| Datos | Clima, no se guardan | Alumnos, rúbricas y evaluaciones en H2 |
| Interfaz Retrofit | `ClimaApiService` | `ApiService` |
| URL | `https://api.open-meteo.com/` | `http://10.0.2.2:8080/` |
| Si falla | La tarjeta del clima muestra el error y Reintentar; el resto de la app sigue igual | La pantalla muestra el error y se puede reintentar |

`RetrofitClient` crea un `Retrofit` para cada URL con `GsonConverterFactory`, que convierte el JSON en objetos Kotlin. Las funciones de las interfaces son `suspend`, por eso se llaman dentro de corrutinas.

## Niveles de desempeño y nota

Cada rúbrica tiene sus niveles. Los de ejemplo:

| Nivel | Porcentaje | Con un criterio de 5 pts |
|---|---|---|
| Muy buen desempeño | 100% | 5 |
| Logrado | 80% | 4 |
| Aceptable | 60% | 3 |
| No logrado | 0% | 0 |

Puntos del criterio = puntaje máximo × porcentaje / 100, redondeado.

Nota de 1.0 a 7.0 con 60% de exigencia (`CalculadoraNota`):
- Si el puntaje es menor al 60% del máximo: `nota = 1 + 3 × puntaje / (0.6 × máximo)`
- Si es igual o mayor: `nota = 4 + 3 × (puntaje − 0.6 × máximo) / (0.4 × máximo)`
- Se redondea a un decimal. Se aprueba con 4.0.

Ejemplo con 20 puntos máximos: 20 pts = 7.0, 17 pts = 5.9, 15 pts = 5.1, 12 pts = 4.0, 10 pts = 3.5.

Reglas al guardar una rúbrica: nombre de 3 letras o más, asignatura, cada criterio con nombre y puntaje mayor a 0, niveles con nombre sin repetir, porcentajes entre 0 y 100 y un nivel con 100%. No se puede quitar un nivel si quedan solo 2.

## Pruebas unitarias

### App (`app/src/test`, JUnit 4)

Qué se simula y cómo: igual que el `FakeProductoDao` del ejemplo de clases, se usan clases falsas que solo existen en `test`. En vez del microservicio se usa `FakeApiService` (guarda todo en mapas y con `servidorApagado = true` lanza `IOException`), en vez de SQLite `FakeUsuarioDao` y en vez de Open-Meteo `FakeClimaApiService`. Así las pruebas no necesitan Android, internet ni el microservicio. Los ViewModel usan `viewModelScope`, que corre en el hilo principal de Android; en las pruebas ese hilo no existe, por eso se reemplaza con `Dispatchers.setMain(UnconfinedTestDispatcher())`.

| Archivo | Qué prueba |
|---|---|
| EvaluarViewModelTest | elegir curso, alumnos y rúbrica, puntos por nivel, nota en vivo, comentarios, no enviar si faltan criterios, enviar individual y en grupo, error del servidor, editar |
| EvaluacionViewModelTest | cargar, actualizar, error del servidor, detalle y eliminar |
| RubricaViewModelTest | crear y editar, agregar y quitar criterios y niveles, todas las validaciones, eliminar, error del servidor |
| AlumnoViewModelTest | cursos, validaciones, registrar, editar, eliminar, error del servidor y reintentar |
| AlumnoRepositoryTest / RubricaRepositoryTest / EvaluacionRepositoryTest | listar, POST para nuevos, PUT para existentes, eliminar y error del servidor |
| LoginViewModelTest / RegistroViewModelTest / EditarUsuarioViewModelTest | validaciones de cada formulario, correo repetido, iniciar sesión, registrar y editar |
| UsuarioRepositoryTest | correo en minúsculas y sin espacios, buscar, registrar y actualizar |
| ClimaViewModelTest / ClimaRepositoryTest | carga del clima, error y reintento, conversión de la respuesta |
| CalculadoraNotaTest | fórmula de la nota, porcentaje, puntaje por nivel y errores |
| DescripcionClimaTest | códigos del clima y si es apto para terreno |
| ValidacionesTest | reglas de los formularios de usuario y documentos |
| SesionViewModelTest | iniciar, actualizar y cerrar sesión |
| FactoryTest | cada `Factory` crea su ViewModel y rechaza otros |

Cobertura (Run with Coverage): `logic` y los Repository quedan al 100%, y los ViewModel de alumnos, rúbricas, evaluaciones, clima y usuarios sobre el 95%. Lo que no tiene prueba unitaria son las clases que necesitan el teléfono: `BaseDatos`, `UsuarioDaoSQLite`, `DocumentoRepository`, `RetrofitClient`, las pantallas y los ViewModel de documentos (usan archivos de Android). Esas se prueban ejecutando la app.

Por qué ayudan: si alguien cambia la fórmula de la nota, una validación o cómo se envían los datos, la prueba falla antes de que el error llegue al docente. Eso da estabilidad (lo que funcionaba sigue funcionando), confiabilidad (las notas se calculan siempre igual) y seguridad (no se envían evaluaciones incompletas ni datos inválidos).

### Microservicio (`src/test/kotlin`, JUnit 5 + MockK)

`AlumnoServiceTest`, `RubricaServiceTest` y `EvaluacionServiceTest` prueban listar, buscar, crear, actualizar y eliminar. El `JpaRepository` se simula con `mockk<AlumnoRepository>()` y se le dice qué responder con `every { repository.existsById(5L) } returns true`. Con `verify(exactly = 0) { repository.save(any()) }` se revisa que no se guarde ni borre cuando no corresponde.

## Firma del APK

1. Build > Generate Signed App Bundle or APK > APK > Next.
2. Elegir la llave `app/evalapp.jks` (o crear una con Create new). Alias `evalapp`, contraseñas `evalapp123`.
3. Variante `release` > Create. Queda en `app/release/app-release.apk`.
4. La firma también está en `app/build.gradle.kts`, en `signingConfigs` y `buildTypes.release.signingConfig`.
5. Verificar con `apksigner verify --verbose` y ver el certificado con `apksigner verify --verbose --print-certs` (carpeta `Android\Sdk\build-tools\37.0.0`).

La llave y las contraseñas están en el repositorio porque lo pide la evaluación. En una app real no se deberían subir.
