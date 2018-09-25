package org.camunda.bpm.extension.amqp.send;

import org.camunda.connect.spi.Connector;
import org.camunda.connect.spi.ConnectorProvider;

public class SendAmqpProvider implements ConnectorProvider {

  @Override
  public String getConnectorId() {
    return SendAmqpConnector.CONNECTOR_ID;
  }

  @Override
  public Connector<?> createConnectorInstance() {
    return new SendAmqpConnector();
  }

}
