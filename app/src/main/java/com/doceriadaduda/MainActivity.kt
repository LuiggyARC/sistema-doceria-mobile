package com.doceriadaduda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.doceriadaduda.ui.navigation.AppNavigation
import com.doceriadaduda.ui.theme.DoceriaDaDudaAppTheme

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.doceriadaduda.ui.theme.DynamicThemeState
import com.doceriadaduda.ui.theme.LocalDynamicThemeState

import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import android.content.Context
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val dynamicThemeState = remember { DynamicThemeState() }
            val context = LocalContext.current
            
            // Carrega as preferências salvas ao iniciar
            LaunchedEffect(Unit) {
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val savedColor = prefs.getInt("theme_color", -1)
                val savedBgColor = prefs.getInt("bg_color", -1)
                val savedName = prefs.getString("company_name", null)
                
                if (savedColor != -1) {
                    dynamicThemeState.primaryColor = Color(savedColor)
                }
                if (savedBgColor != -1) {
                    dynamicThemeState.backgroundColor = Color(savedBgColor)
                }
                if (savedName != null) {
                    dynamicThemeState.companyName = savedName
                } else {
                    dynamicThemeState.companyName = "Pai D’égua Hub"
                }
            }

            CompositionLocalProvider(LocalDynamicThemeState provides dynamicThemeState) {
                DoceriaDaDudaAppTheme(dynamicThemeState = dynamicThemeState) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

 @Preview(showBackground = true) @Composable
fun DefaultPreview() {
    DoceriaDaDudaAppTheme {
        // TODO: Implement preview content here
    }
}
