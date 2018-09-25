package org.camunda.bpm.extension.amqp.send;

import org.camunda.bpm.extension.amqp.EmptyResponse;
import org.camunda.connect.impl.AbstractConnectorRequest;
import org.camunda.connect.spi.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendAmqpRequest extends AbstractConnectorRequest<EmptyResponse> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SendAmqpRequest.class);

  protected static final String PARAM_CONNECTION_STRING = "connectionString";
  protected static final String PARAM_QUEUE_NAME = "queueName";
  protected static final String PARAM_MESSAGE = "message";
  protected static final String PARAM_TOPIC_NAME = "topicName";
  protected static final String PARAM_TOPIC_SUBSCRIPTION = "topicSubscription";

  public SendAmqpRequest(Connector<?> connector) {
    super(connector);
  }


  public String getConnectionString() {
    return getRequestParameter(PARAM_CONNECTION_STRING);
  }

  public SendAmqpRequest connectionString(String connectionString) {
    setRequestParameter(PARAM_CONNECTION_STRING, connectionString);
    return this;
  }

  public String getQueueName() {
    return getRequestParameter(PARAM_QUEUE_NAME);
  }

  public SendAmqpRequest queueName(String queueName) {
    setRequestParameter(PARAM_QUEUE_NAME, queueName);
    return this;
  }

  public String getMessage() {
    return getRequestParameter(PARAM_MESSAGE);
  }

  public SendAmqpRequest message(String message) {
    setRequestParameter(PARAM_MESSAGE, message);
    return this;
  }

  public String getTopicName() {
    return getRequestParameter(PARAM_TOPIC_NAME);
  }

  public SendAmqpRequest topicName(String topicName) {
    setRequestParameter(PARAM_TOPIC_NAME, topicName);
    return this;
  }

  public String getTopicSubscription() {
    return getRequestParameter(PARAM_TOPIC_SUBSCRIPTION);
  }

  public SendAmqpRequest topicSubscription(String topicSubscription) {
    setRequestParameter(PARAM_TOPIC_SUBSCRIPTION, topicSubscription);
    return this;
  }


  @Override
  protected boolean isRequestValid() {

    if(getConnectionString() == null || getConnectionString().isEmpty()) {
      LOGGER.warn("invalid request: missing parameter 'connectionString' in {}", this);
      return false;
    }

    if(getQueueName() == null || getQueueName().isEmpty()) {
      LOGGER.warn("invalid request: missing parameter 'queueName' in {}", this);
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    return "";
  }

}
