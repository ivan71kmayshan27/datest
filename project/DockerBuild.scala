import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.archetypes._
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.Docker
import sbt.Keys._

object DockerBuild {
  val Plugins = JavaServerAppPackaging && DockerPlugin

  def settings = Seq(

    packageName in Docker := "dockerTest",
    version in Docker := name + "-latest",

    dockerBaseImage := "openjdk:8u151-jre-alpine",
    dockerExposedPorts := 8081 :: Nil,

    defaultLinuxInstallLocation in Docker := s"/opt/",

    daemonUser in Docker := "root",
    daemonGroup in Docker := "root",
  )
}
