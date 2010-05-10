import sbt._


class ConfiggyProject(info: ProjectInfo) extends StandardProject(info) {
  val specs = "org.scala-tools.testing" % "specs_2.8.0.RC2" % "1.6.5-SNAPSHOT" % "test"
  val vscaladoc = "org.scala-tools" % "vscaladoc" % "1.1-md-3"
}
