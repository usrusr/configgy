
Configgy is a library for handling config files and logging for a scala
daemon. The idea is that it should be simple and straightforward, allowing you
to plug it in and get started quickly, writing small useful daemons without
entering the shadowy world of java frameworks.

Features for configuration include:

  - very simple config file format
  - values can be bools, ints, strings, or lists of strings
  - sets of key/value pairs can be grouped into blocks and nested
    inside each other
  - some blocks can inherit default values from other blocks
  - values can depend on previously-set values, or environment variables
  - files can be "imported" into each other
  - scala API for subscribing to a config node and receiving notification
    of changes

Features for logging include:

  - simple API for writing log messages
  - configured via the config file
  - log to multiple files, console, or syslog servers
  - automatic logfile rolling

A quick example of a config file::

    <log>
        filename = "/var/log/pingd.log"
        roll = "daily"
        level = "debug"
    </log>

    hostname = "pingd.example.com"
    port = 3000

and code to use it::

    import net.lag.configgy.Configgy
    import net.lag.logging.Logger

    Configgy.configure("/etc/pingd.conf")

    val config = Configgy.config
    val hostname = config.get("hostname", "localhost")
    val port = config.getInt("port", 3000)

    val log = Logger.get
    log.error("Unable to listen on %s:%d!", hostname, port)

It really is that simple. I'll probably make a website for this project
eventually, but for now, everything you need is on github:

    http://github.com/robey/configgy/tree/master
