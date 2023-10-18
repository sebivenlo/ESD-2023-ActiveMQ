package org.example;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.*;

public class Main {
    public static void main(String[] args) throws Exception {

        ServerLocator locator = ActiveMQClient.createServerLocator("tcp://localhost:61616");

        // In this simple example, we just use one session for both producing and receiving

        ClientSessionFactory factory = locator.createSessionFactory();
        ClientSession session = factory.createSession("artemis", "artemis", true, true, true, true, 1);

        // A producer is associated with an address ...

        ClientProducer producer = session.createProducer("example");
        ClientMessage message = session.createMessage(true);
        message.getBodyBuffer().writeString("Hello");

        // We need a queue attached to the address ...
        try {
            QueueConfiguration queueConfiguration = new QueueConfiguration();
            queueConfiguration.setAddress("example");
            queueConfiguration.setRoutingType(RoutingType.MULTICAST);
            queueConfiguration.setName("example");
            queueConfiguration.setDurable(true);
            session.createQueue(queueConfiguration);

        } catch (ActiveMQException e) {
        }
        try {
            QueueConfiguration queueConfiguration2 = new QueueConfiguration();
            queueConfiguration2.setAddress("example");
            queueConfiguration2.setRoutingType(RoutingType.MULTICAST);
            queueConfiguration2.setName("example2");
            queueConfiguration2.setDurable(true);
            session.createQueue(queueConfiguration2);
        } catch (ActiveMQException e) {
        }

        // And a consumer attached to the queue ...

        ClientConsumer consumer = session.createConsumer("example");
        ClientConsumer consumer2 = session.createConsumer("example2");

        // Once we have a queue, we can send the message ...

        producer.send(message);

        // We need to start the session before we can -receive- messages ...

        session.start();
        ClientMessage msgReceived = consumer.receive();

        System.out.println("message = " + msgReceived.getBodyBuffer().readString());
        ClientMessage msgReceived2 = consumer2.receive();
        System.out.println("message = " + msgReceived2.getBodyBuffer().readString());

        session.close();


    }
}