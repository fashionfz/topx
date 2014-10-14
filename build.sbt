name := "topx"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.jolbox" % "bonecp" % "0.8.0.RELEASE",
  "org.apache.velocity" % "velocity" % "1.7",
  "com.github.mumoshu" %% "play2-memcached" % "0.4.0",
  "commons-collections" % "commons-collections" % "3.2.1",
  "mysql" % "mysql-connector-java" % "5.1.32",
  "org.hibernate" % "hibernate-entitymanager" % "4.2.6.Final",
  "net.sourceforge.jexcelapi" % "jxl" % "2.6.10",
  javaJpa,
  javaJdbc,
  cache,
  filters
)

dependencyOverrides += "com.jolbox" % "bonecp" % "0.8.0.RELEASE"

resolvers += "Spy Repository" at "http://repo1.maven.org/maven2"

play.Project.playJavaSettings