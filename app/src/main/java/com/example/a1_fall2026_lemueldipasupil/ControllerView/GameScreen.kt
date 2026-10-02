package com.example.a1_fall2026_lemueldipasupil.ControllerView

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import kotlinx.coroutines.delay

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
    var history by remember { mutableStateOf(log.all().toList())}
    var summary by remember {mutableStateOf(log.summary())}

    Column(modifier = Modifier.padding(16.dp)){
        when (currentState){
            GameState.SELECTING_LENGTH -> {
                RenderLengthSelector(
                    onStartClick = { length ->
                        game.start(length)
                        currentTarget = game.target
                        currentState = GameState.SHOWING_SEQUENCE
                    }
                )
            }
            GameState.SHOWING_SEQUENCE -> {
                ShowSequence(
                    target = currentTarget,
                    onTimeout = { currentState = GameState.WAITING_FOR_INPUT }
                )
            }

            GameState.WAITING_FOR_INPUT -> {
                RenderInput(
                    onSubmit = { guess ->
                        lastAttempt = game.submitGuess(guess)
                        log.add(lastAttempt!!)
                        history = log.all().toList()
                        summary = log.summary()
                        currentState = GameState.SHOWING_FEEDBACK

                    }
                )
            }
            GameState.SHOWING_FEEDBACK -> {
                RenderFeedback(lastAttempt!!)
                Button(onClick = { currentState = GameState.SELECTING_LENGTH }) {
                    Text("Play Again")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        RenderSummary(summary)
        Spacer(modifier = Modifier.height(16.dp))
        RenderHistory(history)
    }
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
// that the user needs to memorize for a few seconds before auto-advancing.
@Composable
fun ShowSequence(
    target: IntArray,
    durationSeconds: Int = 3,
    onTimeout: () -> Unit
) {
    var secondsLeft by remember(target) { mutableIntStateOf(durationSeconds) }

    LaunchedEffect(target) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
        onTimeout()
    }

    Column {
        Text(
            text = "Memorize this: ${target.joinToString(" ")}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Disappearing in $secondsLeft second(s)...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onTimeout) {
            Text("Ready to Guess")
        }
    }
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
                // Handles whitespaces in the input
                val guess = guessInput
                    .filter { !it.isWhitespace() }
                    .mapNotNull { it.digitToIntOrNull() }
                    .toIntArray()
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
    val text = if (attempt.correct) "Correct!" else "Incorrect, Target was ${attempt.target.joinToString("")}"

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