CREATE MATERIALIZED VIEW manager_daily_reports AS
SELECT m.name             AS manager_name,
       o.order_date,
       COUNT(o.id)        AS total_orders,
       SUM(o.total_price) AS total_sales
FROM managers m
         JOIN orders o ON o.manager_id = m.id
GROUP BY m.name, o.order_date;

SELECT *
FROM manager_daily_reports;
REFRESH MATERIALIZED VIEW manager_daily_reports;

-- Поиск юзеров по критериям
CREATE VIEW user_search AS
SELECT id,
       name,
       email,
       city,
       created_at
FROM users;

SELECT *
FROM user_search
WHERE city = 'Москва';


-- Статистика популярности товаров
CREATE MATERIALIZED VIEW product_popularity AS
SELECT p.id,
       p.name,
       p.category,
       COALESCE(SUM(o.quantity), 0)    AS total_sold,
       COALESCE(SUM(o.total_price), 0) AS total_revenue
FROM products p
         LEFT JOIN orders o ON o.product_id = p.id
GROUP BY p.id, p.name, p.category;

SELECT *
FROM product_popularity
ORDER BY total_sold DESC;


-- API информация о пользователе
CREATE VIEW user_api_info AS
SELECT u.id,
       u.name,
       u.email,
       u.city,
       COUNT(o.id)                     AS total_orders,
       COALESCE(SUM(o.total_price), 0) AS total_spent
FROM users u
         LEFT JOIN orders o ON o.user_id = u.id
GROUP BY u.id, u.name, u.email, u.city;

SELECT *
FROM user_api_info;

-- Дашборд с реальными метриками
CREATE VIEW dashboard_metrics AS
SELECT (SELECT COUNT(*) FROM users)                          AS total_users,
       (SELECT COUNT(*) FROM products)                       AS total_products,
       (SELECT COUNT(*) FROM orders)                         AS total_orders,
       (SELECT COALESCE(SUM(total_price), 0) FROM orders)    AS total_revenue,
       (SELECT COUNT(*) FROM products WHERE in_stock = TRUE) AS products_in_stock;

SELECT *
FROM dashboard_metrics;


--Анализ рекламных кампаний за месяц
CREATE MATERIALIZED VIEW monthly_campaign_analysis AS
SELECT DATE_TRUNC('month', campaign_date)::DATE AS month,
       COUNT(*)                                 AS total_campaigns,
       SUM(budget)                              AS total_budget,
       SUM(clicks)                              AS total_clicks,
       SUM(conversions)                         AS total_conversions,
       ROUND(
               SUM(conversions) * 100.0 / NULLIF(SUM(clicks), 0),
               2
       )                                        AS conversion_rate
FROM advertising_campaigns
GROUP BY DATE_TRUNC('month', campaign_date);

SELECT *
FROM monthly_campaign_analysis
ORDER BY month;

REFRESH MATERIALIZED VIEW monthly_campaign_analysis;
