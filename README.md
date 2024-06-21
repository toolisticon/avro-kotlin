# avro-kotlin

Avro Kotlin provides a type- and null-safe type-system that encapsulates the apache-java avro-types and makes them 
easily accessible from kotlin.

[![stable](https://img.shields.io/badge/lifecycle-STABLE-green.svg)](https://github.com/holisticon#open-source-lifecycle)
[![Build Status](https://github.com/toolisticon/avro-kotlin/workflows/Development%20branches/badge.svg)](https://github.com/toolisticon/avro-kotlin/actions)
[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-RED.svg)](https://holisticon.de/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.toolisticon.lib/avro-kotlin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.toolisticon.lib/avro-kotlin)

Since Version 1.11.4.2 we use a multi-module build. The GroupId changed, best use the provided BOM:

```xml
<dependency>
    <groupId>io.toolisticon.kotlin.avro</groupId>
    <artifactId>avro-kotlin-bom</artifactId>
    <version>LATEST</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```


## Apache Avro

> **Apache Avro is a data serialization system.**
>
> Avro provides:
>
> * Rich data structures.
> * A compact, fast, binary data format.
> * A container file, to store persistent data.
> * Remote procedure call (RPC).
>
> * Simple integration with dynamic languages. Code generation is not required to read or write data files nor to use or implement RPC protocols. Code generation as an optional optimization, only worth implementing for statically typed languages.
>
> see: [Avro Documentation](https://avro.apache.org/docs/1.11.1/specification/)

To simplify usage of the `org.apache.avro:avro-core` lib (written in java) from kotlin, this library wraps the java implementation. Parsing declarations
and generating schema is still done using the underlying official library, but we built tools, wrappers and helpers on top.

## why?

Working with avro files (reading, writing, copying, modifying, ...) can quickly become cumbersome, because the jvm file/resource io is far from convenience.  

This lib provides simplified access to avro schema and protocol files by giving you `fromResource`, `fromDirectory` and `writeToFile` methods to read and write `.avsc` and `.avpr` files.

The assumption is, that the location of an avro file follows the java fqn/package convention, so when you have `namespace=com.acme.foo` and `name=MySchema`, the file/resource is expected under `com/acme/foo/MySchema.avsc`.  

## how?

* store your schema files according to the package convention
* Create a fqn wrapper `schema(namepace="com.acme.foo", name="MySchema")`
* use the extension functions `fromResource` or `fromDirectory` to load and parse the `Schema`. (Same applies to `Protocol`s.)  


## Build

### Semantic Versioning

To reflect the version of the underlying `avro-core` library, we use a 4-digit semantic versioning concept (semVer). Take the current release version: `1.11.2.0`, 
which uses version `1.11.3` of the `avro-core` lib:

* The first two digits `1.11` refer to the major/minor digits of the apache lib. Together they build the `major` version of `avro-kotlin`. We assume our lib to not have breaking (major) changes, unless the underlying apache library provides them
* the last two digits `2.0` refer to the version of our library, where `2` indicates the second iteration when it comes to major features and `0` to the latest patch level, this version increases for (bug-)fixes mainly.

### Releases

* all issues and dependabot updates must release to a milestone. The milestone must be labeled with the intended release version. It is ok to change the milestone label before releasing, so a template name like `1.11.2.x` is ok until you finally choose the minor/patch number.
* Before releasing: close the milestone (and finalize its label). Remember to create a new template milestone for follow-up issues. 
  * Closing the milestone triggers a GitHub action that creates a draft release with the version of the milestone label.
* Then, manually on your local console, run `mvn gitflow:release-start` which will create a release branch and interactively ask for the desired release version. It triggers a pipeline run that evaluates the release branch
* Finalize the release by running `mvn gitflow:release-finish` this will merge the release branch to `master` branch and thereby trigger the github release action that publishes the jar, sources and docs to sonatype/maven-central. 
* Wait until the library is available on maven-central. This should take just a couple of minutes if you manually try to use it in a pom.xml via `mvn -U` (although it might take hours until it is indexed and the badge changes and dependabot starts to create requests).
* Go to the GitHub releases and finalize the draft that was created in step 2.
* done

### Project Structure

- The root pom of the project only defines plugins and modules.
- The `_pom/parent` defines a POM module, that is used by the implementation submodules as a parent and defines `dependencyManagement` and `dependencies`.
- The `_pom/bom` defines a POM module used by the users of the libraries as a Bill of Material (BOM).
- The `_test` defines a common module used on a Maven scope `test`. If you need test libraries, define it there.
- The `avro-kotlin` defines main functions to effectively work with Avro from Kotlin. 
- The `avro-kotlin-serialization` handles serialization issues for using `kotlinx-serialization` framework with Avro.
- The `generator` defines a parent for Kotlin code generation.
- The `generator/avro-kotlin-generator-api` provides an API/SPI module for the extensions of the Avro Kotlin code generator.
- The `generator/avro-kotlin-generator` provides an Avro Kotlin code generator based on Kotlin Poet.
- The `generator/avro-kotlin-generator-maven-plugin` provides a Maven Plugin using the Avro Kotlin from Maven.
- The `_examples` provide a series of examples and test fixtures. Those take the external side and are considers as project users (and not implementers) and therefor use the BOM.
