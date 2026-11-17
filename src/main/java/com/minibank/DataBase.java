package com.minibank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataBase {

  private static final String URL = "jdbc:postgresql://localhost:5432/MiniBank";
  private static final String USER = "postgres";
  private static final String PASSWORD = "students";

  private DataBase() {
  }

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}