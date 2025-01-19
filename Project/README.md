# PreQL - ELS Project - Group 3
## Grade: 19/20

## Group elements

- Eduardo Luís Tronjo Ramos (up201906732@up.pt);
- Fábio Araújo de Sá (up202007658@up.pt);
- Pedro Pereira Ferreira (up202004986@up.pt);

## Assignments

- The Report for Assignment 1 can be found [here](./docs/assignment_1/els2024-3-CP1.md);
- The Presentation for Assignment 1 can be found [here](./docs/assignment_1/els2024-3-CP1-presentation.pdf);
- The Report for Assignment 2 can be found [here](./docs/assignment_2/els2024-3-CP2.md);
- The Presentation for Assignment 2 can be found [here](./docs/assignment_2/els2024-3-CP2-presentation.pdf);
- The Report for Assignment 3 can be found [here](./docs/assignment_3/els2024-3-CP3.md);
- The Presentation for Assignment 3 can be found [here](./docs/assignment_3/els2024-3-CP3-presentation.pdf);

## Project setup

For this project, you need to [install Gradle](https://gradle.org/install/)

Copy your source files to the ``src`` folder, and your JUnit test files to the ``test`` folder.

## Compile and Running

To compile and install the program, run ``gradle installDist``. This will compile your classes and create a launcher script in the folder ``build/install/els2024-<GROUP>/bin``. For convenience, there are two script files, one for Windows (``els2024-<GROUP>.bat``) and another for Linux (``els2024-<GROUP>``), in the root of the repository, that call these scripts.

After compilation, tests will be automatically executed, if any test fails, the build stops. If you want to ignore the tests and build the program even if some tests fail, execute Gradle with flags "-x test".

When creating a Java executable, it is necessary to specify which class that contains a ``main()`` method should be entry point of the application. This can be configured in the Gradle script with the property ``mainClassName``, which by default has the value ``pt.up.fe.els2024.Main``.

We need to specify an argument, which will be the ``.preql`` file to be executed in the process. For example:

```bash
$ gradle build && gradle run --args="resources/preql/alice.preql"
```

## Test

To test the program, run ``gradle test``. This will execute the build, and run the JUnit tests in the ``test`` folder. If you want to see output printed during the tests, use the flag ``-i`` (i.e., ``gradle test -i``).
You can also see a test report by opening ``build/reports/tests/test/index.html``.

## Code Documentation

To update the code documentation, simply run ``gradle javadoc``. If you want to see the output, please go to [javadoc](./javadoc/) folder.