import sbt._
import Keys._

scalaVersion in Global := "2.11.11"

resolvers ++= Seq(
  DefaultMavenRepository,
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases"),
  Resolver.sbtPluginRepo("releases")
)

lazy val commonSettings = Seq(
  organization := "net.i2p",
  scalaVersion := "2.11.11", // We have to use Scala 11 as long as we're going to support JRE 1.7
  version      := "0.1.0-SNAPSHOT",
  maintainer := "Meeh <mikalv@mikalv.net>",
  packageSummary := "The Invisible Internet Project",
  packageDescription := "Blabla"
)


lazy val browserbundle = (project in file("browserbundle"))
  .settings(
    commonSettings,
    name         := "RouterLaunchApp",
    assemblyJarName in assembly := s"${name.value}-${version.value}.jar",
    mainClass in assembly := Some("net.i2p.RouterLauncherApp")
  )

lazy val macosx = (project in file("macosx"))
  .settings(
    commonSettings,
    name         := "MacI2PLauncher",
    assemblyJarName in assembly := s"${name.value}-${version.value}.jar",
    mainClass in assembly := Some("net.i2p.MacOSXRouterLauncherApp")
  )


lazy val root = (project in file("."))
  .aggregate(browserbundle, macosx)

scalacOptions in Compile := Seq("-deprecated")

fork := true

run / javaOptions += "-Xmx512M"
run / connectInput := true
