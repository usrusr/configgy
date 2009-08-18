
Configgy is a library for handling config files and logging for a scala
daemon. The idea is that it should be simple and straightforward, allowing you
to plug it in and get started quickly, writing small useful daemons without
entering the shadowy world of java frameworks.

Changes in version 1.1 include:

  - lift-style call-by-name logging (ifDebug, etc)
  - fixed a bug with true/false values
  - switched build to ivy, to support maven repositories
  - switched to vscaladoc and specs
  - migrated the parser to use the new RegexParser in the parser-combinator
    library, cutting that code in half
  - renamed ConfiggyExtensions to just net.lag.extensions


It can't be a project without a website, so that's here:

  http://www.lag.net/configgy/

The github repository is here:

  http://github.com/robey/configgy/tree/master

And, for fans of maven/ivy, you can add this to your repository list to
get configgy versions:

  http://www.lag.net/repo/

