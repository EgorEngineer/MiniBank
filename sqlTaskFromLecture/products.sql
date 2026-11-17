CREATE TABLE products
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name              CHAR(255),
    price             BIGINT,
    category          CHAR(50),
    in_stock          BOOLEAN,
    date_of_incomming DATE
);

INSERT INTO products (name, price, category, in_stock, date_of_incomming)
VALUES ('Ноутбук ASUS', 85000, 'Электроника', TRUE, '2025-10-15'),
       ('Смартфон Samsung', 65000, 'Электроника', TRUE, '2026-02-10'),

       ('Java для начинающих', 1500, 'Книги', TRUE, '2025-03-10'),
       ('PostgreSQL на практике', 2000, 'Книги', TRUE, '2025-08-15'),
       ('Изучаем Python', 1800, 'Книги', TRUE, '2024-12-01'),

       ('Офисное кресло', 15000, 'Мебель', TRUE, '2025-01-15'),
       ('Компьютерный стол', 12000, 'Мебель', FALSE, '2024-03-20'),
       ('Футболка', 2000, 'Одежда', TRUE, '2026-09-10');

-- Товары из категории Электроника
SELECT name, price
FROM products
WHERE category = 'Электроника'
  AND in_stock = TRUE;

-- Увеличение цены книг на 10%
UPDATE products
SET price = price * 1.10
WHERE category = 'Книги';

-- Добавление нового товара
INSERT INTO products (name, price, category, in_stock)
VALUES ('Моя первая PostgreSQL книга', 500, 'Книги', TRUE);

--Удаление товаров, которые нет в наличии
DELETE
FROM products
WHERE in_stock = FALSE;

-- Снизить цену книг, доставленных от полугода до 2 лет назад, на 20% и добавить «(Скидочная позиция)» в название
UPDATE products
SET price = price * 0.80,
    name  = RTRIM(name) || ' (Скидочная позиция)'
WHERE category = 'Книги'
  AND date_of_incomming BETWEEN CURRENT_DATE - INTERVAL '2 years'
    AND CURRENT_DATE - INTERVAL '6 months';
