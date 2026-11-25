package com.minibank;

import com.minibank.server.exception.AccountNotFoundException;
import com.minibank.server.exception.ClientNotFoundException;
import com.minibank.server.exception.InsufficientFundsException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * MiniBank — "MVP", который наспех собрал предыдущий разработчик:
 * вся логика в одном классе, все данные — в HashMap, персистентности
 * нет вообще (перезапуск процесса — и всё пропало).
 * <p>
 * ПРОБЛЕМА (найдёшь и исправишь, когда дойдёт очередь — модуль 7.1,
 * когда появится @Transactional): в transfer() проверка существования
 * счёта-получателя происходит ПОСЛЕ списания денег со счёта-
 * отправителя. Если счёт-получатель не существует, деньги уже спишутся
 * с первого счёта и "исчезнут" — их некуда вернуть, потому что метод
 * прерывается исключением до зачисления.
 * <p>
 * Эта проблема не решается на HashMap "в лоб" без искусственных
 * костылей (можно вручную откатывать баланс в catch, но это не
 * настоящее решение) — она по-настоящему решается только когда
 * появится транзакционность (сначала в БД — ACID, модуль 4, а затем
 * в коде — @Transactional, модуль 7.1). До этого момента баг просто
 * существует и это нормально: то, что появится в модуле 7.1, — как
 * раз тот инструмент, который проблему полностью закрывает.
 */
public class BankService {

  public Client registerClient(String name, String email) {
    return JpaUtils.inTransaction(em -> {
      Client client = new Client(name, email);
      em.persist(client);
      return client;
    });
  }

  public Account openAccount(Long clientId, String accountNumber) {
    return JpaUtils.inTransaction(em -> {
      Client client = em.find(Client.class, clientId);
      if (client == null) {
        throw new ClientNotFoundException(clientId);
      }

      Account account = new Account(client, accountNumber);
      em.persist(account);
      return account;
    });
  }

  public Account getAccount(Long accountId) {
    return JpaUtils.inTransaction(em -> {
      Account account = em.find(Account.class, accountId);
      if (account == null) {
        throw new AccountNotFoundException(accountId);
      }
      return account;
    });
  }

  public Client getClient(Long clientId) {
    return JpaUtils.inTransaction(em -> {
      Client client = em.find(Client.class, clientId);
      if (client == null) {
        throw new ClientNotFoundException(clientId);
      }
      return client;
    });
  }

  public List<Client> getAllClientsWithAccounts() {
    return JpaUtils.inTransaction(em ->
        em.createQuery("""
                SELECT DISTINCT c
                FROM Client c
                LEFT JOIN FETCH c.accounts
                ORDER BY c.id
                """, Client.class)
            .getResultList()
    );
  }

  public Client getClientWithAccounts(Long clientId) {
    return JpaUtils.inTransaction(em -> {
      List<Client> result = em.createQuery("""
              SELECT DISTINCT c
              FROM Client c
              LEFT JOIN FETCH c.accounts
              WHERE c.id = :clientId
              """, Client.class)
          .setParameter("clientId", clientId)
          .getResultList();

      if (result.isEmpty()) {
        throw new ClientNotFoundException(clientId);
      }
      return result.get(0);
    });
  }

  public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
    JpaUtils.runInTransaction(em -> {
      Account from = em.find(Account.class, fromAccountId);
      if (from == null) {
        throw new AccountNotFoundException(fromAccountId);
      }

      Account to = em.find(Account.class, toAccountId);
      if (to == null) {
        throw new AccountNotFoundException(toAccountId);
      }

      if (from.getBalance().compareTo(amount) < 0) {
        throw new InsufficientFundsException(fromAccountId);
      }

      from.setBalance(from.getBalance().subtract(amount));
      to.setBalance(to.getBalance().add(amount));

      LocalDateTime now = LocalDateTime.now();
      em.persist(new Transaction(from, amount.negate(), "TRANSFER_OUT", now));
      em.persist(new Transaction(to, amount, "TRANSFER_IN", now));
    });
  }

  public void removeAccountFromClient(Long clientId, Long accountId) {
    JpaUtils.runInTransaction(em -> {
      List<Client> result = em.createQuery("""
              SELECT DISTINCT c
              FROM Client c
              LEFT JOIN FETCH c.accounts
              WHERE c.id = :clientId
              """, Client.class)
          .setParameter("clientId", clientId)
          .getResultList();

      if (result.isEmpty()) {
        throw new ClientNotFoundException(clientId);
      }

      Client client = result.get(0);
      Account account = client.getAccounts().stream()
          .filter(a -> a.getId().equals(accountId))
          .findFirst()
          .orElseThrow(() -> new AccountNotFoundException(accountId));

      client.removeAccount(account);
    });
  }

  public long countAccountsInDb(Long clientId) {
    return JpaUtils.inTransaction(em ->
        em.createQuery(
                "SELECT COUNT(a) FROM Account a WHERE a.client.id = :clientId",
                Long.class)
            .setParameter("clientId", clientId)
            .getSingleResult()
    );
  }
}
