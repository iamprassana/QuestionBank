package com.example.question_bank.navigation

sealed class Screen(var route : String) {

    //All different types of screens and their route name
    object Main      : Screen("main")
    object Home      : Screen("home")
    object Login     : Screen("login")
    object Register  : Screen("register")
    object Settings  : Screen("settings")
    object Search : Screen("search")
    object Organization : Screen("organization")
    object Course : Screen("course")
}