# Avro files copied from apache-avro

To get a broad set of examples to test against, this resource folder contains
schema and protocol declarations taken directly from the [apache-avro repo](https://github.com/apache/avro).

The files have been extracted via `find . -name "*.[avsc|avpr]" -type f -exec cp {} <target> \;` and formatted.

Use in tests to generate code and check correctness.
