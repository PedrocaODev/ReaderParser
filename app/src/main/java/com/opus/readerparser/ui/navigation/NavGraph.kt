package com.opus.readerparser.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.opus.readerparser.ui.browse.BrowseScreen
import com.opus.readerparser.ui.components.BottomNavBar
import com.opus.readerparser.ui.components.BottomNavDestination
import com.opus.readerparser.ui.downloads.DownloadsScreen
import com.opus.readerparser.ui.library.LibraryScreen
import com.opus.readerparser.ui.reader.ReaderScreen
import com.opus.readerparser.ui.series.SeriesScreen
import com.opus.readerparser.ui.settings.SettingsScreen

internal val OuterScaffoldWindowInsets = WindowInsets(0, 0, 0, 0)

internal fun NavOptionsBuilder.configureTopLevelNavigation(startDestinationId: Int) {
    popUpTo(startDestinationId) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

internal fun NavController.navigateToTopLevelDestination(route: String) {
    navigate(route) {
        configureTopLevelNavigation(graph.findStartDestination().id)
    }
}

internal val bottomNavRoutes = setOf(
    Destinations.LIBRARY,
    Destinations.BROWSE,
    Destinations.DOWNLOADS,
)

@Composable
fun AppNavGraph(onNavGraphReady: (NavController) -> Unit = {}) {
    val navController = rememberNavController()

    androidx.compose.runtime.LaunchedEffect(navController) {
        onNavGraphReady(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavDestinations = listOf(
        BottomNavDestination(
            route = Destinations.LIBRARY,
            label = "Library",
            icon = Icons.Filled.CollectionsBookmark,
            contentDescription = "Library",
        ),
        BottomNavDestination(
            route = Destinations.BROWSE,
            label = "Browse",
            icon = Icons.Filled.Explore,
            contentDescription = "Browse",
        ),
        BottomNavDestination(
            route = Destinations.DOWNLOADS,
            label = "Downloads",
            icon = Icons.Filled.Download,
            contentDescription = "Downloads",
        ),
    )

    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        contentWindowInsets = OuterScaffoldWindowInsets,
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    destinations = bottomNavDestinations,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigateToTopLevelDestination(route)
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.LIBRARY,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            composable(Destinations.LIBRARY) {
                LibraryScreen(
                    onNavigateToSeries = { series ->
                        navController.navigate(Destinations.series(series.sourceId, series.url))
                    },
                    onNavigateToSettings = {
                        navController.navigate(Destinations.SETTINGS)
                    },
                    onBrowseSources = {
                        navController.navigateToTopLevelDestination(Destinations.BROWSE)
                    },
                )
            }
            composable(Destinations.BROWSE) {
                BrowseScreen(
                    onNavigateToSeries = { series ->
                        navController.navigate(Destinations.series(series.sourceId, series.url))
                    },
                    onNavigateToSettings = {
                        navController.navigate(Destinations.SETTINGS)
                    },
                )
            }
            composable(Destinations.DOWNLOADS) {
                DownloadsScreen(
                    onNavigateToSettings = {
                        navController.navigate(Destinations.SETTINGS)
                    },
                )
            }
            composable(Destinations.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = Destinations.SERIES,
                arguments = listOf(
                    navArgument("sourceId") { type = NavType.LongType },
                    navArgument("seriesUrl") { type = NavType.StringType },
                ),
            ) {
                SeriesScreen(
                    onNavigateToReader = { sourceId, seriesUrl, chapterUrl, contentType ->
                        navController.navigate(Destinations.reader(sourceId, seriesUrl, chapterUrl, contentType))
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = Destinations.READER,
                arguments = listOf(
                    navArgument("sourceId") { type = NavType.LongType },
                    navArgument("seriesUrl") { type = NavType.StringType },
                    navArgument("chapterUrl") { type = NavType.StringType },
                    navArgument("contentType") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                ReaderScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToChapter = { chapter ->
                        val contentType = backStackEntry.arguments?.getString("contentType") ?: "NOVEL"
                        navController.navigate(
                            Destinations.reader(chapter.sourceId, chapter.seriesUrl, chapter.url, contentType)
                        ) {
                            popUpTo(Destinations.READER) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}
