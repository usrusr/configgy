
Configgy is a library for handling config files and logging for a scala
daemon. The idea is that it should be simple and straightforward, allowing you
to plug it in and get started quickly, writing small useful daemons without
entering the shadowy world of java frameworks.


Changes in version 1.4 include these features:

  - system properties are now expanded in $() in config files [patch from
    blair zajac]
  - log line prefix can be set manually with a new GenericFormatter class
    and "prefix_format" config
  - ConfigMap.getBool() will throw an exception if the value isn't set to
    true or false
  - numbers are allowed in string lists (but converted to strings)

and these bug fixes:

  - fixed bug where weekly-rolled logfiles were rolled too frequently on
    their scheduled rotation day
  - fixed Attributes.copy() to copy over the inheritance chain too
  - fixed ByteArray.hexlify() sign problem for high bytes


It can't be a project without a website, so that's here:

  http://www.lag.net/configgy/

The github repository is here:

  http://github.com/robey/configgy/tree/master

And, for fans of maven/ivy, you can add this to your repository list to
get configgy versions:

  http://www.lag.net/repo/

