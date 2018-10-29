package com.example.configs

import java.io.{File, InputStream}
import java.nio.file.{Files, NoSuchFileException, Paths}
import java.time.Duration

import com.example.Commons.{ErrorsOr, ValidOr}
import com.typesafe.config.{Config, ConfigException, ConfigFactory}
import com.example.Commons._

import scala.util.control.Exception.catching
import cats.syntax.either._

trait ConfigReader {
  def configPath: String

  protected lazy val conf: Config = ConfigFactory.parseFile(new File(configPath))

  trait PropertyReader[T] {
    def read(propertyName: String): T
  }

  object PropertyReader {
    implicit val stringReader: PropertyReader[String] =
      (propertyName: String) => conf.getString(propertyName)

    implicit val intReader: PropertyReader[Int] =
      (propertyName: String) => conf.getInt(propertyName)

    implicit val durationReader: PropertyReader[Duration] =
      (propertyName: String) => conf.getDuration(propertyName)

    implicit val bytesReader: PropertyReader[Array[Byte]] =
      (path: String) => Files.readAllBytes(Paths.get(path))

    implicit val inputStreamReader: PropertyReader[InputStream] =
      (path: String) => Files.newInputStream(Paths.get(path))
  }

  implicit def toValidated[V](errorsOr: ErrorsOr[V]): ValidOr[V] =
    errorsOr.toValidated

  implicit def read[V](paramName: String)(implicit reader: PropertyReader[V]): ErrorsOr[V] =
    catching(classOf[ConfigException.Missing], classOf[ConfigException.WrongType], classOf[NoSuchFileException]) either {
      reader.read(paramName)
    } leftMap {
      case e: NoSuchFileException => s"No such file [${e.getMessage}]" :: Nil
      case e                      => e.getMessage :: Nil
    }
}
