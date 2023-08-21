CREATE TABLE IF NOT EXISTS users (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    image_path VARCHAR(300),
    token VARCHAR(300),
    status VARCHAR(50),
    choosen_one BOOLEAN NOT NULL DEFAULT false,
    roleId SMALLINT
);
ALTER TABLE users
ADD COLUMN token VARCHAR(300);
ALTER TABLE users
ADD CONSTRAINT fk_users_roles FOREIGN KEY(roleId) REFERENCES roles(id);
ALTER TABLE users DROP token;
ALTER TABLE users
ALTER column status TYPE VARCHAR(50);
SELECT *
FROM users;
CREATE TABLE IF NOT EXISTS confirmation_tokens (
    id SERIAL NOT NULL PRIMARY KEY,
    token VARCHAR(300) NOT NULL,
    confirmed_date TIMESTAMP,
    created_date TIMESTAMP,
    userId INT NOT NULL,
    CONSTRAINT fk_tokens_users FOREIGN KEY(userId) REFERENCES users(id)
);
ALTER TABLE confirmation_tokens
ALTER column userId
SET NOT NULL;
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
INSERT INTO roles (name)
VALUES('USER'),
    ('CHOOSEN');
SELECT *
FROM roles;
CREATE TABLE IF NOT EXISTS meals (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    early_order BOOLEAN,
    is_reguler BOOLEAN
);
SELECT *
FROM meals;
CREATE TABLE IF NOT EXISTS types (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price decimal,
    reguler BOOLEAN
);
SELECT *
FROM types;
CREATE TABLE IF NOT EXISTS meals_types (
    id SERIAL NOT NULL PRIMARY KEY,
    -- mt_id SERIAL NOT NULL PRIMARY KEY,
    meal_id SERIAL NOT NULL,
    type_entity_id SERIAL NOT NULL,
    CONSTRAINT fk_meals_types FOREIGN KEY (meal_id) REFERENCES meals(id),
    CONSTRAINT fk_types_meals FOREIGN KEY (type_entity_id) REFERENCES types(id) -- PRIMARY KEY(meal_id, type_entity_id)
);
ALTER TABLE meals_types
    RENAME COLUMN type_id TO type_entity_id;
ALTER TABLE meals_types
    RENAME COLUMN id TO mt_id;
SELECT *
FROM meals_types;
CREATE TABLE IF NOT EXISTS daily_menus (
    id SERIAL NOT NULL PRIMARY KEY,
    date DATE,
    weekly_menu_id INT
);
ALTER TABLE daily_menus
ADD COLUMN weekly_menu_id INT;
ALTER TABLE daily_menus
ALTER column date TYPE DATE;
CREATE TABLE IF NOT EXISTS weekly_menus (
    id SERIAL NOT NULL PRIMARY KEY,
    date_from DATE,
    date_to DATE,
    image_path VARCHAR(300)
);
SELECT *
FROM weekly_menus;
ALTER TABLE weekly_menus
    RENAME COLUMN datefrom TO date_from;
ALTER TABLE weekly_menus
    RENAME COLUMN dateto TO date_to;
ALTER TABLE weekly_menus
ALTER column date_from TYPE DATE;
ALTER TABLE weekly_menus
ALTER column date_to TYPE DATE;
CREATE TABLE IF NOT EXISTS daily_menus_meals (
    meal_id SERIAL NOT NULL,
    daily_menu_id SERIAL NOT NULL,
    CONSTRAINT fk_meals_daily_menus FOREIGN KEY (meal_id) REFERENCES meals(id),
    CONSTRAINT fk_daily_menus_meals FOREIGN KEY (daily_menu_id) REFERENCES daily_menus(id),
    PRIMARY KEY(meal_id, daily_menu_id)
);
CREATE TABLE IF NOT EXISTS user_orders (
    id SERIAL NOT NULL PRIMARY KEY,
    date DATE,
    paid BOOLEAN,
    userId INT
);
ALTER TABLE user_orders
ALTER column date TYPE DATE;
ALTER TABLE user_orders
ADD column userId INT;
ALTER TABLE user_orders
ADD CONSTRAINT fk_user_orders_users FOREIGN KEY(userId) REFERENCES users(id);
CREATE TABLE IF NOT EXISTS meal_types_user_orders (
    meal_type_id SERIAL NOT NULL,
    user_order_id SERIAL NOT NULL,
    CONSTRAINT fk_meal_types_user_orders FOREIGN KEY (meal_type_id) REFERENCES meals_types(mt_id),
    CONSTRAINT fk_user_orders_meal_types FOREIGN KEY (user_order_id) REFERENCES user_orders(id),
    PRIMARY KEY(meal_type_id, user_order_id)
);
SELECT *
FROM meal_types_user_orders;
CREATE TABLE IF NOT EXISTS viber_senders (
    id SERIAL NOT NULL PRIMARY KEY,
    userId VARCHAR(100),
    date TIMESTAMP
);