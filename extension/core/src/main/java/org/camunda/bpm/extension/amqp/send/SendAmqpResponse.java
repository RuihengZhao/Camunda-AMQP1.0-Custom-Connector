package org.camunda.bpm.extension.amqp.send;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.*;
// import org.camunda.bpm.extension.mail.dto.Mail;
// import org.camunda.bpm.extension.mail.service.MailService;
import org.camunda.connect.impl.AbstractConnectorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendAmqpResponse extends AbstractConnectorResponse {

  private static final Logger LOGGER = LoggerFactory.getLogger(SendAmqpResponse.class);

  public static final String PARAM_IS_SUCCESS = "isSuccess";

  //   protected final Message message;
  protected final boolean response;

  public SendAmqpResponse(boolean response) {
    // this.message = message;
    this.response = response;
  }

  @Override
  protected void collectResponseParameters(Map<String, Object> responseParameters) {
    System.out.printf("PARAMETER " + response + "\n");
    responseParameters.put(PARAM_IS_SUCCESS, response);
  }


  @Override
  public String toString() {
    return "";
  }

}
