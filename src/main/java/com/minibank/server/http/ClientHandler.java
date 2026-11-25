package com.minibank.server.http;

import com.minibank.BankService;
import com.minibank.Client;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class ClientHandler implements HttpHandler {

  private final BankService bankService;

  public ClientHandler(BankService bankService) {
    this.bankService = bankService;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    if (!exchange.getRequestMethod().equals("POST")) {
      HttpUtils.sendError(exchange, 405, "Method not allowed");
      return;
    }

    try {
      ClientRequest request =
          HttpUtils.readJson(exchange, ClientRequest.class);

      if (request.name() == null || request.name().isBlank()
          || request.email() == null || request.email().isBlank()) {

        HttpUtils.sendError(exchange, 400, "Name and email are required");
        return;
      }

      Client client = bankService.registerClient(
          request.name(),
          request.email()
      );

      HttpUtils.sendResponse(exchange, 200, ClientResponse.from(client));

    } catch (IOException e) {
      HttpUtils.sendError(exchange, 400, "Invalid JSON");

    } catch (RuntimeException e) {
      e.printStackTrace();
      HttpUtils.sendError(exchange, 500, "Internal server error");
    }
  }

  public record ClientRequest(String name, String email) {
  }

  public record ClientResponse(Long id, String name, String email) {

    public static ClientResponse from(Client client) {
      return new ClientResponse(client.getId(), client.getName(), client.getEmail());
    }
  }
}
