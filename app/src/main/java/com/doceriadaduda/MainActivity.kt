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
import com.doceriadaduda.di.AppModule
import com.doceriadaduda.ui.navigation.AppNavigation
import com.doceriadaduda.ui.theme.DoceriaDaDudaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializa o AppModule
        AppModule.init(this)
        
        setContent {
            DoceriaDaDudaAppTheme {
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

 @Preview(showBackground = true) @Composable
fun DefaultPreview() {
    DoceriaDaDudaAppTheme {
        // TODO: Implement preview content here
    }
}
