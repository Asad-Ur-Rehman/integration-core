lazy val runner = (project in file("."))
  .settings(
    name := "ProviderFactory",
    version := "1.0",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.8.7" % Test),
    libraryDependencies ++= Seq("org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % Test)
  )