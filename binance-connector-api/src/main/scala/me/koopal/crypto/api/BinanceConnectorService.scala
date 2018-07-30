package me.koopal.crypto.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait BinanceConnectorService extends Service {

  def test(): ServiceCall[NotUsed, String]

  override def descriptor = {
    import Service._

    named("binance-connector")
      .withCalls(
        pathCall("/test", test _)
      )
  }
}
