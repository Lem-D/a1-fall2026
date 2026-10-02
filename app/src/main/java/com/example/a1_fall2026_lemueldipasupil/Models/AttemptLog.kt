package com.example.a1_fall2026_lemueldipasupil.Models

class AttemptLog {
    // Stores Attempt objects (0..* aggregation)
    private val attempts = mutableListOf<Attempt>()

    // Adds an Attempt object to the log
    fun add(attempt: Attempt) {
        attempts.add(attempt)
    }

    // Returns an array of all Attempt objects
    fun all(): Array<Attempt> {
        return attempts.toTypedArray()
    }

    // Returns a Summary object based on the dependency[
    fun summary(): Summary {
        val total = attempts.size
        val correct = attempts.count { it.correct }
        val accuracyPct = if (total > 0) (correct.toFloat() / total) * 100f else 0f

        return Summary(total, correct, accuracyPct)
    }
}