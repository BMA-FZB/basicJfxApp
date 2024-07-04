-- Create the database if it does not exist
CREATE DATABASE IF NOT EXISTS test;

-- Use the test database
USE test;

-- Create the person table if it does not exist
CREATE TABLE IF NOT EXISTS person (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL
);

-- Create additional tables for testing
CREATE TABLE IF NOT EXISTS address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    person_id INT NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    FOREIGN KEY (person_id) REFERENCES person(id)
);

CREATE TABLE IF NOT EXISTS product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS `order` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    person_id INT NOT NULL,
    order_date DATE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (person_id) REFERENCES person(id)
);

CREATE TABLE IF NOT EXISTS order_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES `order`(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE IF NOT EXISTS department (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Insert sample data into tables
INSERT INTO person (name, age) VALUES
('Alice', 30),
('Bob', 25),
('Charlie', 35);

INSERT INTO address (person_id, street, city, postal_code) VALUES
(1, '123 Main St', 'Anytown', '12345'),
(2, '456 Elm St', 'Othertown', '67890');

INSERT INTO product (name, price) VALUES
('Laptop', 999.99),
('Smartphone', 599.99),
('Headphones', 149.99);

INSERT INTO department (name) VALUES
('IT'),
('Sales'),
('Marketing');
