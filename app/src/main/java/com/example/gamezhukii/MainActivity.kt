package com.example.gamezhukii

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private var selectedBirthDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val savedDate = savedInstanceState?.getLong(BIRTH_DATE_KEY, NO_DATE) ?: NO_DATE
        selectedBirthDate = Calendar.getInstance().apply {
            if (savedDate != NO_DATE) {
                timeInMillis = savedDate
            } else {
                set(2001, Calendar.JANUARY, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val difficultyValue = findViewById<TextView>(R.id.difficultyValue)
        findViewById<CalendarView>(R.id.birthDateCalendar).apply {
            maxDate = System.currentTimeMillis()
            date = selectedBirthDate!!.timeInMillis
            setOnDateChangeListener { _, year, month, dayOfMonth ->
                selectedBirthDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
            }
        }
        findViewById<SeekBar>(R.id.difficultySeekBar).setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    difficultyValue.text = getString(R.string.difficulty_value, progress + 1)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            }
        )

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            registerPlayer()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(BIRTH_DATE_KEY, selectedBirthDate?.timeInMillis ?: NO_DATE)
    }

    private fun registerPlayer() {
        val fullNameInput = findViewById<EditText>(R.id.fullNameInput)
        val fullName = fullNameInput.text.toString().trim()
        if (fullName.isEmpty()) {
            fullNameInput.error = getString(R.string.full_name_required)
            fullNameInput.requestFocus()
            return
        }

        val birthDate = selectedBirthDate ?: return

        val gender = if (
            findViewById<RadioGroup>(R.id.genderGroup).checkedRadioButtonId == R.id.maleRadioButton
        ) getString(R.string.male) else getString(R.string.female)
        val course = findViewById<Spinner>(R.id.courseSpinner).selectedItemPosition + 1
        val difficulty = findViewById<SeekBar>(R.id.difficultySeekBar).progress + 1
        val birthDateText = getString(
            R.string.selected_birth_date,
            birthDate.get(Calendar.DAY_OF_MONTH),
            birthDate.get(Calendar.MONTH) + 1,
            birthDate.get(Calendar.YEAR)
        )
        val zodiacSign = ZodiacCalculator.calculate(
            birthDate.get(Calendar.MONTH) + 1,
            birthDate.get(Calendar.DAY_OF_MONTH)
        )
        val player = Player(fullName, gender, course, difficulty, birthDateText, zodiacSign)

        findViewById<TextView>(R.id.resultText).apply {
            text = getString(
                R.string.player_result,
                player.fullName,
                player.gender,
                player.course,
                player.difficulty,
                player.birthDate,
                player.zodiacSign
            )
            visibility = View.VISIBLE
        }
        findViewById<ImageView>(R.id.zodiacImage).setImageBitmap(zodiacBitmap(zodiacSign))
        findViewById<FrameLayout>(R.id.zodiacContainer).visibility = View.VISIBLE
    }

    private fun zodiacBitmap(sign: String): Bitmap {
        val index = when (sign) {
            "Овен" -> 0
            "Телец" -> 1
            "Близнецы" -> 2
            "Рак" -> 3
            "Лев" -> 4
            "Дева" -> 5
            "Весы" -> 6
            "Скорпион" -> 7
            "Стрелец" -> 8
            "Козерог" -> 9
            "Водолей" -> 10
            else -> 11
        }
        val sprite = BitmapFactory.decodeResource(resources, R.drawable.zodiac_symbols)
        val cellWidth = sprite.width / 3
        val cellHeight = sprite.height / 4
        return Bitmap.createBitmap(
            sprite,
            index % 3 * cellWidth,
            index / 3 * cellHeight,
            cellWidth,
            cellHeight
        )
    }

    private companion object {
        const val BIRTH_DATE_KEY = "birth_date"
        const val NO_DATE = -1L
    }
}
