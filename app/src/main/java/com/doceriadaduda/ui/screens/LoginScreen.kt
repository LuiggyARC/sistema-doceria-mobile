package com.doceriadaduda.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.doceriadaduda.ui.theme.LocalDynamicThemeState

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.graphics.BitmapFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var companyNameInput by remember { mutableStateOf("") }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dynamicThemeState = LocalDynamicThemeState.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                dynamicThemeState.isLoading = true
                try {
                    val selectedBitmap = withContext(Dispatchers.IO) {
                        // Redimensiona o bitmap para evitar crash por memória (OOM)
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        context.contentResolver.openInputStream(it)?.use { stream ->
                            BitmapFactory.decodeStream(stream, null, options)
                        }
                        
                        var width = options.outWidth
                        var height = options.outHeight
                        var inSampleSize = 1
                        while (width > 600 || height > 600) {
                            width /= 2
                            height /= 2
                            inSampleSize *= 2
                        }
                        
                        options.inJustDecodeBounds = false
                        options.inSampleSize = inSampleSize
                        
                        context.contentResolver.openInputStream(it)?.use { stream ->
                            BitmapFactory.decodeStream(stream, null, options)
                        }
                    }

                    selectedBitmap?.let { b ->
                        bitmap = b
                        withContext(Dispatchers.Default) {
                            val palette = Palette.from(b).generate()
                            
                            // Tenta pegar uma cor vibrante para os textos/botoes (Primaria)
                            // E uma cor dominante ou escura para o fundo
                            val primaryColorInt = palette.vibrantSwatch?.rgb ?: palette.lightVibrantSwatch?.rgb ?: palette.dominantSwatch?.rgb
                            val backgroundColorInt = palette.darkMutedSwatch?.rgb ?: palette.mutedSwatch?.rgb ?: palette.dominantSwatch?.rgb

                            withContext(Dispatchers.Main) {
                                primaryColorInt?.let { dynamicThemeState.primaryColor = Color(it) }
                                backgroundColorInt?.let { dynamicThemeState.backgroundColor = Color(it) }
                                
                                // Salva no "banco" (SharedPreferences) para persistência
                                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                prefs.edit().apply {
                                    primaryColorInt?.let { putInt("theme_color", it) }
                                    backgroundColorInt?.let { putInt("bg_color", it) }
                                }.apply()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    dynamicThemeState.isLoading = false
                }
            }
        }
    }

    if (dynamicThemeState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Processando sua marca...", style = MaterialTheme.typography.bodyMedium)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bem-vindo",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Seleção de Logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Logo da Empresa",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Carregar Logo",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Text(
                text = "Toque para carregar sua logo",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = companyNameInput,
                onValueChange = { companyNameInput = it },
                label = { Text("Nome da Empresa") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    if (companyNameInput.isNotBlank()) {
                        dynamicThemeState.companyName = companyNameInput
                        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit().putString("company_name", companyNameInput).apply()
                        onLoginSuccess(companyNameInput)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = companyNameInput.isNotBlank()
            ) {
                Text("Entrar no Sistema", modifier = Modifier.padding(8.dp))
            }
        }
    }
}
