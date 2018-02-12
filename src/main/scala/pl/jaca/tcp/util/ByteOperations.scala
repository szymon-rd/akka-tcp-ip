package pl.jaca.tcp.util

import java.nio.ByteBuffer

/**
  * @author Jaca777
  *         Created 2018-02-12 at 23
  */
object ByteOperations {
  implicit class ExtendedByteBuffer(byteBuffer: ByteBuffer) {
    def getArray(array: Array[Byte]): Array[Byte] = {
      byteBuffer.get(array)
      array
    }
  }
}
