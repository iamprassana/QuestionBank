package com.example.question_bank.pages.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.question_bank.navigation.NavItem
import com.example.question_bank.ui.theme.IconColor
import com.example.question_bank.ui.theme.MainColor
import com.example.question_bank.ui.theme.TextColor

@Composable
fun SearchPage(navController: NavController) {
    val navItems = listOf(
        NavItem("Home", "home", Icons.Filled.Home),
        NavItem("Search", "search", Icons.Filled.Search),
        NavItem("Settings", "settings", Icons.Filled.Settings)
    )

    var selectedItem by remember { mutableIntStateOf(1) } // search page default

    Scaffold(
        containerColor = MainColor,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MainColor
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        label = { Text(item.label, color = TextColor) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (selectedItem == index) TextColor else IconColor
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MainColor
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Hello there", color = TextColor, style = MaterialTheme.typography.titleLarge)
        }
    }
}