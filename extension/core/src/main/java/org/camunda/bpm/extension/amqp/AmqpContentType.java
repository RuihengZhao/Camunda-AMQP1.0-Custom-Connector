package org.camunda.bpm.extension.amqp;

public enum AmqpContentType {

  TEXT_PLAIN("text/plain; charset=utf-8"),
  TEXT_HTML("text/html; charset=utf-8"),
  MULTIPART("multipart");

  private final String type;

  private AmqpContentType(String type) {
    this.type = type;
  }

  public boolean match(String contentType) {
    return contentType.toLowerCase().contains(type);
  }

  public String getType() {
    return type;
  }

}
