# module: lib

In the past we had a lot of issues where transitive dependencies of used libs
where incompatible and lead to major and minor misbehavior.

Especially core libs like:

* avro
* kotlinx-serialization
* slf4j
* jackson
* ...

are used by many 3rd party libs. It proved to be hard to achieve a clean exclusion/replacement
using our multi module/parent/root setup.

That's why we decided to provide these core libs via our own poms, so we keep full control
over the dependency tree.

There might be phases where the defined exclusions are not necessary because the used libs declare the same
dependencies, but for sake of consistency, we decided to keep them in this lib module,
otherwise we would have releases where the modules are removed and others where they are added again.
