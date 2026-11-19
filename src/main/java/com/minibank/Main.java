package com.minibank;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Main {

  public static void main(String[] args) {

    BankService bank = new BankService();

    Client client = bank.registerClient(
        "Ivan Petrov",
        "ivan@example.com"
    );

    Account account1 = bank.openAccount(
        client.getId(),
        "ACC-001"
    );

    Account account2 = bank.openAccount(
        client.getId(),
        "ACC-002"
    );

    setBalance(account1.getId(), new BigDecimal("1000.00"));

    System.out.println("Начальные балансы");
    printBalances(bank, account1.getId(), account2.getId());

    System.out.println("\n Обычный перевод");
    bank.transfer(
        account1.getId(),
        account2.getId(),
        new BigDecimal("100.00")
    );

    printBalances(bank, account1.getId(), account2.getId());

    System.out.println("\nПеревод на несуществующий счёт");
    BigDecimal balanceBefore = bank.getAccount(account1.getId()).getBalance();
    try {
      bank.transfer(
          account1.getId(),
          123456789L,
          new BigDecimal("50.00")
      );
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
    }

    BigDecimal balanceAfter = bank.getAccount(account1.getId()).getBalance();
    System.out.println("Баланс до попытки: " + balanceBefore);
    System.out.println("Баланс после попытки: " + balanceAfter);
    System.out.println("Деньги не списаны: " + (balanceBefore.compareTo(balanceAfter) == 0));

    System.out.println("\nПеревод при недостаточном балансе");
    try {
      bank.transfer(
          account1.getId(),
          account2.getId(),
          new BigDecimal("10000.00")
      );
    } catch (IllegalStateException e) {
      System.out.println("Error: " + e.getMessage());
    }

    printBalances(bank, account1.getId(), account2.getId());
  }

  private static void setBalance(Long accountId, BigDecimal balance) {

    String sql = "UPDATE accounts SET balance = ? WHERE id = ?";

    try (Connection connection = DataBase.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {

      ps.setBigDecimal(1, balance);
      ps.setLong(2, accountId);
      ps.executeUpdate();

    } catch (Exception e) {
      throw new RuntimeException("Failed to set account balance", e);
    }
  }

  private static void printBalances(
      BankService bank,
      Long account1Id,
      Long account2Id
  ) {
    System.out.println("Account 1: " + bank.getAccount(account1Id).getBalance());
    System.out.println("Account 2: " + bank.getAccount(account2Id).getBalance());
  }
}
