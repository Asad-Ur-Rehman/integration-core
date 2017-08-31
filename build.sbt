import sbtrelease.ReleaseStateTransformations._
import sbtrelease.{Version, versionFormatError}

val isFinal = {
  Option(System.getProperty("final")) match {
    case Some("true") => true
    case _ => false
  }
}

lazy val providerFactory = (project in file("."))
  .settings(
    name := "provider-factory",
    organization := "com.signalvine",
    scalaVersion := "2.11.8",
    publishTo := {
      val nexus = "https://nexus.signalvine.com/"
      if (isSnapshot.value)
        Some("maven-snapshots" at nexus + "repository/maven-snapshots")
      else
        Some("maven-releases"  at nexus + "repository/maven-releases")
    },
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials"),
    releaseVersion := {
      ver =>
        isFinal match {
          case true => Version(ver).map(_.withoutQualifier.string).getOrElse(versionFormatError)
          case false => Version(ver).map(_.asSnapshot.string).getOrElse(versionFormatError)
        }
    },

    releaseIgnoreUntrackedFiles := true,

    releaseNextVersion := { ver =>
      isFinal match {
        case true => Version(ver).map(_.bump.string).getOrElse(versionFormatError)
        case false => Version(ver).map(_.withoutQualifier.string).getOrElse(versionFormatError)
      }
    },

    releaseProcess := {
      isFinal match {
        case true => Seq[ReleaseStep](
          inquireVersions,
          setReleaseVersion,
          commitReleaseVersion,
          tagRelease,
          publishArtifacts,
          setNextVersion,
          commitNextVersion,
          pushChanges
        )
        case false => Seq[ReleaseStep](
          inquireVersions,
          setReleaseVersion,
          commitReleaseVersion,
          publishArtifacts,
          setNextVersion,
          commitNextVersion,
          pushChanges
        )
      }
    },
    libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.8.7" % Test),
    libraryDependencies ++= Seq("org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % Test),
    libraryDependencies ++= Seq("com.signalvine" %% "integration-core" % "0.1.2-SNAPSHOT"),
    libraryDependencies ++= Seq("net.codingwell" %% "scala-guice" % "4.1.0"),
    libraryDependencies ++= Seq("org.clapper" %% "classutil" % "1.1.1"),
    libraryDependencies ++= Seq("ch.qos.logback" % "logback-core" % "1.1.2", "ch.qos.logback" % "logback-classic" % "1.1.2"),
    resolvers ++= Seq("maven-snapshots" at "https://nexus.signalvine.com/repository/maven-snapshots"),
    resolvers ++= Seq("maven-releases"  at "https://nexus.signalvine.com/repository/maven-releases")
  )
