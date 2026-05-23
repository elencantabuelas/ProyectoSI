# Sistema planificador de estudios para exámenes

## Descripción del Proyecto


## Integrantes del Grupo

- Andrés Chamoso Rodríguez
- Yoan Crul
- Aaron Suarez 
- Andres David Gaviria Salcedo 
- Vicent Morant Boigues 

---

# 1. Instrucciones de Instalación

## Git y Requisitos
Para preparar el entorno de desarrollo y colaborar en este proyecto, sigue estos pasos:

1. **Clonar el repositorio:**
```bash
   git clone https://github.com/elencantabuelas/ProyectoSI
````
o puedes usar github desktop

2. **Requisitos previos:**
    
    - Java Development Kit 11 recomendado
    - Plataforma JADE descargada.
        
3. **Configuración en el IDE (IntelliJ IDEA / Eclipse):**
    
    - Importar el proyecto como aplicación Java.
    - Añadir los ficheros `jade.jar` y `commons-codec.jar` al _classpath_ del módulo 


# 2. Dependencias Necesarias

![[dependencias.png]]
# 3. Instrucciones de Ejecución

Para arrancar la plataforma JADE y lanzar nuestros agentes:

1. Configurar el **Main Class** en el IDE como: `jade.Boot`
2. En los **Program Arguments**, introducir:

    ```
    -gui 
    ```

3. Ejecutar el proyecto. Debería abrirse la interfaz RMA (Remote Agent Management) mostrando el _Main-Container_ con nuestros agentes registrados.

# 4. Datos de Ejemplo

Numero de exámenes: 2

#### EXAMEN 1

Asignatura: PPS 

Créditos: 6

Porcentaje del examen: 45

Dificultad (1 - 10): 7

Días antes del examen: 15

Nota deseada: 6

#### EXAMEN 2

Asignatura: Ingenieria de Software I

Créditos: 3

Porcentaje del examen: 6

Dificultad (1 - 10): 5

Días antes del examen: 4

Nota deseada: 7

# 5. Arquitectura del Sistema


```mermaid
%%{init: {"layout": "elk"}}%%

flowchart TD

    A["Agente Interfaz"]

    B["Agente Coordinador"]

    C["Agente Esfuerzo"]

    D["Agente Urgencia"]

    E["Agente Ensamblador"]

  

    A -->|"Lista Examenes"| B

  

    B -->|"n°examenes<br>asignatura<br>nota deseada<br>creditos<br>dificultad"| C

    B -->|"n°examenes<br>asignatura<br>nota deseada<br>dias antes del examen"| D

  

    C -->|"{asignatura, horas}"| E

    D -->|"{asignatura, prioridad}"| E

  

    classDef interfaz stroke:#818cf8,fill:#eef2ff;

    classDef coordinador stroke:#a78bfa,fill:#f5f3ff;

    classDef esfuerzo stroke:#2dd4bf,fill:#f0fdfa;

    classDef urgencia stroke:#fb923c,fill:#fff7ed;

    classDef ensamblador stroke:#4ade80,fill:#f0fdf4;

  

    class A interfaz;

    class B coordinador;

    class C esfuerzo;

    class D urgencia;

    class E ensamblador;

`````
## 6. Declaración de IA

