package com.example.langbridge.messages.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController


@Composable
fun MessageScreen(navController: NavController,id:String?) {
    Scaffold {
        MainContent(it,id)
    }
}

@Composable
fun MainContent(it: PaddingValues,id: String?) {

    Text(text = "$id")
}
