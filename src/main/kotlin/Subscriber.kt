import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import org.zeromq.ZMsg

class Subscriber(
    private val id: String
) {
    private val socket: ZMQ.Socket

    init {
        val context = ZContext()
        socket = context.createSocket(SocketType.REQ)

        socket.connect("tcp://localhost:5555")
    }

    fun subscribe(topic: String) {
        println("SUBSCRIBE|$topic|$id")

        val message = ZMsg()

        message.addString("SUBSCRIBE")
        message.addString(topic)
        message.addString(id)

        message.send(socket)

        val reply = ZMsg.recvMsg(socket)

        println(reply)
    }

    fun unsubscribe(topic: String) {
        val message = ZMsg()

        message.addString("SUBSCRIBE")
        message.addString(topic)
        message.addString(id)

        message.send(socket)

        //socket.recv(0)

        //println(reply)
    }

    fun get(topic: String): Boolean {
        println("GET|$topic")

        val message = ZMsg()

        message.addString("GET")
        message.addString(topic)
        message.addString(id) //TODO: Needed?

        message.send(socket)

        val reply = ZMsg.recvMsg(socket)
        println(reply.toString())

        // Verify if there was no message available on topic.
        if (reply.toString() == "[ No_content ]") {
            // Enter Retry Get mode.
            retryGetMode(topic)
            return false
        }

        return true
    }

    private fun retryGetMode(topic: String) {
        println("> Entered Retry Get mode...")
        Thread.sleep(5_000)
        get(topic)
    }
}
