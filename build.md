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

[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building M2CAT 0.1.0
[INFO] ------------------------------------------------------------------------
[WARNING] The POM for xom:xom:jar:1.2.1 is missing, no dependency information available
[INFO] 
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ M2CAT ---
[INFO] 
[INFO] --- maven-resources-plugin:2.6:copy-resources (copy-resources) @ M2CAT ---
[WARNING] File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 2 resources
[INFO] 
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ M2CAT ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /users/stud00/mp487/mig-git/m2cat/resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:compile (default-compile) @ M2CAT ---
[INFO] Changes detected - recompiling the module!
[WARNING] File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
[INFO] Compiling 28 source files to /users/stud00/mp487/mig-git/m2cat/target/classes
[INFO] 
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ M2CAT ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /users/stud00/mp487/mig-git/m2cat/src/test/resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:testCompile (default-testCompile) @ M2CAT ---
[INFO] No sources to compile
[INFO] 
[INFO] --- maven-surefire-plugin:2.17:test (default-test) @ M2CAT ---
[INFO] No tests to run.
[INFO] 
[INFO] --- maven-war-plugin:2.2:war (default-war) @ M2CAT ---
[INFO] Packaging webapp
[INFO] Assembling webapp [M2CAT] in [/users/stud00/mp487/mig-git/m2cat/target/M2CAT-0.1.0]
[INFO] Processing war project
[INFO] Copying webapp resources [/users/stud00/mp487/mig-git/m2cat/src/main/webapp]
[INFO] Webapp assembled in [277 msecs]
[INFO] Building war: /users/stud00/mp487/mig-git/m2cat/target/M2CAT-0.1.0.war
[INFO] WEB-INF/web.xml already added, skipping
[INFO] 
[INFO] >>> maven-source-plugin:3.0.1:jar (attach-sources) > generate-sources @ M2CAT >>>
[INFO] 
[INFO] --- maven-resources-plugin:2.6:copy-resources (copy-resources) @ M2CAT ---
[WARNING] File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 2 resources
[INFO] 
[INFO] <<< maven-source-plugin:3.0.1:jar (attach-sources) < generate-sources @ M2CAT <<<
[INFO] 
[INFO] --- maven-source-plugin:3.0.1:jar (attach-sources) @ M2CAT ---
[INFO] Building jar: /users/stud00/mp487/mig-git/m2cat/target/M2CAT-0.1.0-sources.jar
[INFO] 
[INFO] --- maven-javadoc-plugin:2.10.4:jar (attach-javadocs) @ M2CAT ---
[WARNING] Source files encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
[INFO]
[INFO] Building jar: /users/stud00/mp487/mig-git/m2cat/target/M2CAT-0.1.0-javadoc.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 11.157 s
[INFO] Finished at: 2017-03-21T13:56:45+01:00
[INFO] Final Memory: 36M/539M
[INFO] ------------------------------------------------------------------------
```

That done, you'll find the binaries in the `target` directory.
