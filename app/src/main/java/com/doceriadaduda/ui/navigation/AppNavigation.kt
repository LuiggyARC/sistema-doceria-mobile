package com.doceriadaduda.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.doceriadaduda.ui.theme.LocalDynamicThemeState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.doceriadaduda.ui.screens.DashboardScreen
import com.doceriadaduda.ui.screens.DespesaScreen
import com.doceriadaduda.ui.screens.EstoqueScreen
import com.doceriadaduda.ui.screens.RelatoriosScreen
import com.doceriadaduda.ui.screens.VendaScreen

import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.doceriadaduda.ui.screens.LoginScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Login : Screen("login", "Login", Icons.AutoMirrored.Filled.Login)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Venda : Screen("venda", "Venda", Icons.Default.ShoppingCart)
    object Estoque : Screen("estoque", "Estoque", Icons.Default.Inventory)
    object Despesa : Screen("despesa", "Despesa", Icons.AutoMirrored.Filled.ReceiptLong)
    object Relatorios : Screen("relatorios", "Relatorios", Icons.Default.BarChart)
    object Config : Screen("config", "Ajustes", Icons.Default.Settings)
}

val items = listOf(
    Screen.Dashboard,
    Screen.Venda,
    Screen.Estoque,
    Screen.Despesa,
    Screen.Relatorios,
    Screen.Config
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val dynamicThemeState = LocalDynamicThemeState.current
    
    // O estado de login agora é baseado na presença do nome da empresa no estado global
    // que foi carregado na MainActivity
    var isLoggedIn by remember(dynamicThemeState.companyName) { 
        mutableStateOf(dynamicThemeState.companyName != "Pai D’égua Hub" && dynamicThemeState.companyName.isNotBlank())
    }

    if (!isLoggedIn) {
        NavHost(navController, startDestination = Screen.Login.route) {
            composable(Screen.Login.route) {
                LoginScreen(onLoginSuccess = { _ ->
                    // Ao logar com sucesso, o isLoggedIn mudará automaticamente
                    // porque dynamicThemeState.companyName será atualizado na LoginScreen
                })
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController, startDestination = Screen.Dashboard.route, Modifier.padding(innerPadding)) {
                composable(Screen.Dashboard.route) { DashboardScreen(companyName = dynamicThemeState.companyName) }
                composable(Screen.Venda.route) { VendaScreen() }
                composable(Screen.Estoque.route) { EstoqueScreen() }
                composable(Screen.Despesa.route) { DespesaScreen() }
                composable(Screen.Relatorios.route) { RelatoriosScreen() }
                composable(Screen.Config.route) { com.doceriadaduda.ui.screens.ConfigScreen() }
            }
        }
    }
}
