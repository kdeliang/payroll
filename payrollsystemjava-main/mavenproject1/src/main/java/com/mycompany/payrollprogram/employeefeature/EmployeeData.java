package com.mycompany.payrollprogram.employeefeature;

import java.util.Locale;

final class EmployeeData {

    static final String NAME = "Ana Mendoza";
    static final String FIRST_NAME = "Ana";
    static final String EMPLOYEE_ID = "EMP-001";
    static final String DEPARTMENT = "Human Resources";
    static final String POSITION = "HR Officer";

    private static final String[] PERIODS = {"September 2026", "August 2026", "July 2026"};
    private static final String[] RANGES = {"September 1–30, 2026", "August 1–31, 2026", "July 1–31, 2026"};
    private static final String[] PAY_DATES = {"September 30, 2026", "August 31, 2026", "July 31, 2026"};
    private static final double[][] AMOUNTS = {
        {30000.00, 2000.00, 1500.00, 426.14, 1500.00, 200.00, 750.00, 1471.98, 0.00, 42.61, 1000.00},
        {30000.00, 2000.00, 1500.00, 0.00, 1500.00, 200.00, 750.00, 1684.14, 0.00, 0.00, 1000.00},
        {30000.00, 2000.00, 1500.00, 0.00, 1500.00, 200.00, 750.00, 1684.14, 0.00, 0.00, 1000.00}
    };

    private EmployeeData() {
    }

    static int count() {
        return PERIODS.length;
    }

    static String period(int index) {
        return PERIODS[index];
    }

    static String range(int index) {
        return RANGES[index];
    }

    static String payDate(int index) {
        return PAY_DATES[index];
    }

    static String status(int index) {
        return "Paid";
    }

    static double amount(int index, int item) {
        return AMOUNTS[index][item];
    }

    static double gross(int index) {
        double total = 0;
        for (int i = 0; i < 4; i++) {
            total += AMOUNTS[index][i];
        }
        return total;
    }

    static double deductions(int index) {
        double total = 0;
        for (int i = 4; i < AMOUNTS[index].length; i++) {
            total += AMOUNTS[index][i];
        }
        return total;
    }

    static double net(int index) {
        return gross(index) - deductions(index);
    }

    static String money(double value) {
        return String.format(Locale.ENGLISH, "%,.2f", value);
    }
}
