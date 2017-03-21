Build ModelCrawler
==================

When you've cloned the source code:

```sh
git clone git@github.com:SemsProject/M2CAT.git
```

There are two supported options to build this project:

* [Build with Maven](#build-with-maven)

Build with Maven 
-----------------

[Maven](https://maven.apache.org/) is a build automation tool. We ship a `pom.xml` together with the sources which tells maven about versions and dependencies. Thus, maven is able to resolve everything on its own and, in order to create the library, all you need to call is `mvn package`:

```sh
usr@srv $ cd M2CAT
usr@srv $ mvn -P release clean package


```

That done, you'll find the binaries in the `target` directory.
