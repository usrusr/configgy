
Configgy is a library for handling config files and logging for a scala
daemon. The idea is that it should be simple and straightforward, allowing you
to plug it in and get started quickly, writing small useful daemons without
entering the shadowy world of java frameworks.


Changes in version 1.3 include:

  - API CHANGE: Log level objects (WARNING, etc) moved into Level
  - added JMX support for viewing/editing a config tree
  - setLong/getLong added to ConfigMap
  - copy() added to ConfigMap, for making a deep copy
  - setConfigMap() added to ConfigMap, for deep-copying one ConfigMap into
    another
  - Config.fromResource() now optionally takes a ClassLoader
  - mkdir the log folder if it doesn't exist (thanks to ijuma)
  - fixed a bug where updating a config key would invalidate other ConfigMap
    objects
  - RuntimeEnvironment's default config filename is now <stage>.conf
  - clarified license as apache 2


It can't be a project without a website, so that's here:

  http://www.lag.net/configgy/

The github repository is here:

  http://github.com/robey/configgy/tree/master

And, for fans of maven/ivy, you can add this to your repository list to
get configgy versions:

  http://www.lag.net/repo/

