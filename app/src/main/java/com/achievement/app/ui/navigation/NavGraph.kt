package com.achievement.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.achievement.app.ui.screens.gamedetail.GameDetailScreen
import com.achievement.app.ui.screens.games.GameListScreen
import com.achievement.app.ui.screens.home.HomeScreen
import com.achievement.app.ui.screens.login.LoginScreen
import com.achievement.app.ui.screens.profile.ProfileScreen
import com.achievement.app.ui.screens.trophy.TrophyListScreen
import com.achievement.app.ui.theme.PSNBackground
import com.achievement.app.ui.theme.PSNBlue
import com.achievement.app.ui.theme.PSNOnSurfaceVariant
import com.achievement.app.viewmodel.AuthViewModel
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Login : Screen("login", "登录", Icons.Default.Person)
    data object Home : Screen("home", "概览", Icons.Default.Home)
    data object Games : Screen("games", "游戏", Icons.Default.VideogameAsset)
    data object Trophies : Screen("trophies", "奖杯", Icons.Default.EmojiEvents)
    data object Profile : Screen("profile", "我的", Icons.Default.Person)
}

object NavRoutes {
    const val GAME_DETAIL = "gamedetail/{appId}/{npCommunicationId}"
    fun gameDetail(appId: String, npCommunicationId: String?): String {
        val encodedId = URLEncoder.encode(npCommunicationId ?: "none", "UTF-8")
        val encodedAppId = URLEncoder.encode(appId, "UTF-8")
        return "gamedetail/$encodedAppId/$encodedId"
    }
}

@Composable
fun NavGraph(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val uiState by authViewModel.uiState.collectAsState()

    val navItems = listOf(Screen.Home, Screen.Games, Screen.Trophies, Screen.Profile)

    if (!uiState.isLoggedIn) {
        LoginScreen(
            onLoginSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        )
    } else {
        Scaffold(
            containerColor = PSNBackground,
            bottomBar = {
                val currentDestination = navController.currentBackStackEntryAsState().value?.destination
                val showBottomBar = currentDestination?.route in navItems.map { it.route }

                if (showBottomBar) {
                    NavigationBar(
                        containerColor = PSNBackground,
                        tonalElevation = 0.dp
                    ) {
                        navItems.forEach { screen ->
                            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        screen.icon,
                                        contentDescription = screen.title,
                                        tint = if (selected) PSNBlue else PSNOnSurfaceVariant
                                    )
                                },
                                label = {
                                    Text(
                                        screen.title,
                                        color = if (selected) PSNBlue else PSNOnSurfaceVariant
                                    )
                                },
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PSNBlue,
                                    unselectedIconColor = PSNOnSurfaceVariant,
                                    selectedTextColor = PSNBlue,
                                    unselectedTextColor = PSNOnSurfaceVariant,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            androidx.navigation.compose.NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onGameClick = { appId, npCommunicationId ->
                            navController.navigate(NavRoutes.gameDetail(appId, npCommunicationId))
                        }
                    )
                }
                composable(Screen.Games.route) {
                    GameListScreen(
                        onGameClick = { appId, npCommunicationId ->
                            navController.navigate(NavRoutes.gameDetail(appId, npCommunicationId))
                        }
                    )
                }
                composable(Screen.Trophies.route) {
                    TrophyListScreen(
                        onGameClick = { appId, npCommunicationId ->
                            navController.navigate(NavRoutes.gameDetail(appId, npCommunicationId))
                        }
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onLogout = { authViewModel.logout() }
                    )
                }
                composable(
                    route = NavRoutes.GAME_DETAIL,
                    arguments = listOf(
                        androidx.navigation.navArgument("appId") { type = androidx.navigation.NavType.StringType },
                        androidx.navigation.navArgument("npCommunicationId") { type = androidx.navigation.NavType.StringType }
                    )
                ) {
                    GameDetailScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
