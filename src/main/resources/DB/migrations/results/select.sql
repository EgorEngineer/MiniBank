EXPLAIN ANALYZE
SELECT id, account_id, amount, type, transaction_date
FROM transactions
WHERE account_id = (SELECT id FROM accounts WHERE account_number = 'SEED-ACC-001')
  AND transaction_date BETWEEN now() - interval '7' AND now();


DROP INDEX IF EXISTS idx_transactions_account_date;
