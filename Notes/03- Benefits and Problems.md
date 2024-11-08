# DSLs, Benefits and Problems

DSL é uma linguagem de computador com expressividade limitada focada num determinado domínio. Usada pelos humanos para dar instruções que serão executadas pelo computador. Deve ter um senso de fluência, com expressões individuais que podem ser compostas. 

## Categorias

### External DSL

Tem uma custom syntax, separada da linguagem onde internamente é executada. É depois parsed para ser interpretada pela internal DSL. São exemplos o SQL ou o Regex.

### Internal DSL

Criada a partir de uma general-purpose language, como Java, onde usa um subset das features da linguagem-host.

## Benefits

- Melhora a produtividade do desenvolvimento, porque é uma forma mais clara e objectiva (abstract até) de comunicação entre as partes, assim como de debug e de code improvement. Como é uma linguagem limitada, também limita os tipos de erros que podem ocorrer;
- Permite que o cliente perceba melhor o que se está a fazer, sem que tenha de entender internamente da implementação;

## Problems and Limitations

- Problemas no aprendizado de novas DSLs, tem de haver um período de adaptação de qualquer forma. No entanto o esforço de aprendizado não se compara com uma general-purpose language;
- É mais complicado entender o modelo por detrás das DSLs que a própria DSL, daí a camada extra de abstração necessária;
- Custo de construção e manutenção, porque os desenvolvedores não estão habituados a saber lidar com a sua implementação, podendo nesse caso ser um bom contra-exemplo;
- A qualidade e a construção da abstração, que tem tendência a evoluir e não torná-la estática;