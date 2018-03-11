// The Play plugin
resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.12")
addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "4.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")