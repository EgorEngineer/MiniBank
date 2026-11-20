package com.minibank.server.http;

import com.minibank.BankService;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class BankHttpServer {

  private final HttpServer server;

  public BankHttpServer(BankService bankService, int port) throws IOException {
    server = HttpServer.create(new InetSocketAddress(port), 0);

    server.createContext("/transfer", new TransferHandler(bankService));
    server.createContext("/clients", new ClientHandler(bankService));
    server.createContext("/accounts", new AccountHandler(bankService));

    server.setExecutor(null);
  }

  public void start() {
    server.start();
    System.out.println("MiniBank HTTP-server started on port " + server.getAddress().getPort());
  }

  public void stop() {
    server.stop(0);
  }
}
