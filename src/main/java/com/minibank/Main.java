package com.minibank;

import com.minibank.server.http.BankHttpServer;

public class Main {

  public static void main(String[] args) throws Exception {
    BankService bank = new BankService();

    BankHttpServer server = new BankHttpServer(bank, 8000);
    server.start();
  }
}
