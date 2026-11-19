package com.minibank.server.exception;

public class ClientNotFoundException extends RuntimeException {

  public ClientNotFoundException(Long clientId) {
    super("Client not found: " + clientId);
  }
}
