1. Start MySQL
In XAMPP Control Panel, start MySQL. Start Apache too if using phpMyAdmin. Open http://localhost/phpmyadmin.
2. Create the database and app account
In phpMyAdmin, open SQL and run database/setup.sql after replacing CHANGE_THIS_PASSWORD with your own local password. Do not commit your real password.
3. Set the connection in VS Code
Open a PowerShell terminal at the repository root:
$env:PAYROLL_DB_USER = "payroll_app"
$env:PAYROLL_DB_PASSWORD = "YOUR_LOCAL_DB_PASSWORD"
cd mavenproject1
mvn clean compile exec:java
Use the same password you put in database/setup.sql. The app creates the users table and demo logins on first connection if the table is empty. Current JDBC integration is for login only; other panels still use sample/in-memory data.
4. Demo logins
Role	Username	Password
Admin	admin	admin123
Employee	employee	employee123


These are app logins, not MySQL credentials.
5. Generate a salt and hash for a new app account
1. Put PasswordHashGenerator.java in mavenproject1 (beside pom.xml).
2. In the VS Code terminal, run:
cd mavenproject1
java PasswordHashGenerator.java "YOUR_APP_PASSWORD"
Copy the printed SALT and HASH into this SQL in phpMyAdmin. Replace the placeholders and choose EMPLOYEE or ADMIN:
INSERT INTO users (username, password_salt, password_hash, `role`, is_active)
VALUES ('YOUR_USERNAME', 'PASTE_SALT_HERE', 'PASTE_HASH_HERE', 'EMPLOYEE', 1);
Generate a new salt and hash for each account. Run the SQL only after the app has created the users table.