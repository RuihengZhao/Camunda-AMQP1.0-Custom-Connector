package org.camunda.bpm.extension.amqp;

import org.camunda.bpm.extension.amqp.send.SendAmqpConnector;
import org.camunda.connect.Connectors;

public class AmqpConnectors {

  public static SendAmqpConnector sendMail() {
    return Connectors.getConnector(SendAmqpConnector.CONNECTOR_ID);
  }

}
