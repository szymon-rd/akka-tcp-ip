package pl.jaca.tcp.stack

import java.util

import akka.actor.Actor
import akka.stream.{Attributes, Inlet, SinkShape, SourceShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode
import org.pcap4j.core.{PcapHandle, Pcaps}
import org.pcap4j.packet.{EthernetPacket, Packet}
import pl.jaca.tcp.stack.EthernetSender.SendFrame

/**
  * @author Jaca777
  *         Created 2018-02-10 at 18
  */
class EthernetSender(deviceHandle: PcapHandle) extends Actor {

  override def receive = {
    case SendFrame(ethernetPacket) =>
      sendEthPacket(ethernetPacket)
  }

  def sendEthPacket(packet: EthernetPacket): Unit = {
    val packetData = (packet.dmac ++ packet.smac ++ getEthTypeBytes(packet)) ++ packet.payload.array()
    deviceHandle.sendPacket(packetData)
  }

  private def getEthTypeBytes(packet: EthernetPacket): Array[Byte] = {
    Array((packet.ethType >> 8)& 0xff, packet.ethType & 0xff)
      .map(_.asInstanceOf[Byte])
  }
}

object EthernetSender {
  case class SendFrame(ethernetPacket: EthernetPacket)
}

