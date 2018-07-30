package me.koopal.crypto.api

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.transport.Method.GET
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import me.koopal.crypto.api.BinanceEntities.DepthEvent

trait BinanceStreamingService extends Service {
  def depthStream(symbol: String): ServiceCall[NotUsed, Source[DepthEvent, NotUsed]]

  override final def descriptor = {
    import Service._
    import me.koopal.crypto.api.BinanceEntityMarshallers._

    named("depth-stream")
      .withCalls(
        restCall(GET, "/ws/:symbol@deth", depthStream _)
      )
  }
}
