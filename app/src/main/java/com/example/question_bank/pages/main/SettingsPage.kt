package com.example.question_bank.pages.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.question_bank.navigation.NavItem
import com.example.question_bank.repositories.authentication.FirebaseAuth
import com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel.DataViewModel
import com.example.question_bank.ui.theme.IconColor
import com.example.question_bank.ui.theme.MainColor
import com.example.question_bank.ui.theme.TextColor
import kotlinx.coroutines.launch

@Composable
fun SettingsPage(
    navController: NavController,
    auth: FirebaseAuth,
    dataModel: DataViewModel,
) {
    val navItems = listOf(
        NavItem("Home", "home", Icons.Filled.Home),
        NavItem("Search", "search", Icons.Filled.Search),
        NavItem("Settings", "settings", Icons.Filled.Settings)
    )

    var selectedItem by remember { mutableStateOf(2) } // settings page by default
    val scope = rememberCoroutineScope()
    val userState by dataModel.user.collectAsState()

    Scaffold(
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
        },
        containerColor = MainColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // keeps title at top, logout at bottom
        ) {
            // Top Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(
                    "Account Information",
                    color = TextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (userState?.email == null) {
                    CircularProgressIndicator(color = MainColor)
                } else {
                    // Info Card (centered)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f) // narrower card to keep centered look
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(0.dp),
                        colors = CardDefaults.cardColors(MainColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp, horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            InformationRow(
                                Icons.Filled.Email,
                                "Email",
                                userState?.email ?: "Loading"
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            InformationRow(
                                Icons.Filled.Person,
                                "Name",
                                userState?.name ?: "Loading"
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            InformationRow(
                                Icons.Filled.AccountBox,
                                "Organization",
                                userState?.organization ?: "Loading"
                            )
                        }
                    }
                }
            }

            // Bottom Section - Logout Button
            ElevatedButton(
                onClick = {
                    scope.launch {
                        dataModel.clearData()
                        auth.logout()
                        navController.popBackStack()
                        navController.navigate("login")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(bottom = 24.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MainColor,
                    contentColor = TextColor,
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 6.dp)
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                Text("Log Out", color = TextColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun InformationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = IconColor,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 14.sp, color = IconColor, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, color = TextColor, fontWeight = FontWeight.Medium)
        }
    }
}
