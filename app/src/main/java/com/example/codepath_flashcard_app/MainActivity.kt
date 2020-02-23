package com.example.codepath_flashcard_app

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.new_flashcard.view.*
import kotlin.random.Random

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val ANSWER_DELAY: Long = 1000
    }

    private val questions = mutableListOf(
        FlashcardQuestion("What is 1 + 1?", "2", listOf("1", "3", "4"))
    )
    private lateinit var answerButtons: List<Button>
    private var index = -1
    private var correctIndex: Int = -1

    private val handler = Handler()
    private var answersClickable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_add_question.setOnClickListener {
            val root = layoutInflater.inflate(R.layout.new_flashcard, null)
            val builder = AlertDialog.Builder(this)
                .setTitle(R.string.new_flash_card)
                .setView(root)
                .setPositiveButton("OK") { dialog, which ->
                    index = questions.size - 1
                    questions.add(root.toQuestion())
                    displayNextQuestion()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
            builder.create().show()
        }

        answerButtons = listOf(button_answer1, button_answer2, button_answer3, button_answer4)
        answerButtons.forEach { it.setOnClickListener(this) }
        displayNextQuestion()
    }

    private fun displayNextQuestion() {
        answerButtons.forEach {
            it.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorAccent))
        }

        index = (index + 1) % questions.size
        val question = questions[index]
        val answers = question.wrongAnswers.shuffled().toMutableList()
        correctIndex = Random.nextInt(FlashcardQuestion.SIZE + 1)
        answers.add(correctIndex, question.correctAnswer)

        textview_question.text = question.question
        answers.forEachIndexed { index, answer ->
            answerButtons[index].text = answer
        }

        answersClickable = true
    }

    override fun onClick(v: View?) {
        if (!answersClickable) return

        require(v in answerButtons)
        requireNotNull(v)

        answersClickable = false
        v.backgroundTintList = ColorStateList.valueOf(Color.RED)
        answerButtons[correctIndex].backgroundTintList = ColorStateList.valueOf(Color.GREEN)
        handler.postDelayed({
            displayNextQuestion()
        }, ANSWER_DELAY)
    }
}

private fun View.toQuestion(): FlashcardQuestion {
    val question = new_fc_question.text.toString()
    val correctAnswer = new_fc_correct_ans.text.toString()
    val wrongAnswers = listOf(
        new_fc_incorrect_ans1.text.toString(),
        new_fc_incorrect_ans2.text.toString(),
        new_fc_incorrect_ans3.text.toString()
    )
    return FlashcardQuestion(question, correctAnswer, wrongAnswers)
}
