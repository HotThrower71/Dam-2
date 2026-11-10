# Documentación: Fire Mateu Expl

Una animación de **fuego** en una ventana. Cada punto de la pantalla tiene una temperatura (0 a 255), el calor sube y se enfría, y cada temperatura se pinta con un color (negro → rojo → amarillo → blanco).

## Cómo leer esta documentación

Léela en este orden, de lo general a lo concreto:

| # | Archivo | Qué explica |
|---|---|---|
| 1 | [01-flujo-general.md](01-flujo-general.md) | El mapa completo: qué piezas hay y cómo se hablan |
| 2 | [02-modelo.md](02-modelo.md) | `Fire`, `Palette`, `ColorTargetDTO`, `Render`, `Model` |
| 3 | [03-vista.md](03-vista.md) | `View` y `Viewer` (la ventana) |
| 4 | [04-presenter-y-main.md](04-presenter-y-main.md) | `Presenter` y `Main` (lo que lo une todo) |

## Mapa de archivos

```
Fire Mateu Expl/
├── Main.java                      ← arranca todo
├── documentation/                 ← estás aquí
└── src/
    ├── model/                     ← los datos y la lógica (sin ventanas)
    │   ├── IModel.java
    │   ├── Model.java
    │   ├── Fire.java
    │   ├── Palette.java
    │   ├── ColorTargetDTO.java
    │   └── Render.java
    ├── view/                      ← lo que se ve (la ventana)
    │   ├── IView.java
    │   ├── View.java
    │   └── Viewer.java
    └── presenter/                 ← el intermediario
        ├── IPresenter.java
        └── Presenter.java
```

## Mini glosario

- **Temperatura:** un número de 0 (apagado) a 255 (lo más caliente). Cada punto del fuego tiene una.
- **Píxel:** un punto de la imagen. Aquí hay un píxel por cada punto de temperatura.
- **Hilo (`Thread`):** un "trabajador" que va por su cuenta. Aquí hay dos: uno hace avanzar el fuego y otro refresca la ventana.
- **Interfaz (`IModel`, `IView`, `IPresenter`):** un contrato. Dice *qué* métodos hay que tener, sin decir *cómo* se hacen.
- **DTO:** una clase que solo guarda datos, sin lógica (`ColorTargetDTO`).
- **MVP:** Modelo - Vista - Presenter. El modelo y la vista **no se conocen entre sí**; los dos hablan solo con el Presenter.
- **ARGB:** cómo se guarda un color en un `int`: transparencia (A), rojo (R), verde (G), azul (B).
