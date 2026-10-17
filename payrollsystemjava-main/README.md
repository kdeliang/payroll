# Payroll System (Java Swing + MySQL JDBC)

This keeps the existing Java Swing screens and current sample/in-memory behavior. JDBC is connected to MySQL for login authentication. Employee, attendance, payroll, and payslip panels have not been rewritten as database-backed CRUD.

## Requirements

- JDK 21 or newer (the Maven build targets Java 21)
- Apache Maven 3.9+
- MySQL Server running on `localhost:3306`
- Visual Studio Code with the **Extension Pack for Java**
- Internet connection for Maven to download MySQL Connector/J and Maven plugins on the first build

## 1. Set up MySQL / XAMPP

Follow [`MYSQL_SETUP.md`](MYSQL_SETUP.md) for the full step-by-step guide. In short: start MySQL in XAMPP, open phpMyAdmin, run `database/setup.sql` as a database administrator after replacing `CHANGE_THIS_PASSWORD`, then set `PAYROLL_DB_USER=payroll_app` and `PAYROLL_DB_PASSWORD` to the same password in your VS Code terminal. Do not commit actual passwords to GitHub.

## 3. Run in VS Code

1. Extract the ZIP.
2. In VS Code, choose **File → Open Folder** and open the extracted `payrollsystemjava-main` folder (the folder containing this README and `mavenproject1`).
3. Open **Terminal → New Terminal**.
4. Run these commands:

```powershell
cd mavenproject1
mvn -U clean compile
mvn exec:java
```

Or run the app in one command after changing to `mavenproject1`:

```powershell
mvn -U clean compile exec:java
```

`-U` tells Maven to check/update dependency metadata. MySQL Connector/J is already declared in `mavenproject1/pom.xml`; you do not need to download a JAR manually. To explicitly download the dependency into your local Maven cache, run this from `mavenproject1`:

```powershell
mvn dependency:get -Dartifact=com.mysql:mysql-connector-j:9.6.0
```

If VS Code asks to import the Maven project, allow it. Keep using the same terminal session where you set `PAYROLL_DB_PASSWORD`.

## Demo accounts

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `admin123` |
| Employee | `employee` | `employee123` |

The demo accounts are inserted only when the `users` table is empty. Passwords are stored as salted PBKDF2 hashes. These are demonstration credentials; change them before any real use.

## Troubleshooting

- **`Access denied for user`**: Check `PAYROLL_DB_USER` and `PAYROLL_DB_PASSWORD`; follow `MYSQL_SETUP.md` to create/reset the `payroll_app` account.
- **`Unknown database 'payrollsystem'`**: Run the database-creation command or execute `database/setup.sql`.
- **`Communications link failure` / connection refused**: Start MySQL Server and verify its host and port.
- **`No suitable driver`**: Confirm you opened the folder containing the Maven project and run `mvn -U clean compile`; don't launch the raw `.java` file outside Maven.
- **`JVM creation failed`**: This is separate from the JDBC dependency. See the Java runtime/environment checks in the usage instructions you received with this project; a newer JDK alone does not guarantee VS Code or Maven is using that JDK.

## Scope note

JDBC currently authenticates the login screen against MySQL. The other panels retain their original in-memory/sample data, matching the requested minimal-change approach.
