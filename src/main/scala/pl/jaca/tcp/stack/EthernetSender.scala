package pl.jaca.tcp.stack

import java.util

import akka.actor.Actor
import akka.stream.{Attributes, Inlet, SinkShape, SourceShape}
import akka.stream.stage.{GraphStage, GraphStageLogic}
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode
import org.pcap4j.core.{PcapHandle, Pcaps}
import org.pcap4j.packet.{EthernetPacket, Packet}

/**
  * @author Jaca777
  *         Created 2018-02-10 at 18
  */
class EthernetSender(deviceHandle: PcapHandle) extends GraphStage[SinkShape[EthernetPacket]] {

  val in: Inlet[EthernetPacket] = Inlet("PacketSink")

  override def shape = SinkShape(in)

  override def createLogic(inheritedAttributes: Attributes) = {

  }
}

