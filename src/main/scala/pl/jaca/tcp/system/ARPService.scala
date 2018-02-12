package pl.jaca.tcp.system

import akka.actor.Actor
import pl.jaca.tcp.stack
import pl.jaca.tcp.system.ARPService.{ARPCacheEntry, ARPPacket}
import pl.jaca.tcp.system.FrameHandler.IncomingFrame
import pl.jaca.tcp.util.ByteOperations._

/**
  * @author Jaca777
  *         Created 2018-02-12 at 23
  */
class ARPService extends Actor {

  private val config = context.system.settings.config
  private val mac = config.getString("tcpip.mac")
  private val ip = config.getString("tcpip.ip")

  override def receive: Receive = receiving(Array.empty)

  private def receiving(arpCache: Array[ARPCacheEntry]): Receive = {
    case IncomingFrame(arpFrame) =>
      val packet = decodeFrame(arpFrame)
      val updatedCache = handlePacket(packet, arpCache)
      context become receiving(updatedCache)
  }

  def decodeFrame(arpFrame: stack.EthernetPacket): ARPPacket = {
    val payload = arpFrame.payload
    ARPPacket(
      payload.getShort(),
      payload.getShort(),
      payload.get(),
      payload.get(),
      payload.getShort(),
      payload.getArray(Array.ofDim[Byte](6)),
      payload.getInt(),
      payload.getArray(Array.ofDim[Byte](6)),
      payload.getInt()
    )
  }

  def handlePacket(packet: ARPPacket, arpCache: Array[ARPCacheEntry]): Array[ARPCacheEntry] = {
    if (packet.hwtype == 0x0001 // ethernet hardware
      && packet.protype == 0x0800 // IPv4
      && packet.hwsize == 6 // 6-byte MAC
      && packet.prosize == 4 // 4-byte IP
    ) {
      val updatedCache = if (arpCache.exists(e => e.sip == packet.sip)) {
        updateARPTable(packet, arpCache)
      } else {
        insertToARPTable(packet, arpCache)
      }
      val sender = context.actorSelection("")
      updatedCache
    } else {
      throw new UnsupportedOperationException("Unable to resolve address for given metadata")
    }
  }

  def updateARPTable(packet: ARPPacket, arpCache: Array[ARPCacheEntry]): Array[ARPCacheEntry] = {
    arpCache.map(entry => if (entry.sip == packet.sip) ARPCacheEntry(entry.hwtype, entry.sip, packet.smac) else entry)
  }
  def insertToARPTable(packet: ARPPacket, arpCache: Array[ARPCacheEntry]): Array[ARPCacheEntry] = {
    arpCache :+ ARPCacheEntry(packet.hwtype, packet.sip, packet.smac)
  }


}

object ARPService {

  case class ARPPacket(
    hwtype: Short,
    protype: Short,
    hwsize: Byte,
    prosize: Byte,
    opcode: Short,
    smac: Array[Byte],
    sip: Int,
    dmac: Array[Byte],
    dip: Int
  )

  case class ARPCacheEntry(
    hwtype: Short,
    sip: Int,
    smac: Array[Byte],
  )

}