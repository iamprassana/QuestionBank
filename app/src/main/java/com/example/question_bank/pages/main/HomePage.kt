package com.example.question_bank.pages.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.question_bank.navigation.NavItem
import com.example.question_bank.pages.utils.CreateDialog
import com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel.OrganizationViewModel
import com.example.question_bank.ui.theme.BoxColor
import com.example.question_bank.ui.theme.IconColor
import com.example.question_bank.ui.theme.MainColor
import com.example.question_bank.ui.theme.TextColor
import kotlinx.coroutines.launch


@Composable
fun HomePage(navController: NavController, ovm: OrganizationViewModel) {

    val navItems = listOf(
        NavItem("Home", "home", Icons.Filled.Home),
        NavItem("Search", "search", Icons.Filled.Search),
        NavItem("Settings", "settings", Icons.Filled.Settings)
    )
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        ovm.loadAllOrganization()
    }

    val organizations by ovm.organization.collectAsState()

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
                            indicatorColor = BoxColor
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog.value = true
                },
                containerColor = MainColor
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add an organization",
                    tint = IconColor
                )
            }
        }

    ) { padding ->
        LazyVerticalGrid(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            columns = GridCells.Adaptive(140.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(organizations) { org ->
                CardDecoration(org.Name, {
                    //Toast.makeText(context, "${org.Name} Clicked", Toast.LENGTH_SHORT).show()
                    navController.navigate("organization/${org.id}")
                })
            }
        }

        if (showDialog.value) {
            CreateDialog(
                onDismiss = {
                    showDialog.value = false
                },
                onCreate = {orgName ->
                    scope.launch {
                        val success = ovm.createOrganization(orgName)

                        if(success != null) {
                            Toast.makeText(context, "Successfully Created $orgName", Toast.LENGTH_SHORT).show()
                            showDialog.value = false
                            ovm.loadAllOrganization() //Refresh the page
                        }else {
                            Toast.makeText(context, "Something Went Wrong...", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }

}

@Composable
fun CardDecoration(organization: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(140.dp)
            .width(140.dp),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MainColor,
            contentColor = TextColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Organization",
                tint = IconColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = organization,
                color = TextColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

