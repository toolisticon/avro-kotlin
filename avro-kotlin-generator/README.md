# avro-kotlin-generator

Purpose: Takes one (or more) schema or protocol input files and outputs one (or more) `*.kt` files containing source code.

## Concept

The central class is the AvroGenerator. It is initialized with 



## Use Cases

### Generate one DataClass for a given record-schema file.

Reusing schemas or including types that are not declared within a schema-file is not allowed by the avro specification.
For code generation. this means:

* read a source file into an SchemaDeclaration
