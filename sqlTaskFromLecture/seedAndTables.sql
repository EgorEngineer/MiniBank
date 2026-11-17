CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100),
    email      VARCHAR(150),
    city       VARCHAR(100),
    created_at DATE
);

CREATE TABLE products
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255),
    price    BIGINT,
    category VARCHAR(50),
    in_stock BOOLEAN
);

CREATE TABLE managers
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150),
    department VARCHAR(100)
);

CREATE TABLE orders
(
    id          SERIAL PRIMARY KEY,
    user_id     INT    NOT NULL REFERENCES users (id),
    product_id  INT    NOT NULL REFERENCES products (id),
    manager_id  INT    NOT NULL REFERENCES managers (id),
    quantity    INT    NOT NULL,
    total_price BIGINT NOT NULL,
    order_date  DATE   NOT NULL
);

CREATE TABLE advertising_campaigns
(
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(150),
    budget        BIGINT,
    clicks        INT,
    conversions   INT,
    campaign_date DATE
);

INSERT INTO users (name, email, city, created_at)
VALUES ('Иван', 'ivan@mail.ru', 'Москва', '2025-01-10'),
       ('Пётр', 'petr@mail.ru', 'Казань', '2025-03-15'),
       ('Анна', 'anna@mail.ru', 'Москва', '2025-05-20'),
       ('Мария', 'maria@mail.ru', 'Санкт-Петербург', '2025-07-01'),
       ('Олег', 'oleg@mail.ru', 'Казань', '2025-09-12');

INSERT INTO products (name, price, category, in_stock)
VALUES ('Ноутбук', 80000, 'Электроника', TRUE),
       ('Смартфон', 50000, 'Электроника', TRUE),
       ('Java для начинающих', 1200, 'Книги', TRUE),
       ('PostgreSQL на практике', 1800, 'Книги', TRUE),
       ('Наушники', 5000, 'Электроника', FALSE),
       ('SQL за 30 дней', 900, 'Книги', TRUE);

INSERT INTO managers (name, email, department)
VALUES ('Иван Петров', 'ivan.petrov@shop.ru', 'Электроника'),
       ('Анна Смирнова', 'anna.smirnova@shop.ru', 'Книги'),
       ('Олег Иванов', 'oleg.ivanov@shop.ru', 'Общие продажи'),
       ('Мария Соколова', 'maria.sokolova@shop.ru', 'Электроника');

INSERT INTO orders
    (user_id, product_id, manager_id, quantity, total_price, order_date)
VALUES (1, 1, 1, 1, 80000, '2025-10-01'),
       (1, 3, 2, 2, 2400, '2025-10-05'),
       (2, 2, 1, 1, 50000, '2025-10-10'),
       (3, 4, 3, 1, 1800, '2025-10-12'),
       (4, 1, 2, 1, 80000, '2025-10-15'),
       (5, 6, 3, 3, 2700, '2025-10-20'),
       (2, 3, 2, 1, 1200, '2025-11-01'),
       (3, 2, 1, 1, 50000, '2025-11-05');

INSERT INTO advertising_campaigns
    (name, budget, clicks, conversions, campaign_date)
VALUES ('Осенняя распродажа', 50000, 1200, 100, '2025-10-01'),
       ('Реклама ноутбуков', 30000, 800, 60, '2025-10-10'),
       ('Книжная неделя', 15000, 500, 80, '2025-10-15'),
       ('Чёрная пятница', 100000, 3000, 250, '2025-11-01'),
       ('Реклама смартфонов', 40000, 1500, 120, '2025-11-05');
