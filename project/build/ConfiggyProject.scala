import sbt._

class OstrichProject(info: ProjectInfo) extends DefaultProject(info) {
  // Maven repositories
  val scalaToolsTesting = "testing.scala-tools.org" at "http://scala-tools.org/repo-releases/"
  val powerMock = "powermock-api" at "http://powermock.googlecode.com/svn/repo/"
  val mavenDotOrg = "repo1" at "http://repo1.maven.org/maven2/"
  val scalaToolsReleases = "scala-tools.org" at "http://scala-tools.org/repo-releases/"
  val reucon = "reucon" at "http://maven.reucon.com/public/"
  val lagDotNet = "lag.net" at "http://www.lag.net/repo/"
  val oauthDotNet = "oauth.net" at "http://oauth.googlecode.com/svn/code/maven"
  val javaDotNet = "download.java.net" at "http://download.java.net/maven/2/"
  val jBoss = "jboss-repo" at "http://repository.jboss.org/maven2/"
  val nest = "nest" at "http://www.lag.net/nest/"
  val atlassian = "atlassian" at "https://m2proxy.atlassian.com/repository/public/"

  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.1"
  val vscaladoc = "org.scala-tools" % "vscaladoc" % "1.1-md-3"
}
