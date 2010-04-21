import sbt._


class ConfiggyProject(info: ProjectInfo) extends StandardProject(info) {
  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.1" % "test"
  val vscaladoc = "org.scala-tools" % "vscaladoc" % "1.1-md-3"
}
