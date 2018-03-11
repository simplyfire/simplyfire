name := """simplyfire"""
organization := "ru.psu"

version := "1.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  "org.postgresql" % "postgresql" % "42.2.1",
  "com.h2database" % "h2" % "1.4.196",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.atlassian.commonmark" % "commonmark" % "0.11.0",
  "com.atlassian.commonmark" % "commonmark-ext-gfm-tables" % "0.9.0",
  "commons-io" % "commons-io" % "2.6",
)

resolvers += Resolver.typesafeRepo("releases")

javacOptions := Seq(
  "-Xlint:deprecation"
)