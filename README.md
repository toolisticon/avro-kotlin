# avro-kotlin

opinionated extension functions and helpers for using apache avro with kotlin.

[![incubating](https://img.shields.io/badge/lifecycle-INCUBATING-orange.svg)](https://github.com/holisticon#open-source-lifecycle)
[![Build Status](https://github.com/toolisticon/avro-kotlin/workflows/Development%20branches/badge.svg)](https://github.com/toolisticon/avro-kotlin/actions)
[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-RED.svg)](https://holisticon.de/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.toolisticon.lib/avro-kotlin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.toolisticon.lib/avro-kotlin)

## why?

Working with avro files (reading, writing, copying, modifying, ...) can quickly become cumbersome, because the jvm file/resource io is far from convenience.  

This lib provides simplified access to avro schema and protocol files by giving you `fromResource`, `fromDirectory` and `writeToFile` methods to read and write `.avsc` and `.avpr` files.

The assumption is, that the location of an avro file follows the java fqn/package convention, so when you have `namespace=com.acme.foo` and `name=MySchema`, the file/resource is expected under `com/acme/foo/MySchema.avsc`.  

## how?

* store your schema files according to the package convention
* Create an fqn wrapper `schema(namepace="com.acme.foo", name="MySchema")`
* use the extension functions `fromResource` or `fromDirectory` to load and parse the `Schema`. (Same applies to `Protocol`s.)  
