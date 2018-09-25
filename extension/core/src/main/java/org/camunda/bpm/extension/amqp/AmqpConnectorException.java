package org.camunda.bpm.extension.amqp;

public class AmqpConnectorException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AmqpConnectorException(String message) {
    super(message);
  }

  public AmqpConnectorException(Throwable cause) {
    super(cause);
  }

  public AmqpConnectorException(String message, Throwable cause) {
    super(message, cause);
  }

}
