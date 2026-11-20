package com.minibank.server.http;

import com.minibank.BankService;
import com.minibank.server.exception.AccountNotFoundException;
import com.minibank.server.exception.InsufficientFundsException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.math.BigDecimal;

public class TransferHandler implements HttpHandler {

  private final BankService bankService;

  public TransferHandler(BankService bankService) {
    this.bankService = bankService;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    if (!exchange.getRequestMethod().equals("POST")) {
      HttpUtils.sendError(exchange, 405, "Method not allowed");
      return;
    }

    try {
      TransferRequest request =
          HttpUtils.readJson(exchange, TransferRequest.class);

      if (request.fromAccountId() == null || request.toAccountId() == null
          || request.amount() == null) {

        HttpUtils.sendError(exchange, 400, "Missing required fields");
        return;
      }

      if (request.fromAccountId() <= 0 || request.toAccountId() <= 0
          || request.amount().compareTo(BigDecimal.ZERO) <= 0) {

        HttpUtils.sendError(exchange, 400, "Invalid request data");
        return;
      }

      bankService.transfer(
          request.fromAccountId(),
          request.toAccountId(),
          request.amount()
      );

      HttpUtils.sendResponse(exchange, 200,
          new SuccessResponse("Transfer completed successfully"));

    } catch (AccountNotFoundException e) {
      HttpUtils.sendError(exchange, 404, e.getMessage());

    } catch (InsufficientFundsException e) {
      HttpUtils.sendError(exchange, 409, e.getMessage());

    } catch (IOException e) {
      HttpUtils.sendError(exchange, 400, "Invalid JSON");

    } catch (RuntimeException e) {
      e.printStackTrace();
      HttpUtils.sendError(exchange, 500, "Internal server error");
    }
  }

  public record TransferRequest(
      Long fromAccountId,
      Long toAccountId,
      BigDecimal amount
  ) {
  }

  public record SuccessResponse(String message) {
  }
}
