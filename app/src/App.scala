package app

import zio._
import zio.blocking._
import zio.Runtime
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import scala.util.Random
import cask.Request
import cask.Response

import doobie._
import doobie.implicits._
import cats.effect.IO
import scala.concurrent.ExecutionContext

import app.Person

object MinimalApplication extends cask.MainRoutes {

  val runtime = Runtime.default

  implicit val cs = IO.contextShift(ExecutionContext.global)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:app",
    "db_user",
    "db_pass"
  )

  def randomFailure(prob: Int, max: Int = 10): Int = {
    val r = new Random()
    if (prob < r.nextInt(max)) prob
    else {
      println("crashed")
      throw new RuntimeException("failed")
    }
  }

  def process(i: Int): ZIO[zio.blocking.Blocking, Throwable, Int] = {
    effectBlocking {
      println(s"started for $i")
      Thread.sleep(i * 1000)
      randomFailure(5)
      println(s"done for $i")
      i
    }.retryUntil(_ => false)
  }

  @cask.post("/person-blocking-test")
  def postPersonZIO(request: Request): Response[String] = {
    decode[Person](request.text()) match {
      case Left(err) => Response(err.toString, statusCode = 400)
      case Right(p) => {
        runtime.unsafeRunAsync_(process(p.age))
        Response("", statusCode = 201)
      }
    }
  }

  @cask.get("/person/:uuid")
  def getPerson(request: Request, uuid: String): Response[String] = {
    sql"select name, age from person where uuid = $uuid"
      .query[Person]
      .option
      .transact(xa)
      .unsafeRunSync match {
      case Some(p) => Response(p.toString, 200)
      case None    => Response("", 404)
    }
  }

  @cask.post("/person")
  def postPerson(request: Request): Response[String] = {
    decode[Person](request.text()) match {
      case Left(err) => Response(err.toString, statusCode = 400)
      case Right(p) => {
        try {
          sql"insert into person (uuid, name, age) values (${new Random()
            .nextInt(1000)
            .toString}, ${p.name}, ${p.age})".update.run
            .transact(xa)
            .unsafeRunSync match {
            case v if v > 0 => Response("", 201)
            case _          => Response("", 400)
          }
        } catch {
          case e: org.postgresql.util.PSQLException => Response(e.toString, 400)
        }
      }
    }
  }

  initialize()
}
