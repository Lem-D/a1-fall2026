package com.example.a1_fall2026_lemueldipasupil.Controllers
import com.example.a1_fall2026_lemueldipasupil.Models.Attempt
import com.example.a1_fall2026_lemueldipasupil.Models.AttemptLog
import com.example.a1_fall2026_lemueldipasupil.Models.SequenceGame
import com.example.a1_fall2026_lemueldipasupil.Models.Summary


interface GameUI {
    fun renderLengthSelector()
    fun showSequence(game: SequenceGame)
    fun renderInput()
    fun renderFeedback(attempt: Attempt)
    fun renderSummary(summary: Summary)
    fun renderHistory(log: AttemptLog)

    // Captured user actions to pass to the ViewModel/Controller
    fun onGameStartRequested(selectedLength: Int)
    fun onGuessSubmitted(userInput: IntArray)
    fun onPlayAgainRequested()

    // Error handling
    fun showInputError(errorMessage: String)
}