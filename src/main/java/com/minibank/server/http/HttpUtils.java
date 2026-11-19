package com.minibank.server.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class HttpUtils {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private HttpUtils() {
  }

  public static <T> T readJson(HttpExchange exchange, Class<T> type)
      throws IOException {
    return MAPPER.readValue(exchange.getRequestBody(), type);
  }

  public static void sendResponse(
      HttpExchange exchange,
      int statusCode,
      Object response
  ) throws IOException {

    byte[] body = MAPPER.writeValueAsString(response)
        .getBytes(StandardCharsets.UTF_8);

    exchange.getResponseHeaders().set(
        "Content-Type",
        "application/json; charset=UTF-8"
    );

    exchange.sendResponseHeaders(statusCode, body.length);

    try (var output = exchange.getResponseBody()) {
      output.write(body);
    }
  }

  public static void sendError(
      HttpExchange exchange,
      int statusCode,
      String message
  ) throws IOException {

    sendResponse(exchange, statusCode, new ErrorResponse(message));
  }

  public record ErrorResponse(String error) {
  }
}
