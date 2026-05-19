package com.doceriadaduda.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ReceiptLong
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.doceriadaduda.ui.screens.DashboardScreen
import com.doceriadaduda.ui.screens.DespesaScreen
import com.doceriadaduda.ui.screens.EstoqueScreen
import com.doceriadaduda.ui.screens.RelatoriosScreen
import com.doceriadaduda.ui.screens.VendaScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Venda : Screen("venda", "Venda", Icons.Default.ShoppingCart)
    object Estoque : Screen("estoque", "Estoque", Icons.Default.Inventory)
    object Despesa : Screen("despesa", "Despesa", Icons.Default.ReceiptLong)
    object Relatorios : Screen("relatorios", "Relatorios", Icons.Default.BarChart)
}

val items = listOf(
    Screen.Dashboard,
    Screen.Venda,
    Screen.Estoque,
    Screen.Despesa,
    Screen.Relatorios
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
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
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Venda.route) { VendaScreen() }
            composable(Screen.Estoque.route) { EstoqueScreen() }
            composable(Screen.Despesa.route) { DespesaScreen() }
            composable(Screen.Relatorios.route) { RelatoriosScreen() }
        }
    }
}
