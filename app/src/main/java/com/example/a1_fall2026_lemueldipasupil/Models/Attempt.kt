package com.example.a1_fall2026_lemueldipasupil.Models

import java.util.Date

data class Attempt(
    val sequenceLength: Int, //     Length of the sequence
    val target: List<Int>, //       The sequence the user needs to memorize
    val guess: List<Int>,//         The user's guess
    val correct: Boolean, //        Whether the guess was correct
    val timestamp: Date = Date()//  Timestamp of the attempt
)