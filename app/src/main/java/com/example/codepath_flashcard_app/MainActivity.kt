package com.example.codepath_flashcard_app

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
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
        FlashcardQuestion("1 + 1 = ?", "2", listOf("1", "3", "4"))
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
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
            builder.create().let {
                it.show()
                root.onSubmitQuestion(it, null) { fc ->
                    index = questions.size - 1
                    questions.add(fc)
                    displayNextQuestion()
                    Toast.makeText(it.context, "Flashcard added!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fab_edit_question.setOnClickListener {
            val root = layoutInflater.inflate(R.layout.new_flashcard, null)
            val builder = AlertDialog.Builder(this)
                .setTitle(R.string.new_flash_card)
                .setView(root)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
            builder.create().let {
                it.show()
                root.bindFc(questions[index])
                root.onSubmitQuestion(it, questions[index]) {
                    index--
                    displayNextQuestion()
                }
            }
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

private fun View.bindFc(fc: FlashcardQuestion) {
    new_fc_question.setText(fc.question)
    new_fc_correct_ans.setText(fc.correctAnswer)
    new_fc_incorrect_ans1.setText(fc.wrongAnswers[0])
    new_fc_incorrect_ans2.setText(fc.wrongAnswers[1])
    new_fc_incorrect_ans3.setText(fc.wrongAnswers[2])
}

private fun View.onSubmitQuestion(
    dialog: AlertDialog,
    fc: FlashcardQuestion?,
    callback: ((FlashcardQuestion) -> Unit)?
) {
    dialog.getButton(AlertDialog.BUTTON_POSITIVE)!!.setOnClickListener {
        if (new_fc_question.text!!.isEmpty() ||
            new_fc_correct_ans.text!!.isEmpty()
        ) {
            Toast.makeText(it.context, "Question/Answer cannot be empty!", Toast.LENGTH_SHORT).show()
        } else {
            val toEdit = fc ?: FlashcardQuestion("", "", List(3) { "" })
            toEdit.apply {
                question = new_fc_question.text.toString()
                correctAnswer = new_fc_correct_ans.text.toString()
                wrongAnswers = listOf(
                    new_fc_incorrect_ans1.text.toString(),
                    new_fc_incorrect_ans2.text.toString(),
                    new_fc_incorrect_ans3.text.toString()
                )
            }
            callback?.invoke(toEdit)
            dialog.dismiss()
        }
    }
}
