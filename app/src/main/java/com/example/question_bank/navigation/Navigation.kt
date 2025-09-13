package com.example.question_bank.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.question_bank.pages.authentication_page.LoginScreen
import com.example.question_bank.pages.authentication_page.RegisterScreen
import com.example.question_bank.pages.main.CoursePage
import com.example.question_bank.pages.main.HomePage
import com.example.question_bank.pages.main.OrganizationPage
import com.example.question_bank.pages.main.SearchPage
import com.example.question_bank.pages.main.SettingsPage
import com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel.DataViewModel
import com.example.question_bank.repositories.authentication.FirebaseAuth
import com.example.question_bank.repositories.fireStore.FireStoreClient
import com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel.OrganizationViewModel

@Composable
fun NavigationStack() {

    val auth = FirebaseAuth()
    val fireStore = FireStoreClient()
    val user = auth.currentUser()
    val route = remember { mutableStateOf("login?email{email}") }
    if(user != null) {
        route.value = "home"
    }

    val dataModel : DataViewModel = viewModel()
    val organizationViewModel : OrganizationViewModel = viewModel()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = route.value) {
        //Login composable
        composable(route = Screen.Login.route + "?email={email}", arguments = listOf(
            navArgument("email") {
                type = NavType.StringType
                nullable = true
                defaultValue = ""
            }
        )) {it ->
            val email = it.arguments?.getString("email") ?: ""

            //Call Login Screen
            LoginScreen(email , navController = navController)
        }

        //Register composable
        composable(
            route = Screen.Register.route + "?email={email}", arguments = listOf(
            navArgument("email") {
                type = NavType.StringType
                nullable = true
                defaultValue = ""
            }
        )) {backStackEntry ->
            //Call Register Screen
            val email = backStackEntry.arguments?.getString("email") ?: ""
            RegisterScreen(email = email, navController)
        }

        //Home composable
        composable(route = Screen.Home.route) {
            //Call Home Screen
            HomePage(navController = navController, ovm = organizationViewModel)
        }

        //Settings composable
        composable(route = Screen.Settings.route) {
            //Call Settings Screen
            SettingsPage(navController = navController, auth =  auth, dataModel = dataModel)
        }

        //Search composable
        composable(route = Screen.Search.route) {
            //Call Search Screen
            SearchPage(navController = navController)
        }

        //Upload to firebase page
        composable(route = Screen.Organization.route + "/{orgId}", arguments = listOf(
            navArgument("orgId") {
                type = NavType.StringType
                nullable = true
                defaultValue = ""
                }
            )
        ) {bst ->
            val orgId = bst.arguments?.getString("orgId").toString()
            OrganizationPage(
                navController = navController,
                organizationVm = organizationViewModel,
                orgId = orgId
            )
        }

        //Course Page
        composable(route = Screen.Course.route + "/{orgId}/{courseId}", arguments = listOf(
            navArgument(name = "orgId") {
                type = NavType.StringType
                nullable = true
                defaultValue = ""
            },
            navArgument(name = "courseId") {
                type = NavType.StringType
                nullable = true
                defaultValue = ""
            }
        )) {it ->
            val orgId = it.arguments?.getString("orgId") ?: ""
            val courseId = it.arguments?.getString("courseId") ?: ""

            CoursePage(navController = navController, ovm = organizationViewModel, orgId = orgId, courseId =  courseId)
        }
    }
}