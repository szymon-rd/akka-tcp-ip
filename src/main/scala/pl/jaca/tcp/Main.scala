package pl.jaca.tcp

import java.net.InetAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode
import org.pcap4j.core.Pcaps
import pl.jaca.tcp.stack.EthernetReceiver

/**
  * @author Jaca777
  *         Created 2018:02:10 at 16
  */
object Main {

  implicit val system: ActorSystem = ActorSystem("tcp_ip")


  def main(args: Array[String]): Unit = {
    Pcaps.findAllDevs().toArray().foreach(println)
    val handle = Pcaps.getDevByName("\\Device\\NPF_{65C565EC-3440-4314-A43E-4C2C02BF2A9B}").openLive(2048, PromiscuousMode.PROMISCUOUS, 0)
    val receiver = new EthernetReceiver(handle)

    val mac = macToBytes("d8:cb:8a:33:f6:2d")



  }

  private def macToBytes(mac: String): Array[Byte] = {
    mac.split(":")
      .map(s => Integer.parseInt(s, 16))
      .map(_.toByte)
  }

  private def bytesToMac(mac: Array[Byte]): String = {
    mac.map(b => Integer.toUnsignedString(b & 0xFF, 16)).mkString("[", ":", "]")
  }
}
//d8:cb:8a:30:f6:2d