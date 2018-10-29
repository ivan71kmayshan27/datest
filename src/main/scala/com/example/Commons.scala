package com.example

import cats.data.{EitherT, Validated, WriterT}

import scala.concurrent.Future

object Commons {
  trait BusinessError

  type ErrorOr[A]  = EitherT[Future, BusinessError, A]
  type ErrorsOr[A] = Either[List[String], A]
  type ValidOr[A]  = Validated[List[String], A]

  type LoggedWith[L, V] = WriterT[ErrorOr, Vector[L], V]
}
