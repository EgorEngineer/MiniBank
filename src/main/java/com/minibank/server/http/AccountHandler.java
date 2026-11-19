package com.minibank.server.http;

import com.minibank.BankService;
import com.minibank.server.exception.ClientNotFoundException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public class AccountHandler implements HttpHandler {

  private final BankService bankService;

  public AccountHandler(BankService bankService) {
    this.bankService = bankService;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    if (!exchange.getRequestMethod().equals("POST")) {
      HttpUtils.sendError(exchange, 405, "Method not allowed");
      return;
    }

    try {
      AccountRequest request =
          HttpUtils.readJson(exchange, AccountRequest.class);

      if (request.clientId() == null || request.accountNumber() == null
          || request.accountNumber().isBlank()) {
        HttpUtils.sendError(exchange, 400, "Invalid request data");
        return;
      }

      if (request.clientId() <= 0) {
        HttpUtils.sendError(exchange, 400, "Invalid client ID");
        return;
      }

      var account = bankService.openAccount(
          request.clientId(),
          request.accountNumber()
      );

      HttpUtils.sendResponse(exchange, 200, account);

    } catch (ClientNotFoundException e) {
      HttpUtils.sendError(exchange, 404, e.getMessage());

    } catch (IOException e) {
      HttpUtils.sendError(exchange, 400, "Invalid JSON");

    } catch (RuntimeException e) {
      e.printStackTrace();
      HttpUtils.sendError(exchange, 500, "Internal server error");
    }
  }

  public record AccountRequest(
      Long clientId,
      String accountNumber
  ) {
  }
}
