import sbtrelease.ReleaseStateTransformations._
import sbtrelease.{Version, versionFormatError}

val integrationCoreVersion = "0.1.0-SNAPSHOT"

lazy val fetch = taskKey[Unit]("Fetch Dependencies")

fetch:={
  "./ci/fetch.sh " + integrationCoreVersion!
}

val isFinal = {
  Option(System.getProperty("final")).getOrElse("false") match {
    case "false" => false
    case _ => true
  }
}

lazy val runner = (project in file("."))
  .settings(
    name := "provider-factory",
    organization := "com.signalvine",
    scalaVersion := "2.11.8",
    publishTo := Some(Resolver.file("file", new File("release"))),
    publishMavenStyle := true,

    releaseVersion := {
      ver =>
        isFinal match {
          case true => Version(ver).map(_.withoutQualifier.string).getOrElse(versionFormatError)
          case false => Version(ver).map(_.asSnapshot.string).getOrElse(versionFormatError)
        }
    },

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
    libraryDependencies ++= Seq("com.signalvine" %% "integration-core" % integrationCoreVersion),
    resolvers ++= Seq("Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository")
  )
