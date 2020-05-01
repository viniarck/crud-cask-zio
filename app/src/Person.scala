package app
import java.util.UUID

abstract sealed class PersonBase(name: String, age: Int)

case class Person(name: String, age: Int) extends PersonBase(name, age)
case class PersonInDB(uuid: UUID, name: String, age: Int)
    extends PersonBase(name, age)
