package com.signalvine.integration

import java.io.File
import java.net.URLClassLoader

import com.google.inject.AbstractModule
import com.signalvine.integration.core.{IntegrationProvider, ProviderIdentity}
import net.codingwell.scalaguice.ScalaModule
import org.clapper.classutil.{ClassFinder, ClassInfo}
import com.typesafe.config.ConfigFactory

class GuiceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    val jarFiles = getListOfFiles(ConfigFactory.load().getString("watcher.folder"))
      .filter((p: File) => p.isFile && p.getName.endsWith(".jar"))
    val filteredClasses = ClassFinder
      .classInfoMap(ClassFinder(jarFiles).getClasses().iterator)
      .filter(_._2.interfaces.contains(classOf[IntegrationProvider].getName))
      .values
    val plugins = new URLClassLoader(jarFiles.map(_.toURI.toURL).toArray, this.getClass.getClassLoader)
    val providers = filteredClasses.map(
      (classInfo: ClassInfo) => {
        val provider = Class
          .forName(classInfo.name, true, plugins)
          .newInstance()
          .asInstanceOf[IntegrationProvider]
        bind[IntegrationProvider].annotatedWithName(provider.id).toInstance(provider)
        provider
      }
    ) ++ Seq(NullProvider)
    bind[IntegrationProvider].annotatedWithName(NullProvider.id).toInstance(NullProvider)
    bind[List[ProviderIdentity]]
      .toInstance(providers
        .map(provider => new ProviderIdentity(provider.id, provider.getAuthFields)).toList)
  }

  def getListOfFiles(dir: String): List[File] = {
    val directory = new File(dir)
    if (directory.exists && directory.isDirectory) {
      directory.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

}