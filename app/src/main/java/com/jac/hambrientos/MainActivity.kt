package com.jac.hambrientos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jac.hambrientos.ui.navigation.AppNavGraph
import com.jac.hambrientos.ui.theme.HambrientosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HambrientosTheme(darkTheme = false, dynamicColor = false) {
                AppNavGraph()
            }
        }
    }
}
