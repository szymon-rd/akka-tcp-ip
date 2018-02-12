package pl.jaca.tcp.system

import java.awt.GraphicsDevice

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.pcap4j.core.{PcapHandle, PcapNetworkInterface, Pcaps}
import pl.jaca.tcp.stack.{EthernetReceiver, EthernetSender}
import pl.jaca.tcp.system.FrameHandler.IncomingFrame
import pl.jaca.tcp.system.TcpStack.Start

/**
  * @author Jaca777
  *         Created 2018-02-10 at 17
  */
class TcpStack extends Actor {

  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val config = system.settings.config

  override def receive = idle

  def idle: Receive = {
    case Start =>
      runTcpStack(config.getString("app.interface"))
  }

  def running: Receive = {
    case _ =>
  }

  def runTcpStack(deviceName: String): Unit = {
    val interface = Pcaps.getDevByName(deviceName)
      .openLive(2048, PcapNetworkInterface.PromiscuousMode.NONPROMISCUOUS, 0)
    setupSender(interface)
    setupHandler(interface)
  }

  private def setupSender(interface: PcapHandle) = {
    context.actorOf(Props(new EthernetSender(interface)), "sender")
  }

  private def setupHandler(interface: PcapHandle) = {
    val receiver = new EthernetReceiver(interface)
    val handler = context.actorOf(Props(new FrameHandler), "handler")

    Source.fromGraph(receiver)
      .collect {
        case Some(frame) => frame
      }
      .map(IncomingFrame)
      .runWith(Sink.actorRef(handler, FrameHandler.Finalize))
  }
}

object TcpStack {

  object Start

}