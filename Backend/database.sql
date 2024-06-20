CREATE DATABASE shopapp;
USE shopapp;

-- Create users table

CREATE TABLE users(
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(10) NOT NULL,
    address VARCHAR(200) DEFAULT '',
    password VARCHAR(100) NOT NULL DEFAULT '',
    created_at DATETIME,
    updated_at DATETIME,
    is_active TINYINT(1) DEFAULT 1, 
    date_of_birth DATE,
    facebook_account_id INT DEFAULT 0,
    google_account_id INT DEFAULT 0,
);

ALTER TABLE users ADD COLUMN role_id INT;

CREATE TABLE roles(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
);

ALTER TABLE users ADD FOREIGN KEY (role_id) REFERENCES roles(id)

CREATE TABLE tokens(
    id INT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    expiration_date DATETIME,
    revoke TINYINT(1) NOT NULL,
    expired TINYINT(1) NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE social_accounts(
    id INT PRIMARY KEY AUTO_INCREMENT,
    provider VARCHAR(50) NOT NULL COMMENT 'Name of Social network',
    provider_id VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL COMMENT 'Email of account',
    name VARCHAR(150) NOT NULL COMMENT 'Name of user',
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
)

CREATE TABLE categories(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL DEFAULT '' COMMENT 'Name of Category'
);

-- Create table product and constrain check price
CREATE TABLE products(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(400) NOT NULL DEFAULT '' COMMENT 'Name of Product',
    price FLOAT NOT NULL CHECK(price >= 0),
    thumbnail VARCHAR(400) DEFAULT '' COMMENT 'URL of Image',
    description LONGTEXT DEFAULT '',
    created_at DATETIME,
    updated_at DATETIME,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id),
);

CREATE TABLE orders(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    full_name VARCHAR(150) DEFAULT '',
    email VARCHAR(100) DEFAULT '',
    phone_number VAR(150) NOT NULL,
    address VARCHAR(200) NOT NULL,
    note VARCHAR(100) DEFAULT '',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50),
    total_money FLOAT CHECK(total_money > 0)
    shipping_method VARCHAR(150),
    shipping_address VARCHAR(200),
    shipping_date DATE,
    tracking_number VARCHAR(150),
    payment_method VARCHAR(150),
    active TINYINT(1)
);

ALTER TABLE orders MODIFY COLUMN status ENUM('pending', 'processing', 'shipped', 'delivery', 'cancelled') COMMENT 'Status of order'

CREATE TABLE order_details(
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    FOREIGN KEY (id) REFERENCES orders(id),
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    price FLOAT CHECK(price >= 0),
    number_of_products INT CHECK(number_of_products > 0),
    total_money FLOAT CHECK(total_money > 0),
    color VARCHAR(50) DEFAULT '',
 );
