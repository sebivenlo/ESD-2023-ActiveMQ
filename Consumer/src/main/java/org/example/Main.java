package org.example;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.client.*;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerLocator locator = ActiveMQClient.createServerLocator("tcp://localhost:61616");

        // In this simple example, we just use one session for both producing and receiving

        ClientSessionFactory factory = locator.createSessionFactory();
        ClientSession session = factory.createSession("artemis", "artemis", true, true, true, true, 1);

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
        // And a consumer attached to the queue ...

        ClientConsumer consumer = session.createConsumer("example");

        // We need to start the session before we can -receive- messages ...
        consumer.setMessageHandler(message -> System.out.println("message = " + message.getBodyBuffer().readString()));
        session.start();
        TimeUnit.MINUTES.sleep(5);
//        while (true) {
//            ClientMessage msgReceived = consumer.receive();
//            System.out.println("message = " + msgReceived.getBodyBuffer().readString());
//        }

    }
}
