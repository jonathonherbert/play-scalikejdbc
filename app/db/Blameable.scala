package db

import org.joda.time.DateTime

trait Blameable {
  val createdBy: String
  val createdAt: DateTime
  val updatedBy: String
  val updatedAt: DateTime
}
