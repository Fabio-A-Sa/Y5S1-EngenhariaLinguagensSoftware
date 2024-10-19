# Internal DSL

As Internal DSL proporcionam uma abordagem mais fácil para manipular o Semantic Model e os próprios objectos da Symbol Table. No entanto fica restrita à linguagem usada (no caso Java), mas permite que não nos preocupemos com o parsing da linguagem em si. As expressões usadas devem ser expressões permitidas na host language.

## Fluent APIs

Uma interface que permite implementar a Internal DSL em cima de uma linguagem host, como se de uma API se tratasse para manipular os objectos. Usa-se `Method Chaining`:

```java
Processor p = new Processor(2, 2500, Processor.Type.i386);
Disk d1 = new Disk(150, Disk.Speed.UNKNOWN_SPEED, null);
Disk d2 = new Disk(75, 7200, Disk.Interface.SATA);
Return new Computer(p, d1, d2);
```

With Fluent API:

```java
return computer()
            .processor()
                .cores(2)
                .speed(2500)
                .i386()
            .end()
            .disk()
                .size(150)
            .end()
            .disk()
                .size(75)
                .speed(7200)
                .sata()
            .end()
        .end();
```

This can be implemented using function sequence calls as well.

## Parsing Layer

Necessitamos de `ExpressionBuilders`, que se dedicam à criação ou manipulação de objectos internos, que no final serão retornados com as exigências do *method chaining* aplicado. É um objecto, ou famílias de objectos, que providenciam uma interface fluente criada em cima de uma API baseada na linguagem host.