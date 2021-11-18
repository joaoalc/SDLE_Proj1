import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import org.zeromq.ZMsg

class Broker {
    private var subscribers: MutableMap<String, MutableSet<Subscriber>> = hashMapOf()

    init {
        val context = ZContext()

        val subscriberSocket = context.createSocket(SocketType.ROUTER)
        val publisherSocket = context.createSocket(SocketType.ROUTER)

        subscriberSocket.bind("tcp://*:5555")
        publisherSocket.bind("tcp://*:5556")

        val poller = context.createPoller(2)

        poller.register(subscriberSocket, ZMQ.Poller.POLLIN)
        poller.register(publisherSocket, ZMQ.Poller.POLLIN)
        
        while (true) {
//            val message = ZMsg.recvMsg(publisherSocket)
            val rc = poller.poll(-1)
            
            //  Poll frontend only if we have available workers
            if (rc == -1) {
                println("rc == -1")
                break //  Interrupted
            }
            
            if (poller.pollin(0)) {
                val message = ZMsg.recvMsg(publisherSocket);

                println(message)
            }

            if (poller.pollin(1)) {
                val message = ZMsg.recvMsg(publisherSocket);

                println(message)
            }
        }
    }

    fun subscribe(topic: String, subscriberID: Subscriber) {
        // Add subscriber to existing topic
        if (subscribers.containsKey(topic))
            subscribers[topic]?.add(subscriberID)

        // Create topic if it doesn't exist and add subscriber
        else
            subscribers[topic] = mutableSetOf(subscriberID)
    }

    fun unsubscribe(topic: String, subscriberID: Subscriber) {
        // Remove subscriber from topic
        subscribers[topic]?.remove(subscriberID)
    }
}

fun main() {
    val broker = Broker()
}