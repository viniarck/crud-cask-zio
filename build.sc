// build.sc
import mill._, scalalib._

val deps = Agg(ivy"dev.zio::zio:1.0.0-RC18-2",
    ivy"com.lihaoyi::requests:0.6.0",
    ivy"com.lihaoyi::cask:0.6.0",
    ivy"io.circe::circe-core:0.12.3",
    ivy"io.circe::circe-generic:0.12.3",
    ivy"io.circe::circe-parser:0.12.3",
    ivy"org.tpolecat::doobie-core:0.8.8",
    ivy"org.tpolecat::doobie-postgres:0.8.8",
  )

val baseVersion = "2.13.1"

object app extends ScalaModule {
  def scalaVersion = baseVersion
  def ivyDeps = deps
}
