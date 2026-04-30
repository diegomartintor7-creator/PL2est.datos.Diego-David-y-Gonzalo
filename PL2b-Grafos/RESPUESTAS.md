//RESPUESTAS PL2b — Grafo de Conocimiento RDF



// ¿Cuál es el camino mínimo entre dos entidades A y B del grafo?

El camino mínimo entre dos entidades A y B es la secuencia más corta de nodos y aristas que conecta A con B recorriendo el menor número de saltos posible.

En nuestro grafo de conocimiento RDF, cada tripleta `<S, P, O>` crea una arista dirigida de S hacia O. Por tanto, el camino mínimo depende de la dirección de las aristas: puede existir un camino de A a B sin que exista el camino inverso de B a A.

El algoritmo utilizado para calcularlo es **BFS (Búsqueda en Anchura)**, que garantiza encontrar el camino con menos saltos en grafos no ponderados. BFS explora primero todos los nodos a distancia 1 del origen, luego a distancia 2, y así sucesivamente, por lo que el primer camino que encuentra hasta el destino es siempre el mínimo.

//Ejemplos sobre el grafo Nobel:

- De `persona:Albert Einstein` a `lugar:Ulm` → **1 salto**
    - `persona:Albert Einstein` → `lugar:Ulm`

- De `persona:Albert Einstein` a `persona:Aage Bohr` → **2 saltos**
    - `persona:Albert Einstein` → `persona:Niels Bohr` → `persona:Aage Bohr`

- De `lugar:Ulm` a `persona:Albert Einstein` → **sin camino**
    - El grafo es dirigido: las aristas no se pueden recorrer hacia atrás.

La implementación se encuentra en el método `caminoMinimo(String inicio, String fin)` de la clase `GrafoConocimiento`.

---

// Dado un archivo de datos que se carga en el grafo, ¿genera un grafo disjunto? Cree dos archivos que generen cada opción posible y compruebe en el código.

Un grafo es "disjunto" (o no conexo) cuando existen al menos dos grupos de nodos que no están conectados entre sí por ningún camino, ni directo ni indirecto. Si todos los nodos están conectados entre sí de alguna forma, el grafo es **conexo**.

Para detectarlo se calculan las "componentes conexas" tratando el grafo como no dirigido (ignoramos la dirección de las aristas para ver si las entidades están relacionadas de algún modo). Si hay más de una componente, el grafo es disjunto.

Se han creado dos ficheros de prueba:

// `datos_conexo.json` → Grafo NO disjunto (conexo)

Todas las entidades están conectadas entre sí a través de alguna cadena de relaciones. Por ejemplo, María → Ana → Luis → Madrid → España, y Carlos → UAH → Alcalá → España, y además María → UAH. Todo el grafo forma una única componente conexa.

//Resultado esperado:** `esDisjunto() = false`, 1 componente conexa.

### `datos_disjunto.json` → Grafo **disjunto**

Contiene dos grupos de nodos completamente aislados entre sí:
- Grupo 1: Carlos → David → Barcelona
- Grupo 2: Eva → Fran → Perro

Ningún nodo del grupo 1 tiene relación con ninguno del grupo 2.

**Resultado esperado:** `esDisjunto() = true`, 2 componentes conexas.

La comprobación se realiza en `Main.java` con el método `comprobarDisjunto()`, que llama a `esDisjunto()` y `getComponentesConexas()` de la clase `GrafoConocimiento`.

---

## Suponiendo un grafo de conocimiento general con la información de los premios Nobel de todas las áreas, ¿cómo harías para responder a la pregunta: ¿Qué físico famoso nació en la misma ciudad que Einstein?

### ¿Cómo se responde esta pregunta con un grafo?

Para responder a esta pregunta con un grafo de conocimiento se siguen estos pasos:

1. Buscar la tripleta `<persona:Albert Einstein, nace_en, lugar:X>` para obtener su ciudad de nacimiento.
2. Buscar todas las tripletas `<persona:Y, nace_en, lugar:X>` donde `lugar:X` sea esa misma ciudad, excluyendo al propio Einstein.
3. Filtrar los resultados: que `persona:Y` tenga una tripleta `<persona:Y, gana_nobel, año>` y una tripleta `<persona:Y, area, Física>`.

### Fichero de datos

El fichero `nobel.json` ya contiene la información necesaria. Se ha incluido el físico **Hans Dietrich Müller**, nacido en `lugar:Ulm` igual que Einstein, con Nobel de Física en 1952. Esto permite verificar la consulta con un resultado concreto.

Además el fichero contiene otros físicos con Nobel (Bohr, Curie, Planck, Heisenberg, Schrödinger, Röntgen, Feynman) nacidos en ciudades distintas, y las tripletas de ciudad → país para enriquecer el grafo.

### Código

La lógica está implementada en el método `fisicoNacidoEnMismaCiudad(String personaReferencia)` de `GrafoConocimiento`, y se invoca y muestra en `Main.java` en la sección `[3]`.

---

