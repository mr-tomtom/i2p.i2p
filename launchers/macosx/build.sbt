import sbtassembly.AssemblyPlugin.defaultShellScript
import sbt._
import Keys._
import sbt.io.IO
import java.io.File

lazy val i2pVersion = "0.9.34"

lazy val buildAppBundleTask = taskKey[Unit](s"Build an Mac OS X bundle for I2P ${i2pVersion}.")
lazy val bundleBuildPath = file("./output")


// Pointing the resources directory to the "installer" directory
resourceDirectory in Compile := baseDirectory.value / ".." / ".." / "installer" / "resources"
lazy val resDir = new File("./../installer/resources")
lazy val i2pBuildDir = new File("./../build")
lazy val warsForCopy = i2pBuildDir.list.filter { f => f.endsWith(".war") }
lazy val jarsForCopy = i2pBuildDir.list.filter { f => f.endsWith(".jar") }

convertToICNSTask := {
  println("TODO")
}

buildAppBundleTask := {
  println(s"Building Mac OS X bundle for I2P version ${i2pVersion}.")
  bundleBuildPath.mkdir()
  val paths = Map[String,File](
    "execBundlePath" -> new File(bundleBuildPath, "I2P.app/Contents/MacOS"),
     "resBundlePath" -> new File(bundleBuildPath, "I2P.app/Contents/Resources"),
     "i2pbaseBunldePath" -> new File(bundleBuildPath, "I2P.app/Contents/Resources/i2pbase"),
    "i2pJarsBunldePath" -> new File(bundleBuildPath, "I2P.app/Contents/Resources/i2pbase/lib"),
     "webappsBunldePath" -> new File(bundleBuildPath, "I2P.app/Contents/Resources/i2pbase/webapps")
  )
  paths.map { case (s,p) => p.mkdirs() }
  val dirsToCopy = List("certificates","locale","man")
  dirsToCopy.map { d => IO.copyDirectory( new File(resDir, d), new File(paths.get("i2pbaseBunldePath").get, d) ) }
  warsForCopy.map { w => IO.copyFile( new File(i2pBuildDir, w), new File(paths.get("webappsBunldePath").get, w) ) }
  warsForCopy.map { j => IO.copyFile( new File(i2pBuildDir, j), new File(paths.get("i2pJarsBunldePath").get, j) ) }
}

// Unmanaged classpath will be available at compile time
unmanagedClasspath in Compile ++= Seq(
  baseDirectory.value / ".." / ".." / "build" / "*.jar",
  baseDirectory.value / ".." / ".." / "router" / "java" / "src"
)

// Please note the difference between browserbundle, this has
// the "in Compile" which limit it's scope to that.
//unmanagedBase in Compile := baseDirectory.value / ".." / ".." / "build"

libraryDependencies ++= Seq(
  "net.i2p" % "router" % i2pVersion % Compile
)


assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript))

assemblyJarName in assembly := s"${name.value}-${version.value}"
