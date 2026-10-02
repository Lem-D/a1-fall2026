package com.example.a1_fall2026_lemueldipasupil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.a1_fall2026_lemueldipasupil.ControllerView.GameScreen
import com.example.a1_fall2026_lemueldipasupil.ui.theme.A1fall2026LemuelDipasupilTheme
import com.example.a1_fall2026_lemueldipasupil.Models.AttemptLog
import com.example.a1_fall2026_lemueldipasupil.Models.SequenceGame


class MainActivity : ComponentActivity() {
    private val sequenceGame = SequenceGame()
    private val attemptLog = AttemptLog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A1fall2026LemuelDipasupilTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(
                        game = sequenceGame,
                        log = attemptLog,
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}
