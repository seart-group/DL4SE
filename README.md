# SEART Data Hub

The SEART Data Hub platform allows to easily create large-scale datasets that can be used to either run empirical MSR
studies or to train Deep Learning models to automate software engineering tasks.

## Contents

This project contains several modules:

- `dl4se-model`: A module containing domain model classes used for mapping the relational database structure to the
  programming environment;
- `dl4se-analyzer`: A module containing implementations of code analysis operations running on `tree-sitter`;
- `dl4se-transformer`: A module containing implementations of code transformation operations running on `tree-sitter`;
- `dl4se-crawler`: A standalone crawler application that we use to mine source code from GitHub repositories indexed by
  [GitHub Search](https://seart-ghs.si.usi.ch/);
- `dl4se-server`: A Spring Boot server application that acts as our platform back-end;
- `dl4se-spring`: Common Spring Boot configuration and utilities used in both the server and the crawler;
- `dl4se-website`: A front-end web-application written in Vue.

## Installation and Usage

This section will detail the necessary actions for setting up and running the project locally on your machine.

### [Environment](README_ENV.md)

### [Database](README_DB.md)

### [Usage](README_RUN.md)

### [Dockerization](README_DOCKER.md)

## License

[MIT](LICENSE)

## FAQ

### How do you implement language-specific analysis heuristics?

Heuristics used to identify test code in Java and Python can be found
[here](dl4se-analyzer/src/main/java/ch/usi/si/seart/analyzer/predicate/path/JavaTestFilePredicate.java) and
[here](dl4se-analyzer/src/main/java/ch/usi/si/seart/analyzer/predicate/path/PythonTestFilePredicate.java).
Heuristics used to identify boilerplate code can be found
[here](dl4se-analyzer/src/main/java/ch/usi/si/seart/analyzer/enumerate/JavaBoilerplateEnumerator.java) and
[here](dl4se-analyzer/src/main/java/ch/usi/si/seart/analyzer/enumerate/PythonBoilerplateEnumerator.java) respectively.

### How can I request a feature or ask a question?

If you have ideas for a feature you would like to see implemented or if you have any questions, we encourage you to
create a new [discussion](https://github.com/seart-group/DL4SE/discussions/). By initiating a discussion, you can engage
with the community and our team, and we'll respond promptly to address your queries or consider your feature requests.

### How can I report a bug?

To report any issues or bugs you encounter, please create a [new issue](https://github.com/seart-group/DL4SE/issues/).
Providing detailed information about the problem you're facing will help us understand and address it more effectively.
Rest assured, we are committed to promptly reviewing and responding to the issues you raise, working collaboratively
to resolve any bugs and improve the overall user experience.
