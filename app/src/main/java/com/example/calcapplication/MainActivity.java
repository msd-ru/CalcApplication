package com.example.calcapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static final String emptyOperation = "";
    static final String emptyNumber = "0";

    enum InputState { stNumber, stOperation, stEqual };

    TextView resultField; // текстовое поле для вывода результата
    TextView operationField;    // текстовое поле для вывода знака операции
    EditText numberField;   // поле для ввода числа
    Double operand = null;  // операнд операции
    String lastOperation = emptyOperation; // последняя операция
    InputState inputState = InputState.stNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // получаем все поля по id из activity_main.xml
        resultField = (TextView) findViewById(R.id.resultField);
        operationField = (TextView) findViewById(R.id.operationField);
        numberField = (EditText) findViewById(R.id.numberField);
        numberField.setInputType(InputType.TYPE_NULL); // Не показывать виртуальную клавиатуру

        numberField.setText(emptyNumber);
    }
    // сохранение состояния
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("OPERATION", lastOperation);
        outState.putSerializable("STATE", inputState);
        if (operand!=null)
            outState.putDouble("OPERAND", operand);
        super.onSaveInstanceState(outState);
    }
    // получение ранее сохраненного состояния
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lastOperation = savedInstanceState.getString("OPERATION");
        inputState = (InputState)savedInstanceState.getSerializable("STATE");
        operand= savedInstanceState.getDouble("OPERAND");
        resultField.setText(operand.toString());
        operationField.setText(lastOperation);
    }

    // Очистка предыдущей операции
    private void clearLastOperation() {
        numberField.setText(emptyNumber);
        operationField.setText("");
        resultField.setText("");
        operand = null;  // операнд операции
        lastOperation = emptyOperation; // последняя операция
        inputState = InputState.stNumber;
    }

    // Обработка нажатия на числовую кнопку
    public void onDigitClick(View view){

        Button button = (Button)view;
        String buttonText = button.getText().toString();
        String numberText = numberField.getText().toString();

//        Toast toast = Toast.makeText(this, "1", Toast.LENGTH_LONG);
//        toast.show();

        // Первая цифра после операции - предварительно очистить операнд/операнды
        if (inputState != InputState.stNumber) {

            if (inputState == InputState.stEqual) {
                clearLastOperation();
            }
            else if (!numberText.equals(emptyNumber)) {
                numberField.setText(emptyNumber);
            }
            numberText = emptyNumber;
        }

        if (buttonText.equals(",")) {
            if (numberText.contains(",")) return; // 2 запятые нельзя)
        }
        else if (numberText.equals(emptyNumber)) {
            numberField.setText(""); // Незначащий ноль
        }

        numberField.append(buttonText);
        inputState = InputState.stNumber;
    }

    // Обработка нажатия на кнопку унарной операции
    public void onUnOperationClick(View view){

        Button button = (Button)view;
        String op = button.getText().toString();

        // Если это "стирание"
        if (op.equals("C")) { // Стереть все
            clearLastOperation();
            return;
        }

        String snumber = numberField.getText().toString();
        int nLen = snumber.length();

        // Операции над numberField
        if (!snumber.equals(emptyNumber)) {
            switch (op) {
                case "CE": // Стереть все число:
                    snumber = emptyNumber;
                    break;
                case "⌫": // Стереть последний символ
                    snumber = (nLen == 1) ? emptyNumber
                                          : snumber.substring(0, nLen - 1);
                    break;
                case "+/-": // Изменить знак
                    snumber = (snumber.charAt(0) == '-') ? snumber.substring(1, nLen)
                                                         : '-' + snumber;
                    break;
                case "%": // Перевести в %:
                    if (operand != null) {
                        snumber = snumber.replace(',', '.');
                        Double number = Double.valueOf(snumber);
                        number *= operand / 100;
                        snumber = number.toString().replace('.', ',');
                    }
                    break;
            }
            numberField.setText(snumber);

            inputState = InputState.stNumber;
            return;
        }
    }

    // Обработка нажатия на кнопку бинарной операции
    public void onBinOperationClick(View view){

        Button button = (Button)view;
        String op = button.getText().toString();
//        String sTxt = "onOperationClick " + op;
//        Toast toast = Toast.makeText(this, sTxt, Toast.LENGTH_LONG);
//        toast.show();

        if (op.equals("=") || inputState == InputState.stNumber) {
            try {
                performBinOperation();
            } catch (NumberFormatException ex) {
                numberField.setText("***");
            }
        }

        if (!op.equals("=")) {
            lastOperation = op;
            operationField.setText(lastOperation);
            inputState = InputState.stOperation;
        }
        else inputState = InputState.stEqual;
    }

    private void performBinOperation() {

        String snumber = numberField.getText().toString();
        snumber = snumber.replace(',', '.');
        Double number = Double.valueOf(snumber);

        // если операнд ранее не был установлен (при вводе самой первой операции)
        if (operand == null) operand = number;
        else {
//            if (lastOperation.equals(emptyOperation)) return;
            switch (lastOperation) {
                case emptyOperation:
                    return;
                case "/":
                    if (number == 0) operand = 0.0;
                    else operand /= number;
                    break;
                case "*":
                    operand *= number;
                    break;
                case "+":
                    operand += number;
                    break;
                case "-":
                    operand -= number;
                    break;
            }
        }
        resultField.setText(operand.toString().replace('.', ','));
//        numberField.setText(emptyNumber);
    }
}