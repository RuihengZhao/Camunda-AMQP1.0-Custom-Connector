package org.camunda.bpm.extension.amqp.send;

import java.util.List;

import javax.mail.Message;
import javax.mail.Transport;

import org.camunda.connect.impl.AbstractRequestInvocation;
import org.camunda.connect.spi.ConnectorRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendAmqpInvocation extends AbstractRequestInvocation<Message> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SendAmqpInvocation.class);

  // protected final MailService mailService;

  public SendAmqpInvocation(Message message, SendAmqpRequest request, List<ConnectorRequestInterceptor> requestInterceptors) {
    super(message, request, requestInterceptors);
  }

  @Override
  public Object invokeTarget() throws Exception {
    return null;
  }

}
