# Exam Preparation

- State Machine Model, para abstração de problemas com estados;
- SMM pode ter o código de configuração externa (v: não precisa de um programa externo para cada controlador e o JAR do parser pode ser só um para várias state machines, d: erros são detectados apenas em runtime);
- O parsing de uma DSL resulta num Semantic Model, separando a linguagem da parte semântica e assim o modelo poder ser testado independentemente da linguagem;
- As internal DSL estão ligadas à host language (general purpose) através de Fluent APIs;
- Method Chaining para criar expression builders;
- Existe necessidade de uma parsing layer de External para Internal DSL, que transforma tudo em chaining;
- 