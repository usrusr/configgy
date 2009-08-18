
Configgy is a library for handling config files and logging for a scala
daemon. The idea is that it should be simple and straightforward, allowing you
to plug it in and get started quickly, writing small useful daemons without
entering the shadowy world of java frameworks.

NOTICE! There are API changes in this release, and it requires scala 2.7.2.
Scala 2.7.2 moved String.format from configgy into the standard library, so
I took advantage of the forced incompatibility to clean up the API a bit.


Changes in version 1.2 include:

  - log levels OFF and ALL
  - config keys are always normalized to lowercase now
  - moved most functionality from the Configgy object into the Config class
    so you don't have to use the singleton interface if you don't want to
  - name changes:
      AttributeMap -> ConfigMap
      AttributesException -> ConfigException
  - ConfigMap supports an alternate API which asserts the presence of a config
    key without requiring a default value
  - logging can be asked to "use_full_package_names" and also not to "append"
  - added RuntimeEnvironment to help bootstrapping
  - a few bug fixes


It can't be a project without a website, so that's here:

  http://www.lag.net/configgy/

The github repository is here:

  http://github.com/robey/configgy/tree/master

And, for fans of maven/ivy, you can add this to your repository list to
get configgy versions:

  http://www.lag.net/repo/

