package net.i2p

import java.io.{File, InputStream}
/**
  *
  * The purpose of this class is to copy files from the i2p "default config" directory
  * and to a "current config" directory relative to the browser bundle.
  *
  * @author Meeh
  * @version 0.0.1
  * @since 0.9.35
  */
class DeployProfile(confDir: String, baseDir: String) {
  import java.nio.file.{Paths, Files}
  import java.nio.charset.StandardCharsets


  /**
    * This joins two paths in a cross platform way. Meaning it takes care of either we use
    * \\ or / as directory separator. It returns the resulting path in a string.
    *
    * @since 0.9.35
    * @param parent The parent path
    * @param child The child path to append
    * @return String
    */
  def pathJoin(parent:String,child:String): String = new File(new File(parent), child).getPath

  /**
    * This function copies resources from the fatjar to the config directory of i2p.
    *
    * @since 0.9.35
    * @param fStr
    * @return Unit
    */
  def copyFileResToDisk(fStr: String) = Files.copy(
    getClass.getResource("/".concat(fStr)).getContent.asInstanceOf[InputStream],
    Paths.get(pathJoin(confDir, fStr)).normalize()
  )

  /**
    * Filter function for finding missing required files.
    *
    * @since 0.9.35
    * @param l1
    * @param l2
    * @return
    */
  def missingFiles(l1: List[String], l2: List[String]) = l1.filter { x => !l2.contains(x) }


  val warFiles = List("routerconsole.war")

  val staticFiles = List(
    "blocklist.txt",
    "clients.config",
    "continents.txt",
    "countries.txt",
    "hosts.txt",
    "geoip.txt",
    "i2ptunnel.config",
    "logger.config",
    "router.config",
    "webapps.config"
  )

  /**
    *
    * This function will check the existence of static files,
    * and if any of them are lacking, it will be copied from the
    * fat jar's resources.
    *
    * @since 0.9.35
    * @return Unit (Null)
    */
  def verifyExistenceOfConfig() = {
    val fDir = new File(confDir)
    if (fDir.exists()) {
      // We still check if files are in place
      val currentDirContentList = fDir.list.toList
      val missing = missingFiles(staticFiles, currentDirContentList)
      if (!missing.isEmpty) {
        missing.map(copyFileResToDisk)
      }
    } else {
      // New deployment!
      deployDefaultConfig()
    }
  }

  /**
    *
    * This function does the default deployment of files,
    * map is the same as a loop. we're looping over the file list.
    *
    * @since 0.9.35
    * @return Unit
    */
  def deployDefaultConfig(): Unit = staticFiles.map(copyFileResToDisk)

}
