package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.ui.SkillskapesApp
import com.example.ui.SkillskapesViewModel
import com.example.ui.SkillskapesViewModelFactory
import com.example.ui.theme.SkillskapesTheme

class MainActivity : ComponentActivity() {
    private val viewModel: SkillskapesViewModel by viewModels {
        SkillskapesViewModelFactory((application as SkillskapesApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillskapesTheme {
                SkillskapesApp(viewModel = viewModel)
            }
        }
    }
}