## Añada una tripleta `<"persona:Antonio", "nace_en", "lugar:Villarrubia de los Caballeros">` al grafo. Liste cuáles son los lugares de nacimiento de los premios Nobel. ¿Qué caminos necesita recorrer para que su respuesta fuese correcta?

La tripleta se añade en tiempo de ejecución mediante:

```java
/* grafo.agregarTripleta(new Tripleta(
    "persona:Antonio",
    "nace_en",
    "lugar:Villarrubia de los Caballeros"
));
```

### Lugares de nacimiento de los premios Nobel

El método `getLugaresNacimientoNobel()` devuelve únicamente las personas que cumplen **dos condiciones**:
- Tienen una tripleta `<persona, nace_en, lugar>`.
- Tienen una tripleta `<persona, gana_nobel, año>`.

Antonio tiene la primera tripleta pero **no tiene** la segunda, por lo que **no aparece** en la lista de lugares de nacimiento Nobel. Esto es correcto: Antonio no es un premio Nobel.

### ¿Qué caminos hay que recorrer?

Para listar los lugares de nacimiento de los Nobel hay que recorrer **dos tipos de relaciones** por cada persona candidata:

1. `persona ──[nace_en]──► lugar` → para obtener dónde nació.
2. `persona ──[gana_nobel]──► año` → para confirmar que es Nobel.

Ambas rutas parten del mismo nodo persona y tienen longitud 1 (un salto directo). La consulta necesita que las dos rutas existan para incluir a esa persona en el resultado. Si solo existe una de las dos (como en el caso de Antonio, que solo tiene `nace_en`), la persona queda excluida.

---

## ¿Qué tipos de nodos tiene el grafo?

En el grafo Nobel los nodos se clasifican por su prefijo URI. Los tipos presentes son:

- **`persona`**: representa a personas físicas. Ejemplos: `persona:Albert Einstein`, `persona:Marie Curie`.
- **`lugar`**: representa ciudades o países. Ejemplos: `lugar:Ulm`, `lugar:Alemania`.
- **`literal`**: nodos sin prefijo, que representan valores directos como años. Ejemplos: `1921`, `1922`, `Física`.

Esta clasificación se obtiene con el método `getNodosPorTipo()` de `GrafoConocimiento`, que extrae el prefijo antes de `:` en cada URI y agrupa los nodos. Los nodos sin prefijo se etiquetan como `literal`.

La distinción entre tipos de nodos es fundamental en RDF: los nodos con URI identifican entidades del mundo real, mientras que los literales representan valores concretos (fechas, cadenas de texto, números).

---

## ¿Qué es una ontología? ¿Qué relación tiene con los grafos? ¿Podríamos crear una ontología para nuestro problema? ¿Qué haríamos con ella?

### ¿Qué es una ontología?

Una ontología es una especificación formal y explícita de los conceptos de un dominio del conocimiento y las relaciones entre ellos. Define qué tipos de entidades existen, qué propiedades tienen y qué relaciones pueden establecerse entre ellas, incluyendo restricciones y reglas lógicas.

En informática, una ontología va más allá de un simple esquema de datos: permite razonar sobre la información, inferir nuevos hechos a partir de los existentes y validar que los datos son coherentes con las reglas definidas.

// ¿Qué relación tiene con los grafos?

Los grafos de conocimiento RDF son la representación práctica de una ontología. Mientras la ontología define las reglas (por ejemplo: "toda persona puede tener exactamente una ciudad de nacimiento" o "los premios Nobel tienen un área de conocimiento"), el grafo contiene los datos concretos que siguen esas reglas.

En el estándar de la web semántica, las ontologías se expresan con **OWL** (Web Ontology Language) y los datos con **RDF**. El grafo RDF es la instanciación de la ontología: la ontología es la clase, el grafo son los objetos.

// ¿Podríamos crear una ontología para nuestro problema?

Sí. Para el grafo de premios Nobel una ontología definiría:

- Clases: `Persona`, `Lugar`, `Premio`.
- **Propiedades de objeto: `nace_en` (Persona → Lugar), `gana_nobel` (Persona → año), `es_padre_de` (Persona → Persona).
-Propiedades de datos: `area` (Persona → string), `pais` (Lugar → Lugar).
- Restricciones: una persona solo puede nacer en un lugar; el área debe ser un valor del conjunto {Física, Química, Medicina, Literatura, Paz, Economía}.

// ¿Qué haríamos con ella?

Con la ontología podríamos:
-Validar los datos: detectar automáticamente tripletas inconsistentes, como una persona con dos ciudades de nacimiento distintas. 
-Inferir nuevos hechos: si definimos que `es_padre_de` implica `es_familiar_de`, el razonador puede deducir automáticamente que Niels Bohr es familiar de Aage Bohr sin que esa tripleta esté explícitamente en el grafo. 
-Hacer consultas más ricas: usando SPARQL (el lenguaje de consulta de RDF) junto con la ontología, podríamos responder preguntas complejas como "dame todos los científicos que comparten área con alguien de su familia".
