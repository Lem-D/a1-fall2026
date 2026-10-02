package com.example.a1_fall2026_lemueldipasupil.ControllerView

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.a1_fall2026_lemueldipasupil.Models.Attempt
import com.example.a1_fall2026_lemueldipasupil.Models.AttemptLog
import com.example.a1_fall2026_lemueldipasupil.Models.SequenceGame
import com.example.a1_fall2026_lemueldipasupil.Models.Summary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

// Enum to track what part of the UI should be visible
enum class GameState {
    SELECTING_LENGTH, SHOWING_SEQUENCE, WAITING_FOR_INPUT, SHOWING_FEEDBACK
}

// Acts as the main Controller for the UI, holding the current game state
// and routing the user to the correct screens based on that state.
@Composable
fun GameScreen(game: SequenceGame, log: AttemptLog, modifier: Modifier = Modifier) {
    var currentState by remember { mutableStateOf(GameState.SELECTING_LENGTH) }
    var lastAttempt by remember { mutableStateOf<Attempt?>(null) }
    var history by remember { mutableStateOf(log.all().toList()) }
    var summary by remember { mutableStateOf(log.summary()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pushes title down ~1/3 from top of screen
        Spacer(modifier = Modifier.weight(1f))

        // Title
        Text(
            text = "Rapid Recall",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Active Game Input & Controls
        when (currentState) {
            // User chooses how long the sequence should be
            GameState.SELECTING_LENGTH -> {
                RenderLengthSelector(
                    onStartClick = { length ->
                        game.start(length)
                        currentState = GameState.SHOWING_SEQUENCE
                    }
                )
            }

            // Displays the sequence of digits the user needs to memorize
            GameState.SHOWING_SEQUENCE -> {
                ShowSequence(
                    game = game,
                    onTimeout = { currentState = GameState.WAITING_FOR_INPUT }
                )
            }

            // User inputs their guess
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

            // Displays the result of the user's guess
            GameState.SHOWING_FEEDBACK -> {
                RenderFeedback(lastAttempt!!)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { currentState = GameState.SELECTING_LENGTH }) {
                    Text("Play Again")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Card directly beneath the game input
        RenderSummary(summary)
        Spacer(modifier = Modifier.height(8.dp))

        // Session History in remaining space
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
        ) {
            RenderHistory(history)
        }
    }
}

// Displays an input field and button to let the user choose how long
// the memory sequence should be (between 1 and 10) before starting the game.
@Composable
fun RenderLengthSelector(onStartClick: (Int) -> Unit) {
    var lengthInput by remember { mutableStateOf("4") }
    val parsedLength = lengthInput.toIntOrNull()
    val isValid = parsedLength != null && parsedLength in 1..10

    // Input field and button
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Choose Sequence Length (1-10):", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = lengthInput,
            onValueChange = { lengthInput = it },
            label = { Text("Length (1-10)") },
            isError = !isValid && lengthInput.isNotEmpty(), // Error if invalid input or empty
            // Provides a hint if the input is invalid
            supportingText = {
                if (!isValid && lengthInput.isNotEmpty()) {
                    Text("Please enter a number between 1 and 10", color = MaterialTheme.colorScheme.error)
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val finalLength = (parsedLength ?: 4).coerceIn(1, 10)
                onStartClick(finalLength)
            },
            enabled = isValid || lengthInput.isEmpty() // Button disabled if invalid input
        ) {
            Text("Start Game")
        }
    }
}

// Displays one digit at a time of the generated sequence based on sequence length chosen.
@Composable
fun ShowSequence(
    game: SequenceGame,
    digitDisplayMs: Long = 1000L,
    onTimeout: () -> Unit
) {
    var currentDigit by remember { mutableStateOf<Int?>(null) }
    var digitIndex by remember { mutableIntStateOf(0) }
    val totalDigits = game.target.size

    // Launches a coroutine to display the sequence
    LaunchedEffect(game) {
        for (i in 0 until totalDigits) {
            currentDigit = game.nextDigit()
            digitIndex = i + 1
            delay(digitDisplayMs)
        }
        onTimeout()
    }

    // Displays the current digit
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Memorize the sequence",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Digit $digitIndex of $totalDigits",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = currentDigit?.toString() ?: "",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onTimeout) {
            Text("Skip to Guess")
        }
    }
}

// Provides a text field for the user to type in their memorized guess
// and a button to submit it back to the game logic.
@Composable
fun RenderInput(onSubmit: (IntArray) -> Unit) {
    var guessInput by remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = guessInput,
            onValueChange = { guessInput = it },
            label = { Text("Enter Your Guess") }
        )
        Spacer(modifier = Modifier.height(8.dp))
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

// Displays the result of the user's latest attempt, showing green/red status
// along with a side-by-side comparison of the target sequence and user's guess.
@Composable
fun RenderFeedback(attempt: Attempt) {
    val isCorrect = attempt.correct
    val color = if (isCorrect) Color.Green else Color.Red
    val text = if (isCorrect) "Correct!" else "Incorrect"

    // Displays the result and the target and guess sequences
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text, color = color, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Target Sequence: ${attempt.target.joinToString(" ")}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Your Guess:        ${attempt.guess.joinToString(" ")}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Renders a visual card summarizing the player's overall performance,
// including total games played, total correct, and accuracy percentage.
@Composable
fun RenderSummary(summary: Summary) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Plays: ${summary.total}")
            Text("Correct Plays: ${summary.correct}")
            Text("Accuracy: ${summary.accuracyPct}%")
        }
    }
}

// Displays a scrollable list of all previous attempts made during
// the current session, showing length, target, guess, result, and timestamp.
@Composable
fun RenderHistory(attempts: List<Attempt>) {
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(attempts) { attempt ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Displays the attempt details
                    Text(
                        text = "Length: ${attempt.sequenceLength} | Result: ${if (attempt.correct) "Correct" else "Incorrect"} | Time: ${timeFormat.format(attempt.timestamp)}",
                        // Green for correct, red for incorrect
                        style = MaterialTheme.typography.labelLarge,
                        color = if (attempt.correct) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Target: ${attempt.target.joinToString(" ")} | Guessed: ${attempt.guess.joinToString(" ")}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
