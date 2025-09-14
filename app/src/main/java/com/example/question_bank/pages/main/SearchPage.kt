package com.example.question_bank.pages.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.question_bank.navigation.NavItem
import com.example.question_bank.navigation.Screen
import com.example.question_bank.repositories.viewModels.loginViewModel.SearchViewModel
import com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel.Organization
import com.example.question_bank.ui.theme.IconColor
import com.example.question_bank.ui.theme.MainColor
import com.example.question_bank.ui.theme.TextColor

@Composable
fun SearchPage(navController: NavController, searchVm: SearchViewModel) {
    val navItems = listOf(
        NavItem("Home", "home", Icons.Filled.Home),
        NavItem("Search", "search", Icons.Filled.Search),
        NavItem("Settings", "settings", Icons.Filled.Settings)
    )

    var selectedItem by remember { mutableIntStateOf(1) } // search page default

    val query by searchVm.searchQuery.collectAsState()
    val response by searchVm.responseOrganization.collectAsState()

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
            verticalArrangement = Arrangement.Top
        ) {
            SearchTextField(
                query = query,
                onChange = { it ->
                    searchVm.updateQuery(it)
                },
                onPerformSearch = {
                    searchVm.performSearch(query)
                }
            )

            //Lazy Column
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(10.dp)
            ) {
                items(response) { it ->
                    OrganizationCard(it, navController)
                }
            }
        }
    }
}

@Composable
fun SearchTextField(query: String, onChange: (String) -> Unit, onPerformSearch: () -> Unit) {

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = query,
        onValueChange = { it ->
            onChange(it)
        },
        placeholder = {
            Text(
                "Search Organization",
                color = TextColor.copy(alpha = 0.6f)
            )
        },
        shape = RoundedCornerShape(30.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MainColor,
            focusedTextColor = TextColor,
            unfocusedContainerColor = MainColor,

            ),
        trailingIcon = {
            IconButton(onClick = onPerformSearch) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = IconColor
                )
            }
        },
        maxLines = 1
    )

}

@Composable
fun OrganizationCard(org: Organization, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                navController.navigate("organization/${org.id}")
            }
            .padding(10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MainColor
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = org.Name,
                style = MaterialTheme.typography.titleMedium,
                color = TextColor
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}