package pl.jaca.tcp.stack

import java.nio.ByteBuffer
import java.util

import akka.actor.{Actor, ActorRef}
import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode
import org.pcap4j.core.{PcapHandle, Pcaps}

/**
  * @author Jaca777
  *         Created 2018-02-10 at 18
  */
class EthernetReceiver(deviceHandle: PcapHandle) extends GraphStage[SourceShape[Option[EthernetPacket]]] {
  val out: Outlet[Option[EthernetPacket]] = Outlet("PacketSource")
  override val shape: SourceShape[Option[EthernetPacket]] = SourceShape(out)


  override def createLogic(inheritedAttributes: Attributes) =
    new GraphStageLogic(shape) {

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          val packet: Array[Byte] = deviceHandle.getNextRawPacket
          if (packet == null) push(out, None)
          else {
            val ethPacket: EthernetPacket = decodePacket(packet)
            push(out, Some(ethPacket))
          }
        }
      })
    }

  private val byteBuffer = ByteBuffer.allocate(2048)

  private def decodePacket(packet: Array[Byte]) = {
    byteBuffer.put(packet)
    byteBuffer.flip()
    val dmac = Array.ofDim[Byte](6)
    val smac = Array.ofDim[Byte](6)
    byteBuffer.get(dmac)
    byteBuffer.get(smac)
    val etherType = byteBuffer.getShort
    val payload = Array.ofDim[Byte](packet.size - byteBuffer.position())
    byteBuffer.get(payload)
    byteBuffer.clear()
    EthernetPacket(dmac, smac, etherType, payload)
  }
}