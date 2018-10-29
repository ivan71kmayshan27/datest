package com.example.configs

import com.example.Commons.ValidOr

object MongoSettings extends ConfigReader {
  override val configPath = "/opt/datestConfig/application.conf"
  lazy val Uri  = "mongo.uri"
  lazy val Name = "mongo.db.name"

  def uri: ValidOr[String]    = read[String](Uri)
  def dbName: ValidOr[String] = read[String](Name)
}
