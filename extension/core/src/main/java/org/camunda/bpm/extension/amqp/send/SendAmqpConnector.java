package org.camunda.bpm.extension.amqp.send;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.camunda.bpm.extension.amqp.EmptyResponse;
import org.apache.qpid.jms.message.JmsMessage;
import org.camunda.bpm.extension.amqp.AmqpConnectorException;
import org.camunda.connect.impl.AbstractConnector;
import org.camunda.connect.spi.ConnectorResponse;

import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

public class SendAmqpConnector extends AbstractConnector<SendAmqpRequest, EmptyResponse> {

  public static final String CONNECTOR_ID = "amqp-send";

  public SendAmqpConnector() {
    super(CONNECTOR_ID);
  }

  @Override
  public SendAmqpRequest createRequest() {
    return new SendAmqpRequest(this);
  }

  @Override
  public ConnectorResponse execute(SendAmqpRequest request) {

    try {
      String ConnectionString = request.getConnectionString();
      String QueueName = request.getQueueName();
      String messageBody = request.getMessage();
      String TopicName = request.getTopicName();
      String TopicSubscription;
      if (TopicName == null) {
        TopicSubscription = null;
      } else {
        TopicSubscription = TopicName + "/Subscriptions/" + request.getTopicSubscription();
      }

      System.out.printf("Connection String: " + ConnectionString + "\n");
      System.out.printf("Queue Name: " + QueueName + "\n");
      System.out.printf("Message Body: " + messageBody + "\n");
      System.out.printf("Topic Name: " + TopicName + "\n");
      System.out.printf("Topic Subscription: " + TopicSubscription + "\n");

      ConnectionStringBuilder csb = new ConnectionStringBuilder(ConnectionString);

      Hashtable<String, String> hashtable = new Hashtable<>();
      hashtable.put("connectionfactory.SBCF",
          "amqps://" + csb.getEndpoint().getHost() + "?amqp.idleTimeout=120000&amqp.traceFrames=true");
      hashtable.put("queue.QUEUE", QueueName);
      hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");

      Context context = new InitialContext(hashtable);

      // String remoteURI = "amqps://" + csb.getEndpoint().getHost() +
      // "?amqp.idleTimeout=120000&amqp.traceFrames=true";
      // JmsConnectionFactory myFactory = new JmsConnectionFactory(remoteURI);
      // ConnectionFactory cf = myFactory;
      ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");

      // JmsQueue myQueue = new JmsQueue(QueueName);
      // Destination queue = myQueue;
      Destination queue = (Destination) context.lookup("QUEUE");

      {
        Connection connection = cf.createConnection(csb.getSasKeyName(), csb.getSasKey());
        javax.jms.Session session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);

        MessageProducer producer = session.createProducer(queue);

        BytesMessage m = session.createBytesMessage();
        String stringMessage = messageBody;
        m.writeBytes(stringMessage.getBytes());

        producer.send(m);
        System.out.printf("Message Sent.\n");

        producer.close();
        session.close();
        connection.stop();
        connection.close();
      }

      if (TopicName != null) {
        // set up the JNDI context
        Hashtable<String, String> hashtableTopic = new Hashtable<>();
        hashtableTopic.put("connectionfactory.SBCF",
            "amqps://" + csb.getEndpoint().getHost() + "?amqp.idleTimeout=120000&amqp.traceFrames=true");
        hashtableTopic.put("topic.TOPIC", TopicName);
        hashtableTopic.put("queue.SUBSCRIPTION1", TopicSubscription);
        hashtableTopic.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        Context contextTopic = new InitialContext(hashtableTopic);

        ConnectionFactory cfTopic = (ConnectionFactory) contextTopic.lookup("SBCF");

        boolean response = receiveFromSubscription(csb, contextTopic, cfTopic, "SUBSCRIPTION1");

        System.out.printf("Response " + response + "\n");
        return new SendAmqpResponse(response);
      } else {
        return new EmptyResponse();
      }

    } catch (Exception e) {
      throw new AmqpConnectorException("ERROR - ", e);
    }
  }

  private boolean receiveFromSubscription(ConnectionStringBuilder csb, Context context, ConnectionFactory cf,
      String name) throws NamingException, JMSException, InterruptedException {
    AtomicInteger totalReceived = new AtomicInteger(0);
    System.out.printf("Subscription %s: \n", name);

    Destination subscription = (Destination) context.lookup(name);
    Connection connection = cf.createConnection(csb.getSasKeyName(), csb.getSasKey());
    connection.start();

    Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

    MessageConsumer consumer = session.createConsumer(subscription);

    consumer.setMessageListener(message -> {
      try {
        System.out.printf("Received message %d with sq#: %s\n", totalReceived.incrementAndGet(), // increments the counter
            message.getJMSMessageID());

        System.out.printf(message.toString());
        message.acknowledge();
      } catch (Exception e) {
        System.out.printf("%s", e.toString());
      }
    });

    // wait on the main thread until all sent messages have been received
    while (totalReceived.get() < 1) {
      Thread.sleep(1000);
    }
    consumer.close();
    session.close();
    connection.stop();
    connection.close();

    System.out.printf("Received message.\n");
    System.out.printf("Closing topic client.\n");

    return true;
  }

}
