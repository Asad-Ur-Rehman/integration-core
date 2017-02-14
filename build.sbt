lazy val commonSettings = Seq(
  organization := "com.signalvine",
  scalaVersion := "2.11.8"
)

lazy val core = (project in file("."))
  .settings(
    commonSettings,
    name := "IntegrationCore",
    version := "0.1.0",
    libraryDependencies ++= Seq("com.typesafe.play" %% "play-json" % "2.5.9"),
    libraryDependencies ++= Seq("com.typesafe.play" %% "anorm" % "2.5.0"),
    libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.8.7" % Test),
    libraryDependencies ++= Seq("org.specs2" %% "specs2-matcher-extra" % "3.8.7" % Test),
    libraryDependencies ++= Seq("org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % Test),
    resolvers ++= Seq("Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/")
  )
