package me.koopal.crypto.impl

import java.net.URI

import akka.NotUsed
import akka.stream.Materializer
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.client.{LagomClientApplication, StaticServiceLocatorComponents}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import me.koopal.crypto.api.{BinanceConnectorService, BinanceStreamingService}
import play.api.libs.ws.ahc.AhcWSComponents

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class BinanceConnectorServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit val mat: Materializer, ec: ExecutionContext) extends BinanceConnectorService {
  private val binanceStreamApplication = new LagomClientApplication("binance-ws")
    with StaticServiceLocatorComponents
    with AhcWSComponents {

    override def staticServiceUri = URI.create("wss://stream.binance.com:9443")
  }

  private val binanceStreamClient = binanceStreamApplication.serviceClient.implement[BinanceStreamingService]

  override def test(): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    binanceStreamClient.depthStream("bnbbtc")
      .invoke()
      .map { s =>
        s.runForeach(e => println(e))
      }.onComplete {
      case Success(x) => println("success", x)
      case Failure(ex) => println("failure", ex)
    }

    Future.successful("OK")
  }
}
