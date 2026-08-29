-- Run this once in MySQL before starting the application.
-- Hibernate (ddl-auto=update) will create/update the "expenses" table automatically,
-- but it will NOT create the database itself.

CREATE DATABASE IF NOT EXISTS trackmyexpenses_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Optional: if you don't want to use root, create a dedicated user instead and
-- update application.properties accordingly.
-- CREATE USER 'trackmyexpenses_user'@'localhost' IDENTIFIED BY 'change_me';
-- GRANT ALL PRIVILEGES ON trackmyexpenses_db.* TO 'trackmyexpenses_user'@'localhost';
-- FLUSH PRIVILEGES;

-- Reference only - the table below is what Hibernate will generate automatically
-- from the Expense entity on first run (ddl-auto=update). Kept here for visibility/review.
--
-- CREATE TABLE expenses (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     amount DECIMAL(12,2) NOT NULL,
--     category VARCHAR(20) NOT NULL,
--     expense_date DATETIME NOT NULL,
--     description VARCHAR(255),
--     created_at DATETIME NOT NULL,
--     updated_at DATETIME,
--     INDEX idx_expenses_expense_date (expense_date),
--     INDEX idx_expenses_category (category)
-- );
