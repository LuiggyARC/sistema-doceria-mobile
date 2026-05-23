package com.doceriadaduda.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.doceriadaduda.ui.theme.LocalDynamicThemeState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.doceriadaduda.di.AppModule
import com.doceriadaduda.ui.screens.*
import com.doceriadaduda.data.local.SessionManager
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Login : Screen("login", "Login", Icons.AutoMirrored.Filled.Login)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Venda : Screen("venda", "Nova Venda", Icons.Default.ShoppingCart)
    object Historico : Screen("historico", "Histórico Vendas", Icons.Default.History)
    object Estoque : Screen("estoque", "Estoque", Icons.Default.Inventory)
    object Despesa : Screen("despesa", "Despesa", Icons.AutoMirrored.Filled.ReceiptLong)
    object Funcionarios : Screen("funcionarios", "Funcionários", Icons.Default.People)
    object Relatorios : Screen("relatorios", "Relatorios", Icons.Default.BarChart)
    object Config : Screen("config", "Ajustes", Icons.Default.Settings)
    object Admin : Screen("admin", "Painel Admin", Icons.Default.AdminPanelSettings)
}

val items = listOf(
    Screen.Venda,
    Screen.Historico,
    Screen.Dashboard,
    Screen.Estoque,
    Screen.Despesa,
    Screen.Funcionarios,
    Screen.Relatorios,
    Screen.Config
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val dynamicThemeState = LocalDynamicThemeState.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val sessionManager = remember { AppModule.sessionManager }
    var isLoggedIn by remember { mutableStateOf(sessionManager.isLoggedIn()) }

    // Observa mudanças no estado global da empresa para atualizar o login
    LaunchedEffect(dynamicThemeState.companyName) {
        isLoggedIn = sessionManager.isLoggedIn()
    }

    if (!isLoggedIn) {
        NavHost(navController, startDestination = Screen.Login.route) {
            composable(Screen.Login.route) {
                LoginScreen(onLoginSuccess = { _ -> 
                    // O estado isLoggedIn agora é observado pelo dynamicThemeState.companyName
                })
            }
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = dynamicThemeState.companyName,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 20.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    val isAdmin = sessionManager.isAdmin
                    
                    val visibleItems = if (isAdmin) items + Screen.Admin else items

                    visibleItems.forEach { screen ->
                        NavigationDrawerItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(dynamicThemeState.companyName, fontSize = 18.sp) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController, 
                    startDestination = Screen.Venda.route, 
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Dashboard.route) { DashboardScreen(companyName = dynamicThemeState.companyName) }
                    composable(Screen.Venda.route) { VendaScreen() }
                    composable(Screen.Historico.route) { HistoricoVendasScreen() }
                    composable(Screen.Estoque.route) { EstoqueScreen() }
                    composable(Screen.Despesa.route) { DespesaScreen() }
                    composable(Screen.Funcionarios.route) { FuncionarioScreen() }
                    composable(Screen.Relatorios.route) { RelatoriosScreen() }
                    composable(Screen.Config.route) { ConfigScreen() }
                    composable(Screen.Admin.route) { AdminScreen() }
                }
            }
        }
    }
}
