## Без исправлений
```text
Hibernate: 
    select
        c1_0.id,
        c1_0.email,
        c1_0.name 
    from
        clients c1_0 
    order by
        c1_0.id
Hibernate: 
    select
        a1_0.client_id,
        a1_0.id,
        a1_0.account_number,
        a1_0.balance 
    from
        accounts a1_0 
    where
        a1_0.client_id=?
Hibernate: 
    select
        a1_0.client_id,
        a1_0.id,
        a1_0.account_number,
        a1_0.balance 
    from
        accounts a1_0 
    where
        a1_0.client_id=?
Hibernate: 
    select
        a1_0.client_id,
        a1_0.id,
        a1_0.account_number,
        a1_0.balance 
    from
        accounts a1_0 
    where
        a1_0.client_id=?
Hibernate: 
    select
        a1_0.client_id,
        a1_0.id,
        a1_0.account_number,
        a1_0.balance 
    from
        accounts a1_0 
    where
        a1_0.client_id=?
Hibernate: 
    select
        a1_0.client_id,
        a1_0.id,
        a1_0.account_number,
        a1_0.balance 
    from
        accounts a1_0 
    where
        a1_0.client_id=?
Hibernate: 
    select
        a1_0.client_id,
        a1_0.id,
        a1_0.account_number,
        a1_0.balance 
    from
        accounts a1_0 
    where
        a1_0.client_id=?
Hibernate: 
    select
        a1_0.client_id,
        a1_0.id,
        a1_0.account_number,
        a1_0.balance 
    from
        accounts a1_0 
    where
        a1_0.client_id=?
Hibernate: 
    select
        a1_0.client_id,
        a1_0.id,
        a1_0.account_number,
        a1_0.balance 
    from
        accounts a1_0 
    where
        a1_0.client_id=?
```
Hibernate сначала загружает всех клиентов одним запросом,
а затем для каждого клиента выполняет доп.запрос на загрузку его счетов.
Итого 1 + N запросов, в данном случае получается 1 + 8 = 9.


## Исправленный N+1

```text
Hibernate:
    select
        distinct c1_0.id,
        a1_0.client_id,
        a1_0.id,
        a1_0.account_number,
        a1_0.balance,
        c1_0.email,
        c1_0.name
    from
        clients c1_0
    left join
        accounts a1_0
            on c1_0.id=a1_0.client_id
    order by
        c1_0.id
```
После исправления остался только 1 запрос через left join.
Hibernate инициализирует коллекцию accounts сразу вместе с clients,
поэтому дополнительные запросы больше не выполняются.