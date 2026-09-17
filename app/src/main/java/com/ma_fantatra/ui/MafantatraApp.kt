package com.ma_fantatra.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ma_fantatra.R
import com.ma_fantatra.ui.commune.CommuneDetailScreen
import com.ma_fantatra.ui.commune.CommunesScreen
import com.ma_fantatra.ui.detail.ProcedureDetailScreen
import com.ma_fantatra.ui.directory.DirectoryScreen
import com.ma_fantatra.ui.directory.FokontanyDetailScreen
import com.ma_fantatra.ui.navigation.CommuneDetailRoute
import com.ma_fantatra.ui.navigation.CommunesRoute
import com.ma_fantatra.ui.navigation.DirectoryRoute
import com.ma_fantatra.ui.navigation.FokontanyDetailRoute
import com.ma_fantatra.ui.navigation.ProcedureDetailRoute
import com.ma_fantatra.ui.navigation.ProceduresRoute
import com.ma_fantatra.ui.procedures.ProceduresScreen

@Composable
fun MafantatraApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val onProcedures = currentRoute == ProceduresRoute::class.qualifiedName ||
        currentRoute == ProcedureDetailRoute::class.qualifiedName
    val onDirectory = currentRoute == DirectoryRoute::class.qualifiedName ||
        currentRoute == FokontanyDetailRoute::class.qualifiedName
    val onCommunes = currentRoute == CommunesRoute::class.qualifiedName ||
        currentRoute == CommuneDetailRoute::class.qualifiedName

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_home),
                        contentDescription = stringResource(R.string.nav_procedures),
                    )
                },
                label = { Text(stringResource(R.string.nav_procedures)) },
                selected = onProcedures,
                onClick = {
                    navController.navigate(ProceduresRoute) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
            item(
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_place),
                        contentDescription = stringResource(R.string.nav_directory),
                    )
                },
                label = { Text(stringResource(R.string.nav_directory)) },
                selected = onDirectory,
                onClick = {
                    navController.navigate(DirectoryRoute) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
            item(
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_commune),
                        contentDescription = stringResource(R.string.nav_communes),
                    )
                },
                label = { Text(stringResource(R.string.nav_communes)) },
                selected = onCommunes,
                onClick = {
                    navController.navigate(CommunesRoute) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = ProceduresRoute,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable<ProceduresRoute> {
                ProceduresScreen(
                    onProcedureClick = { procedureId ->
                        navController.navigate(ProcedureDetailRoute(procedureId))
                    },
                )
            }
            composable<DirectoryRoute> {
                DirectoryScreen(
                    onFokontanyClick = { fokontanyId ->
                        navController.navigate(FokontanyDetailRoute(fokontanyId))
                    },
                )
            }
            composable<FokontanyDetailRoute> {
                FokontanyDetailScreen(onBack = { navController.popBackStack() })
            }
            composable<CommunesRoute> {
                CommunesScreen(
                    onCommuneClick = { communeId ->
                        navController.navigate(CommuneDetailRoute(communeId))
                    },
                )
            }
            composable<CommuneDetailRoute> {
                CommuneDetailScreen(onBack = { navController.popBackStack() })
            }
            composable<ProcedureDetailRoute> {
                ProcedureDetailScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}