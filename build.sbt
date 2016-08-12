name := "Commonwealth Superannuation Validate Membership Number"

version := "0.0.1"

scalaVersion := "2.11.8"

Seq(webSettings :_*)

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.4"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

libraryDependencies ++= {
  val liftVersion = "2.6.2"
  Seq(
    "net.liftweb"       %% "lift-webkit"        % liftVersion           % "compile",
    "net.liftweb"       %% "lift-testkit"       % liftVersion           % "compile",
    "net.liftweb"       %% "lift-mapper"        % liftVersion           % "compile",
    "net.liftmodules"   %% "fobo_2.6"           % "1.5"                 % "compile",
    "org.eclipse.jetty" % "jetty-webapp"        % "8.1.17.v20150415"    % "container,test",
    "org.eclipse.jetty" % "jetty-plus"          % "8.1.17.v20150415"    % "container,test", // For Jetty Config
    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
    "commons-codec" % "commons-codec" % "1.10"
  )
}