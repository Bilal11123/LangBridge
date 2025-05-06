package com.example.langbridge.admin_dashboard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.langbridge.admin_dashboard.data.models.AdminUser
import androidx.compose.foundation.shape.RoundedCornerShape


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AdminDashboardScreen(
    navController: NavHostController,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val users by viewModel.userList
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isLoading.value,
        onRefresh = { viewModel.fetchUserInteractions() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Admin Dashboard", style = MaterialTheme.typography.titleLarge)
                },
                actions = {
                    TextButton(
                        onClick = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {
                        Text("Logout", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(users) { user ->
                    AdminUserItem(user)
                }
            }

            PullRefreshIndicator(
                refreshing = viewModel.isLoading.value,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                scale = true
            )
        }
    }
}

@Composable
fun AdminUserItem(user: AdminUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "User: ${user.userName}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("User ID: ${user.userId}", style = MaterialTheme.typography.bodySmall)
            Text("Email: ${user.email}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            if (user.contacts.isEmpty()) {
                Text(
                    "No contacts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    "Contacts:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                user.contacts.forEach { contact ->
                    Text(
                        "- ${contact.contactName} (ID: ${contact.contactId}, Email: ${contact.contactEmail})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

