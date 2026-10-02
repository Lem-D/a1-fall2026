package com.example.a1_fall2026_lemueldipasupil.Models

data class Summary(
    val total: Int, // Total games played
    val correct: Int, // Total correct guesses
    val accuracyPct: Float // Percentage of correct guesses
)