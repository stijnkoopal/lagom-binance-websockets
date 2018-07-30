package me.koopal.crypto.api

object BinanceEntities {

  type OrderBookItems = Array[OrderBookItem]
  type EventType = String
  type EpochTime = Long
  type Symbol = String
  type UpdateId = Long
  type Price = String
  type Quantity = String

  case class OrderBookItem(price: Price, quantity: Quantity)

  // REST
  case class DepthResponse(lastUpdateId: EpochTime, bids: OrderBookItems, asks: OrderBookItems)

  // Streams
  case class DepthEvent(e: EventType, E: EpochTime, s: Symbol, U: UpdateId, u: UpdateId, b: OrderBookItems, a: OrderBookItems)
}



