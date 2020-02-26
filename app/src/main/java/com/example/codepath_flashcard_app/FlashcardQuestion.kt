package com.example.codepath_flashcard_app

data class FlashcardQuestion(
    var question: String,
    var correctAnswer: String,
    var wrongAnswers: List<String>
) {
    companion object {
        const val SIZE = 3
    }

    init {
        require(wrongAnswers.size == SIZE)
    }
}