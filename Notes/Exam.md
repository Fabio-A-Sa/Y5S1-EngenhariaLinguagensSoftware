# Exam Preparation

- State Machine Model, para abstração de problemas com estados;
- SMM pode ter o código de configuração externa (v: não precisa de um programa externo para cada controlador e o JAR do parser pode ser só um para várias state machines, d: erros são detectados apenas em runtime);
- O parsing de uma DSL resulta num Semantic Model, separando a linguagem da parte semântica e assim o modelo poder ser testado independentemente da linguagem;
- As internal DSL estão ligadas à host language (general purpose) através de Fluent APIs;
- Method Chaining através de Expression Builders, onde a chaining tem meaning;
- Existe necessidade de uma parsing layer de External para Internal DSL, que transforma tudo em chaining;
- Expression Builder para uma fluent interface sobre uma command-query API na linguagem host;
- Static factory methods ajudam a evitar o `new` na chain;
- DSL é uma linguagem de programação com domínio próprio e restrito (não turing-complete);
- External DSL é separada da main language, com sintaxe própria, embora seja parsed pela host language;
- Internal DSL é criada a partir da language host, um subset das suas features num estilo particular;
- DSL regex, NOT-DSL language R, MAYBE-DSL: Markdown, YAML;
- 