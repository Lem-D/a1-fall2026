package com.example.a1_fall2026_lemueldipasupil.Models

import java.util.Date

data class Attempt(
    val sequenceLength: Int,
    val target: List<Int>,
    val guess: List<Int>,
    val correct: Boolean,
    val timestamp: Date = Date()
)