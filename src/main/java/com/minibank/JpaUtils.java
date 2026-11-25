package com.minibank;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.function.Consumer;
import java.util.function.Function;

public final class JpaUtils {

  private static final EntityManagerFactory EMF =
      Persistence.createEntityManagerFactory("MiniBankPU");

  private JpaUtils() {
  }

  public static <T> T inTransaction(Function<EntityManager, T> work) {
    EntityManager em = EMF.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      T result = work.apply(em);
      tx.commit();
      return result;
    } catch (RuntimeException e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    } finally {
      em.close();
    }
  }

  public static void runInTransaction(Consumer<EntityManager> work) {
    inTransaction(em -> {
      work.accept(em);
      return null;
    });
  }
}
