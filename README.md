### Goal:
Want to build a Camunda Custom Connector that can send messages to Azure Service Bus Queue/Topic via AMQP 1.0 and also be able to listen to Queue/Topic for the response.

### How did I approach this?
Send message:
 ```
String ConnectionString = request.getConnectionString();
String QueueName = request.getQueueName();
String Message = request.getMessage();

// Using azure-servicebus SDK to parse the Servcice Bus connection string
ConnectionStringBuilder csb = new ConnectionStringBuilder(ConnectionString);

// Set up JNDI conext
Hashtable<String, String> hashtable = new Hashtable<>();
hashtable.put("connectionfactory.SBCF", "amqps://" + csb.getEndpoint().getHost() + "?amqp.idleTimeout=120000&amqp.traceFrames=true");
hashtable.put("queue.QUEUE", QueueName);
hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");

Context context = new InitialContext(hashtable);

// Look up connection factory
ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");
// Look up queue
Destination queue = (Destination) context.lookup("QUEUE");

// Create Connection
Connection connection = cf.createConnection(csb.getSasKeyName(), csb.getSasKey());
// Create Session, no transaction, client ack
Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
// Create Producer
MessageProducer producer = session.createProducer(queue);

// Create Message
BytesMessage message = session.createBytesMessage();
message.writeBytes(stringMessage.getBytes());
// Send Message
producer.send(message);

producer.close();
session.close();
connection.stop();
connection.close();
```
Receive message:
```
String TopicName = request.getTopicName();
String  TopicSubscription = TopicName  + "/Subscriptions/" + request.getTopicSubscription();

// Using azure-servicebus SDK to parse the Servcice Bus connection string
ConnectionStringBuilder csb = new ConnectionStringBuilder(connectionString);

// set up the JNDI context 
Hashtable<String, String> hashtable = new Hashtable<>();
hashtable.put("connectionfactory.SBCF", "amqps://" + csb.getEndpoint().getHost() + "?amqp.idleTimeout=120000&amqp.traceFrames=true");
hashtable.put("topic.TOPIC", TopicName );
hashtable.put("queue.SUBSCRIPTION1", TopicSubscription);
hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
Context context = new InitialContext(hashtable);

ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");

receiveFromSubscription(csb, context, cf, "SUBSCRIPTION1");
```
```
private void receiveFromSubscription(ConnectionStringBuilder csb, Context context, ConnectionFactory cf, String name) 
    throws NamingException, JMSException, InterruptedException {
        System.out.printf("Subscription %s: \n", name);
        Destination subscription = (Destination) context.lookup(name);

        // Create Connection
        Connection connection = cf.createConnection(csb.getSasKeyName(), csb.getSasKey());
        connection.start();
        // Create Session, no transaction, client ack
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        // Create consumer
        MessageConsumer consumer = session.createConsumer(subscription);
        // Set callback listener. Gets called for each received message.
        consumer.setMessageListener(message -> {
            try {
                System.out.printf("Received message %d with sq#: %s\n",
                        totalReceived.incrementAndGet(),  // increments the counter
                        message.getJMSMessageID());
                message.acknowledge();
            } catch (Exception e) {
                System.out.printf("%s", e.toString());
            }
        });

        consumer.close();
        session.close();
        connection.stop();
        connection.close();
    }
}
```

Dependencies required:
- camunda-connect-core
- azure-servicebus [1.1.2]
- javax.jms-api [2.0.1]
- qpid-jms-client [0.34.0]
- proton-j [0.27.1]
- log4j [1.2.4]
- slf4j-log4j12 [1.7.21]
- slf4j-nop [1.7.21]

### How to deploy the connector to Camunda Platform as an extension?
1. Build the JAR using Maven `mvn install` or `mvn install -DskipTests` 
2. Copy the JAR into the Camunda server lib folder (e.g. tomcat folder: `server\apache-tomcat-8.0.24\lib`)
3. Make sure all the dependencies required are also available in the `\lib` folder 

### How to use/config the connector?
1. For any Service Task in BPM, select Connector as implementation.
  ![items](https://user-images.githubusercontent.com/10762987/46025076-1abd6200-c0b6-11e8-8771-058d8f1bdfb2.png)

2. Configure the connector properly.
  ![items 1](https://user-images.githubusercontent.com/10762987/46025533-252c2b80-c0b7-11e8-94fc-bd28b73fc1cd.png)

### Note:
- This POC is using Camunda Community Platform 7.9.0 for Apache Tomcat.
- Make sure there's only one version of javax.jms on the dependency list and it should only be imported once.

### References:
1. https://docs.microsoft.com/en-ca/azure/service-bus-messaging/service-bus-java-how-to-use-jms-api-amqp
2. https://github.com/camunda/camunda-bpm-mail
