package me.koopal.crypto.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.softwaremill.macwire._
import me.koopal.crypto.api.BinanceConnectorService
import play.api.LoggerConfigurator

import scala.collection.immutable

class CryptoLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new BinanceConnectorApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    val environment = context.playContext.environment
    LoggerConfigurator(environment.classLoader).foreach {
      _.configure(environment)
    }

    new BinanceConnectorApplication(context) with LagomDevModeComponents
  }

  override def describeService = Some(readDescriptor[BinanceConnectorService])
}

abstract class BinanceConnectorApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[BinanceConnectorService](wire[BinanceConnectorServiceImpl])

  // Register the JSON serializer registry
  object SerializationRegistry extends JsonSerializerRegistry {
    override def serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq()
  }

  override lazy val jsonSerializerRegistry = SerializationRegistry
}
