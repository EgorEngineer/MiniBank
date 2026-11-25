package com.minibank;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clients")
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @OneToMany(mappedBy = "client", cascade = CascadeType.ALL,
      orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Account> accounts = new ArrayList<>();

  protected Client() {
  }

  public Client(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public void addAccount(Account account) {
    accounts.add(account);
    account.setClient(this);
  }

  public void removeAccount(Account account) {
    accounts.remove(account);
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<Account> getAccounts() {
    return accounts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Client other)) {
      return false;
    }
    return email != null && email.equals(other.getEmail());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(email);
  }
}
