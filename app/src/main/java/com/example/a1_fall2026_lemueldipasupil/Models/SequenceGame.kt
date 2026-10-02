package com.example.a1_fall2026_lemueldipasupil.Models

import kotlin.random.Random

class SequenceGame {
    var target: IntArray = IntArray(0)
        private set
    private var index: Int = 0

    fun start(length: Int) {
        target = generateSequence(length)
        index = 0
    }

    fun generateSequence(length: Int): IntArray {
        // Assuming single digits 0-9 for a sequence memory game
        return IntArray(length) { Random.nextInt(0, 10) }
    }

    fun nextDigit(): Int {
        if (index < target.size) {
            return target[index++]
        }
        return -1
    }

    // Creates and returns the Attempt object based on the dependency
    fun submitGuess(rawInput: IntArray): Attempt {
        val isCorrect = target.contentEquals(rawInput)
        return Attempt(
            sequenceLength = target.size,
            target = target.toList(),
            guess = rawInput.toList(),
            correct = isCorrect
        )
    }
}
