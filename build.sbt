ThisBuild / scalaVersion := "2.12.8"
ThisBuild / organization := "io.inpher"

lazy val scala-mpc = (project in file("."))
  .settings(
    name := "scala-mpc",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  )
