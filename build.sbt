import com.typesafe.config.ConfigFactory

name := """play-exercise"""
organization := "jp.cedretaber"

version := "1.0-SNAPSHOT"

lazy val conf = ConfigFactory.parseFile(new File("conf/application.conf"))

lazy val root = (project in file(".")).enablePlugins(PlayScala, FlywayPlugin)

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "com.typesafe" % "config" % "1.3.2",
  "org.postgresql" % "postgresql" % "42.2.2",
  "org.scalikejdbc" %% "scalikejdbc" % "3.2.2",
  "org.scalikejdbc" %% "scalikejdbc-config"  % "3.2.2",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0-scalikejdbc-3.2",
  "org.scalikejdbc" %% "scalikejdbc-test"   % "3.2.2"   % "test"
)

flywayLocations += "filesystem:conf/db/migration"
flywayUrl := conf.getString("db.default.url")
flywayUser := conf.getString("db.default.username")
flywayPassword := conf.getString("db.default.password")
flywayLocations in Test += "filesystem:conf/db/migration"
flywayUrl in Test := conf.getString("db.test.url")
flywayUser in Test := conf.getString("db.test.username")
flywayPassword in Test := conf.getString("db.test.password")

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "jp.cedretaber.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "jp.cedretaber.binders._"
