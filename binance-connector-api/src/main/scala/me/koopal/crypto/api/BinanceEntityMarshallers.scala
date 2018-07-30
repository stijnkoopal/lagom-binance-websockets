package me.koopal.crypto.api

import me.koopal.crypto.api.BinanceEntities.{DepthEvent, DepthResponse, OrderBookItem}
import play.api.libs.json._

object BinanceEntityMarshallers {
  implicit def eitherReads[A, B](implicit A: Reads[A], B: Reads[B]): Reads[Either[A, B]] =
    Reads[Either[A, B]] { json =>
      A.reads(json) match {
        case JsSuccess(value, path) => JsSuccess(Left(value), path)
        case JsError(e1) => B.reads(json) match {
          case JsSuccess(value, path) => JsSuccess(Right(value), path)
          case JsError(e2) => JsError(JsError.merge(e1, e2))
        }
      }
    }

  implicit def eitherWrites[A, B](implicit A: Writes[A], B: Writes[B]): Writes[Either[A,B]] =
    Writes[Either[A, B]] {
      case Left(a) => A.writes(a)
      case Right(b) => B.writes(b)
    }

  implicit def eitherFormat[A, B](implicit A: Format[A], B: Format[B]): Format[Either[A,B]] =
    Format(eitherReads, eitherWrites)

  implicit val orderBookItemWrites: Writes[OrderBookItem] = Writes { orderBookItem =>
    Json.arr (
      orderBookItem.price,
      orderBookItem.quantity,
      Json.arr()
    )
  }

  implicit val orderBookItemReads: Reads[OrderBookItem] =
    __.read[List[Either[String, Array[String]]]]
      .map(list => OrderBookItem(list.head.left.get, list(1).left.get))
  implicit val depthResponseFormat: Format[DepthResponse] = Json.format[DepthResponse]

  implicit val depthEventFormat: Format[DepthEvent] = Json.format[DepthEvent]
}
