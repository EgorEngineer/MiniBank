package com.minibank.server.exception;

public class InsufficientFundsException extends RuntimeException {

  public InsufficientFundsException(Long accountId) {
    super("Not enough money on account " + accountId);
  }
}
