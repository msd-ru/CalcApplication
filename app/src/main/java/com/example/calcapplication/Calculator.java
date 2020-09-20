package com.example.calcapplication;

public class Calculator {
    static final String errZeroDivide = "На ноль делить нельзя!";

    public static class DivisionByZero extends Exception {

        public DivisionByZero() {
            super(errZeroDivide);
        }

    }
    // Десятичное - в строку
    static private String decimalToString(double num) {
        //snumber = String.format("%5.0f", operand);
        //snumber = DecimalFormat.getInstance().format(operand); // Без лишних нулей
        String result = "" + num;
        result = result.replace('.', ',');
        int len = result.length();
        if (len > 1) {
            if (result.substring(len-2, len).equals(",0")) // Незначащий 0
                result = result.substring(0, len-2);
        }
        return result;
    }

    // Строку - в десятичное
    static private double stringToDecimal(String snum) {
        String snumber = snum.replace(',', '.');
        double result = Double.parseDouble(snumber);
        return result;
    }

    // Result = sRes * sNum / 100
    static String percentCalc(String sRes, String sNum) {
        String sResult = sNum;
        if (sRes != "") { // Есть оба операнда
            double result = stringToDecimal(sRes);
            double number = stringToDecimal(sNum);
            result *= number / 100.;
            sResult = decimalToString(result);
        }
        return sResult;
    }

    // Бинарные операции
    static String calc(String sRes, String sNum, String sOper)
            throws DivisionByZero {

        String sResult = sNum; // Если только один операнд

        if (sRes != "") { // Есть оба операнда
            double number = stringToDecimal(sNum);
            double result = stringToDecimal(sRes);
            switch (sOper) {
                case "":
                    break;
                case "/":
                    if (number == 0) {
                        throw new DivisionByZero();
                    }
                    result /= number;
                    break;
                case "*":
                    result *= number;
                    break;
                case "+":
                    result += number;
                    break;
                case "-":
                    result -= number;
                    break;
            }
            sResult = decimalToString(result);
        }

        return sResult;
    }
}
