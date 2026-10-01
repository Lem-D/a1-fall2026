package com.example.a1_fall2026_lemueldipasupil.ControllerView

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.a1_fall2026_lemueldipasupil.Models.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items

// Enum to track what part of the UI should be visible
enum class GameState {
    SELECTING_LENGTH, SHOWING_SEQUENCE, WAITING_FOR_INPUT, SHOWING_FEEDBACK
}

// Acts as the main Controller for the UI, holding the current game state
// and routing the user to the correct screens based on that state.
@Composable
fun GameScreen(game: SequenceGame, log: AttemptLog, modifier: Modifier) {
    var currentState by remember {mutableStateOf(GameState.SELECTING_LENGTH)}
    var currentTarget by remember {mutableStateOf(intArrayOf())}
    var lastAttempt by remember { mutableStateOf<Attempt?>(null) }

}

// Displays an input field and button to let the user choose how long
// the memory sequence should be before starting the game.
@Composable
fun RenderLengthSelector(onStartClick: (Int) -> Unit) {
    var lengthInput by remember { mutableStateOf("4") }

    Column {
        Text("Choose Sequence Length:", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = lengthInput,
            onValueChange = { lengthInput = it },
            label = { Text("Length") }
        )
        Button(onClick = { onStartClick(lengthInput.toIntOrNull() ?: 4) }) {
            Text("Start Game")
        }
    }
}

// Temporarily shows the generated sequence of numbers
// that the user needs to memorize.
@Composable
fun ShowSequence(target: IntArray) {
    Text(
        text = "Memorize this: ${target.joinToString(" ")}",
        style = MaterialTheme.typography.titleMedium
    )
}

// Provides a text field for the user to type in their memorized guess
// and a button to submit it back to the game logic.
@Composable
fun RenderInput(onSubmit: (IntArray) -> Unit) {
    var guessInput by remember { mutableStateOf("") }

    Column{
        OutlinedTextField(
            value = guessInput,
            onValueChange = { guessInput = it },
            label = { Text("Enter Your Guess") }
        )
        Button(
            onClick = {
                val guess = guessInput.split(" ").map { it.toIntOrNull() ?: 0 }.toIntArray()
                onSubmit(guess)
            }
        ) {
            Text("Submit Guess")
        }

    }
}

// Displays the result of the user's latest attempt, showing green text
// if they were correct, and red text revealing the answer if they failed.
@Composable
fun RenderFeedback(attempt: Attempt) {
    val color = if (attempt.correct) Color.Green else Color.Red
    val text = if (attempt.correct) "Correct!" else "Incorrect, Target was ${attempt.target.joinToString(" ")}"

    Text(text, color = color, style = MaterialTheme.typography.headlineSmall)
}

// Renders a visual card summarizing the player's overall performance,
// including total games played, total correct, and accuracy percentage.
@Composable
fun RenderSummary(summary: Summary){
    Card(modifier = Modifier.fillMaxWidth()){
        Column(modifier = Modifier.padding(16.dp)){
            Text("Total Plays: ${summary.total}")
            Text("Correct Plays: ${summary.correct}")
            Text("Accuracy: ${summary.accuracyPct}%")
        }
    }
}

// Displays a scrollable list of all previous attempts made during
// the current session, showing the guessed sequence and the result.
@Composable
fun RenderHistory(attempts: List<Attempt>){
    LazyColumn(modifier = Modifier.fillMaxWidth()){
        items(attempts) { attempt ->
            Text("Guessed ${attempt.guess.joinToString(" ")}, Correct: ${attempt.correct}")
        }
    }
}