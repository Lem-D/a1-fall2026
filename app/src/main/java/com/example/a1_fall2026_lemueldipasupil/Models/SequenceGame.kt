package com.example.a1_fall2026_lemueldipasupil.Models

import kotlin.random.Random

// SequenceGame class to manage the game logic
class SequenceGame {
    var target: IntArray = IntArray(0)
        private set
    private var index: Int = 0

    // Starts the game with a given length
    fun start(length: Int) {
        val validLength = length.coerceIn(1, 10) //coerceIn ensures it's between 1 and 10
        target = generateSequence(validLength)
        index = 0
    }

    // Generates a random sequence of digits
    fun generateSequence(length: Int): IntArray {
        val validLength = length.coerceIn(1, 10)
        // Assuming single digits 0-9 for a sequence memory game
        return IntArray(validLength) { Random.nextInt(0, 10) }
    }

    // Returns the next digit in the sequence
    fun nextDigit(): Int {
        if (index < target.size) {
            return target[index++]
        }
        return -1
    }

    // Creates and returns the Attempt object based on the dependency
    fun submitGuess(rawInput: IntArray): Attempt {
        val comparison = compare(target, rawInput)
        val isCorrect = rawInput.size == target.size && comparison.all { it }
        return Attempt(
            sequenceLength = target.size,
            target = target.toList(),
            guess = rawInput.toList(),
            correct = isCorrect
        )
    }

    // Compares the target and guess arrays and returns a BooleanArray
    fun compare(target: IntArray, guess: IntArray): BooleanArray {
        val result = BooleanArray(target.size)
        for (i in target.indices) {
            result[i] = (i < guess.size && target[i] == guess[i])
        }
        return result
    }
}
