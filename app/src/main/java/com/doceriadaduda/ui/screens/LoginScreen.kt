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

import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.doceriadaduda.di.AppModule
import com.doceriadaduda.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = AppModule.authViewModel,
    onLoginSuccess: (String) -> Unit
) {
    var isRegistering by remember { mutableStateOf(false) }
    var companyNameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dynamicThemeState = LocalDynamicThemeState.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                // ... (mantém a lógica de processamento de imagem)
                dynamicThemeState.isLoading = true
                try {
                    val selectedBitmap = withContext(Dispatchers.IO) {
                        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        context.contentResolver.openInputStream(it)?.use { stream -> BitmapFactory.decodeStream(stream, null, options) }
                        var width = options.outWidth
                        var height = options.outHeight
                        var inSampleSize = 1
                        while (width > 600 || height > 600) { width /= 2; height /= 2; inSampleSize *= 2 }
                        options.inJustDecodeBounds = false
                        options.inSampleSize = inSampleSize
                        context.contentResolver.openInputStream(it)?.use { stream -> BitmapFactory.decodeStream(stream, null, options) }
                    }

                    selectedBitmap?.let { b ->
                        bitmap = b
                        withContext(Dispatchers.Default) {
                            val palette = Palette.from(b).generate()
                            val primaryColorInt = palette.vibrantSwatch?.rgb ?: palette.lightVibrantSwatch?.rgb ?: palette.dominantSwatch?.rgb
                            val backgroundColorInt = palette.darkMutedSwatch?.rgb ?: palette.mutedSwatch?.rgb ?: palette.dominantSwatch?.rgb

                            withContext(Dispatchers.Main) {
                                primaryColorInt?.let { dynamicThemeState.primaryColor = Color(it) }
                                backgroundColorInt?.let { dynamicThemeState.backgroundColor = Color(it) }
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

    if (isLoading || dynamicThemeState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Processando...", style = MaterialTheme.typography.bodyMedium)
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
                text = if (isRegistering) "Criar Conta" else "Bem-vindo",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            if (isRegistering) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap!!.asImageBitmap(),
                            contentDescription = "Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Carregar Logo",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Text("Logo da Empresa", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isRegistering) {
                OutlinedTextField(
                    value = companyNameInput,
                    onValueChange = { companyNameInput = it },
                    label = { Text("Nome da Empresa") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    if (isRegistering) {
                        authViewModel.register(companyNameInput, emailInput, passwordInput) { name ->
                            dynamicThemeState.companyName = name
                            onLoginSuccess(name)
                        }
                    } else {
                        authViewModel.login(emailInput, passwordInput) { name ->
                            dynamicThemeState.companyName = name
                            onLoginSuccess(name)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = emailInput.isNotBlank() && passwordInput.isNotBlank() && (!isRegistering || companyNameInput.isNotBlank())
            ) {
                Text(if (isRegistering) "Cadastrar" else "Entrar", modifier = Modifier.padding(8.dp))
            }

            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(if (isRegistering) "Já tenho uma conta" else "Não tenho conta? Cadastre-se")
            }
        }
    }
}
