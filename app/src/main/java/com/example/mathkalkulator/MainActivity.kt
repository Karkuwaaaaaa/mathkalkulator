package com.example.mathkalkulator

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // Объявление переменных для элементов интерфейса (lateinit означает, что они будут инициализированы позже)
    private lateinit var txtFirstOperand: TextView
    private lateinit var txtOperation: TextView
    private lateinit var txtTwoOperand: TextView
    private lateinit var editValue: EditText
    private lateinit var btnStart: Button
    private lateinit var btnCheck: Button
    private lateinit var txtTotalCount: TextView
    private lateinit var txtNumberRight: TextView
    private lateinit var txtNumberWrong: TextView
    private lateinit var txtPercentageCorrectAnswers: TextView
    private lateinit var layoutExampleBg: LinearLayout

    // Переменные для логики
    private var num1 = 0
    private var num2 = 0
    private var correctAnswer = 0
    private var operator = ""
    private var rightCount = 0
    private var wrongCount = 0
    private var isAnswered = false // Флаг, что пример уже проверен

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация элементов
        txtFirstOperand = findViewById(R.id.txtFirstOperand)
        txtOperation = findViewById(R.id.txtOperation)
        txtTwoOperand = findViewById(R.id.txtTwoOperand)
        editValue = findViewById(R.id.editValue)
        btnStart = findViewById(R.id.btnStart)
        btnCheck = findViewById(R.id.btnCheck)
        txtTotalCount = findViewById(R.id.txtTotalCount)
        txtNumberRight = findViewById(R.id.txtNumberRight)
        txtNumberWrong = findViewById(R.id.txtNumberWrong)
        txtPercentageCorrectAnswers = findViewById(R.id.txtPercentageCorrectAnswers)
        layoutExampleBg = findViewById(R.id.layoutExampleBg)

        // Восстановление состояния при повороте
        if (savedInstanceState != null) {
            num1 = savedInstanceState.getInt("num1")
            num2 = savedInstanceState.getInt("num2")
            operator = savedInstanceState.getString("operator") ?: ""
            correctAnswer = savedInstanceState.getInt("correctAnswer")
            rightCount = savedInstanceState.getInt("rightCount")
            wrongCount = savedInstanceState.getInt("wrongCount")
            isAnswered = savedInstanceState.getBoolean("isAnswered")

            updateStatsUI()

            if (num1 != 0 || num2 != 0) {
                txtFirstOperand.text = num1.toString()
                txtOperation.text = operator
                txtTwoOperand.text = num2.toString()

                if (isAnswered) {
                    btnStart.isEnabled = true
                    btnCheck.isEnabled = false
                    editValue.isEnabled = false

                    val userAnswerStr = savedInstanceState.getString("userAnswer")
                    if (userAnswerStr != null) {
                        editValue.setText(userAnswerStr)
                        val userAnswer = userAnswerStr.toIntOrNull()
                        if (userAnswer == correctAnswer) {
                            layoutExampleBg.setBackgroundColor(Color.GREEN)
                        } else {
                            layoutExampleBg.setBackgroundColor(Color.RED)
                        }
                    }
                } else {
                    btnStart.isEnabled = false
                    btnCheck.isEnabled = true
                    editValue.isEnabled = true
                    layoutExampleBg.setBackgroundColor(Color.WHITE)
                }
            }
        } else {
            // Начальное состояние при первом запуске
            btnStart.isEnabled = true
            btnCheck.isEnabled = false
            editValue.isEnabled = false
        }

        // Обработчик кнопки СТАРТ (лямбда-выражение в Kotlin)
        btnStart.setOnClickListener {
            generateExample()
        }

        // Обработчик кнопки ПРОВЕРКА
        btnCheck.setOnClickListener {
            checkAnswer()
        }
    }

    // Генерация примера
    private fun generateExample() {
        // Генерируем числа от 10 до 99 включительно
        num1 = Random.nextInt(10, 100)
        num2 = Random.nextInt(10, 100)

        // Выбираем случайную операцию
        val operations = arrayOf("+", "-", "*", "/")
        operator = operations.random()

        // Логика для деления (частное должно быть целым)
        if (operator == "/") {
            while (num1 % num2 != 0) {
                num1 = Random.nextInt(10, 100)
                num2 = Random.nextInt(10, 100)
            }
            correctAnswer = num1 / num2
        }
        // Логика для вычитания (чтобы не было отрицательных чисел)
        else if (operator == "-") {
            if (num1 < num2) {
                val temp = num1
                num1 = num2
                num2 = temp
            }
            correctAnswer = num1 - num2
        } else if (operator == "+") {
            correctAnswer = num1 + num2
        } else if (operator == "*") {
            correctAnswer = num1 * num2
        }

        // Обновляем UI
        txtFirstOperand.text = num1.toString()
        txtOperation.text = operator
        txtTwoOperand.text = num2.toString()
        editValue.text.clear()
        editValue.isEnabled = true
        layoutExampleBg.setBackgroundColor(Color.WHITE) // Белый фон

        // Управление кнопками
        btnStart.isEnabled = false
        btnCheck.isEnabled = true
        isAnswered = false
    }

    // Проверка ответа
    private fun checkAnswer() {
        val input = editValue.text.toString().trim()
        if (input.isEmpty()) {
            Toast.makeText(this, "Введите ответ!", Toast.LENGTH_SHORT).show()
            return
        }

        // toIntOrNull() безопасно преобразует строку в число, возвращая null при ошибке
        val userAnswer = input.toIntOrNull()
        if (userAnswer == null) {
            Toast.makeText(this, "Некорректный ввод!", Toast.LENGTH_SHORT).show()
            return
        }

        // Проверяем правильность
        if (userAnswer == correctAnswer) {
            layoutExampleBg.setBackgroundColor(Color.GREEN) // Зеленый фон
            rightCount++
        } else {
            layoutExampleBg.setBackgroundColor(Color.RED) // Красный фон
            wrongCount++
        }

        isAnswered = true
        updateStatsUI()

        // Блокируем элементы
        editValue.isEnabled = false
        btnCheck.isEnabled = false
        btnStart.isEnabled = true
    }

    // Обновление статистики
    private fun updateStatsUI() {
        val total = rightCount + wrongCount
        txtTotalCount.text = total.toString()
        txtNumberRight.text = rightCount.toString()
        txtNumberWrong.text = wrongCount.toString()

        // Расчет процентов
        val percentage = if (total > 0) {
            (rightCount.toDouble() / total) * 100
        } else {
            0.0
        }

        // Форматирование до 2 знаков после запятой с символом %
        txtPercentageCorrectAnswers.text = String.format(Locale.US, "%.2f%%", percentage)
    }

    // Сохранение состояния при повороте экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("num1", num1)
        outState.putInt("num2", num2)
        outState.putString("operator", operator)
        outState.putInt("correctAnswer", correctAnswer)
        outState.putInt("rightCount", rightCount)
        outState.putInt("wrongCount", wrongCount)
        outState.putBoolean("isAnswered", isAnswered)
        outState.putString("userAnswer", editValue.text.toString())
    }
}