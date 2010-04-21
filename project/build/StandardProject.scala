import sbt._
import java.io.File


protected class StandardProject(info: ProjectInfo) extends DefaultProject(info) {
  override def dependencyPath = "lib"
  override def disableCrossPaths = true

  val homeFolder = Path.fromFile(new File(System.getProperty("user.home")))
  override def ivyCacheDirectory = Some(homeFolder / ".ivy2-sbt" ##)

  // maven repositories
  val ibiblioRepository  = "ibiblio" at "http://mirrors.ibiblio.org/pub/mirrors/maven2/"
  val jbossRepository    = "jboss" at "http://repository.jboss.org/maven2/"
  val lagRepository      = "lag.net" at "http://www.lag.net/repo/"
  val twitterRepository  = "twitter.com" at "http://www.lag.net/nest/"

  val powerMock          = "powermock-api" at "http://powermock.googlecode.com/svn/repo/"
//  val mavenDotOrg        = "repo1" at "http://repo1.maven.org/maven2/"
  val scalaToolsReleases = "scala-tools.org" at "http://scala-tools.org/repo-releases/"
  val scalaToolsTesting  = "testing.scala-tools.org" at "http://scala-tools.org/repo-releases/"
  val reucon             = "reucon" at "http://maven.reucon.com/public/"
  val oauthDotNet        = "oauth.net" at "http://oauth.googlecode.com/svn/code/maven"
  val javaDotNet         = "download.java.net" at "http://download.java.net/maven/2/"
  val atlassian          = "atlassian" at "https://m2proxy.atlassian.com/repository/public/"

  override def packageAction = super.packageAction dependsOn(testAction)

  log.info("Standard project rules loaded (2010-04-20).")
}
