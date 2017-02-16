import sbt.Keys.publishTo
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.Version
import sbtrelease.versionFormatError

val isFinal = {
  Option(System.getProperty("final")).getOrElse("false") match {
    case "false" => false
    case _ => true
  }
}

lazy val commonSettings = Seq(
  organization := "com.signalvine",
  scalaVersion := "2.11.8"
)

lazy val core = (project in file("."))
  .settings(
    commonSettings,
    name := "IntegrationCore",
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

    libraryDependencies ++= Seq("com.typesafe.play" %% "play-json" % "2.5.9"),
    libraryDependencies ++= Seq("com.typesafe.play" %% "anorm" % "2.5.0"),
    libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.8.7" % Test),
    libraryDependencies ++= Seq("org.specs2" %% "specs2-matcher-extra" % "3.8.7" % Test),
    libraryDependencies ++= Seq("org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % Test),
    resolvers ++= Seq("Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/")
  )
