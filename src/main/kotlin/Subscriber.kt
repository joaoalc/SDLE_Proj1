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

    companion object {
        @JvmStatic
        private fun main(args: Array<String>) {

            val subscriber: Subscriber
            if (args.isEmpty()) {
                subscriber = Subscriber("1")
            } else {
                subscriber = Subscriber(args[0])
            }

            subscriber.subscribe("Sapos")
            subscriber.get("Sapos")
        }
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

    fun get(topic: String) {
        println("GET|$topic")

        val message = ZMsg()

        message.addString("GET")
        message.addString(topic)
        message.addString(id) //TODO: Needed?

        message.send(socket)

        val reply = ZMsg.recvMsg(socket)

        println(reply)
    }
}

