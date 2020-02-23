package com.example.codepath_flashcard_app

data class FlashcardQuestion(
    val question: String,
    val correctAnswer: String,
    val wrongAnswers: List<String>
) {
    companion object {
        const val SIZE = 3
    }

    init {
        require(wrongAnswers.size == SIZE)
    }
}