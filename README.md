<p align="center">
  <a href="https://seart-dl4se.si.usi.ch/" target="_blank" rel="noopener noreferrer">
    <img src="dl4se-website/src/assets/img/logo.png" alt="DL4SE Logo">
  </a>
</p>
<p align="center">
    <a href="https://github.com/seart-group/DL4SE/actions/workflows/maven-build.yml"><img src="https://github.com/seart-group/DL4SE/actions/workflows/maven-build.yml/badge.svg" alt="Maven Build Status"></a>
    <a href="https://github.com/seart-group/DL4SE/actions/workflows/vue-build.yml"><img src="https://github.com/seart-group/DL4SE/actions/workflows/vue-build.yml/badge.svg" alt="Vue Build Status"></a>
</p>

# DL4SE

The DL4SE tool allows to easily create large-scale datasets that can be used to either run MSR studies or to train DL
models to automate SE tasks.

## Contents

This project contains several modules:

- `dl4se-model`:
A module containing domain model classes used for mapping the relational database structure to the programming
- environment;
- `dl4se-crawler`:
A standalone crawler application that we use to mine Java source code from GitHub repositories indexed by
[GitHub Search](https://seart-ghs.si.usi.ch/);
- `dl4se-src2abs`:
A modification of Michele Tufano's [src2abs](https://github.com/micheletufano/src2abs), changed for more convenient
integration into the overall platform;
- `dl4se-server`:
A Spring Boot server application that acts as our platform back-end;
- `dl4se-website`:
A front-end web-application written in Vue.

## Installation and Usage

This section will detail the necessary actions for setting up and running the project locally on your machine.

### [Environment](README_ENV.md)

### [Database](README_DB.md)

### [Usage](README_RUN.md)

### [Dockerization](README_DOCKER.md)

## License

[MIT](LICENSE)

## FAQ

### How can I report a bug, request a feature, or ask a question?

Please add a [new issue](https://github.com/seart-group/DL4SE/issues) and we will get back to you very soon.