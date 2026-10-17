  -- Local development setup for the Payroll System.
  -- Run this in phpMyAdmin's SQL tab while logged in as a MySQL/MariaDB admin.
  -- Before running, replace CHANGE_THIS_PASSWORD with your own local password.
  -- Do not use this sample password for a production deployment.

  CREATE DATABASE IF NOT EXISTS payrollsystem
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

  CREATE USER IF NOT EXISTS 'payroll_app'@'localhost'
    IDENTIFIED BY 'CHANGE_THIS_PASSWORD';

  -- Current JDBC code uses CREATE (users table), SELECT (authentication), and
  -- INSERT (demo accounts). Grant only what the current login code needs.
  GRANT CREATE, SELECT, INSERT ON payrollsystem.* TO 'payroll_app'@'localhost';

  FLUSH PRIVILEGES;

  -- The Java app creates the users table and seeds demo app accounts on its
  -- first connection IF the users table is empty. Do not insert plain passwords
  -- into this SQL file; Java stores salted PBKDF2 password hashes.
