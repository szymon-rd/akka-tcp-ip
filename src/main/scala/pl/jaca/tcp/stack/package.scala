package pl.jaca.tcp

import java.nio.ByteBuffer

/**
  * @author Jaca777
  *         Created 2018-02-10 at 22
  */
package object stack {

  case class EthernetPacket(
    dmac: Array[Byte],
    smac: Array[Byte],
    ethType: Short,
    payload: ByteBuffer
  )



}
