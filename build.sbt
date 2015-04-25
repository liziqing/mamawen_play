import com.typesafe.sbt.SbtNativePackager
import play.PlayJava

name := "mamawen"

version := "1.0-SNAPSHOT"


scalaVersion := "2.10.4"


bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/application.conf""""

libraryDependencies ++= Seq(
    javaCore,
    cache,
    javaJpa.exclude("org.hibernate.javax.persistence", "hibernate-jpa-2.0-api"),
    "mysql" % "mysql-connector-java" % "5.1.34",
    "com.edulify" %% "play-hikaricp" % "1.5.2",
    "org.springframework" % "spring-context" % "4.1.4.RELEASE",
    "javax.inject" % "javax.inject" % "1",
    "org.springframework.data" % "spring-data-jpa" % "1.7.2.RELEASE",
    "org.springframework" % "spring-expression" % "4.1.4.RELEASE",
    "org.hibernate" % "hibernate-entitymanager" % "4.3.8.Final",
     "commons-io" % "commons-io" % "2.2",
  "com.github.bingoohuang" % "patchca" % "0.0.1",
   "org.apache.httpcomponents" % "httpclient" % "4.3.6",
   "org.apache.camel"%"camel-core"%"2.14.0",
  "org.apache.camel"%"camel-quartz"%"2.14.0",
"com.typesafe.akka" % "akka-camel_2.10" % "2.3.4",
"commons-codec" % "commons-codec" % "1.10",
"org.igniterealtime.smack" % "smack-core" % "4.0.7",
"org.igniterealtime.smack" % "smack-tcp" % "4.0.7",
"org.igniterealtime.smack" % "smack-extensions" % "4.0.7",
  "org.mockito" % "mockito-core" % "1.9.5" % "test"
)

doc in Compile <<= target.map(_ / "none")

lazy val root = (project in file(".")).enablePlugins(PlayJava)


resolvers += "Edulify Repository" at "http://edulify.github.io/modules/releases/"

