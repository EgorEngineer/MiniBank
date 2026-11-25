package com.minibank;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "accounts")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "account_number", nullable = false, length = 34)
  private String accountNumber;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal balance = BigDecimal.ZERO;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL,
      orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Transaction> transactions = new ArrayList<>();

  protected Account() {
  }

  public Account(Client client, String accountNumber) {
    this.client = client;
    this.accountNumber = accountNumber;
  }

  public Long getId() {
    return id;
  }

  public Long getClientId() {
    return client == null ? null : client.getId();
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Account other)) {
      return false;
    }
    return accountNumber != null && accountNumber.equals(other.getAccountNumber());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(accountNumber);
  }
}
