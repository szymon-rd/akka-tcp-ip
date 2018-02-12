package pl.jaca.tcp.system

import akka.actor.{Actor, Props}
import pl.jaca.tcp.stack.EthernetPacket
import pl.jaca.tcp.system.FrameHandler.IncomingFrame

/**
  * @author Jaca777
  *         Created 2018-02-12 at 22
  */
class FrameHandler extends Actor {

  val arpHandler = context.actorOf(Props(new ARPService))
  val ipv4Handler = context.actorOf(Props(new IPv4Service))

  override def receive = {
    case frame: IncomingFrame =>
      handleFrame(frame)
  }

  def handleFrame(frame: IncomingFrame): Unit = {
    (frame.frame.ethType match {
      case 0x0806 => arpHandler
      case 0x0800 => ipv4Handler
    }) ! frame
  }

}
object FrameHandler {
  object Finalize
  case class IncomingFrame(frame: EthernetPacket)
}