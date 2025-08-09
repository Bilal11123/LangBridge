package com.example.langbridge.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.langbridge.R
import com.example.langbridge.UserInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsVM: SettingsViewModel = viewModel()
) {
    val languages = mapOf(
        "en" to "English",
        "ru" to "Русский",
        "zh" to "中国人",
        "fr" to "Français"
    )
    var selectedLanguage by remember { mutableStateOf(UserInfo.language) }
    var expanded by remember { mutableStateOf(false) }

    val isLoading by settingsVM.isLoading.collectAsStateWithLifecycle()
    val languageResponse by settingsVM.languageChangeResponse.collectAsStateWithLifecycle()
    val nameResponse by settingsVM.nameChangeResponse.collectAsStateWithLifecycle()
    val passwordResponse by settingsVM.passwordChangeResponse.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Observe responses and show Snackbar
    LaunchedEffect(languageResponse, nameResponse, passwordResponse) {
        languageResponse?.let {
            snackbarHostState.showSnackbar(
                message = if (it.status == "Success") "Language changed successfully" else "Language change failed"
            )
        }
        nameResponse?.let {
            snackbarHostState.showSnackbar(it.message)
        }
        passwordResponse?.let {
            snackbarHostState.showSnackbar(it.message)
        }

        settingsVM.resetResponses()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings_header)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ChangeSection(
                    title = stringResource(id = R.string.settings_change_name),
                    placeholder = stringResource(id = R.string.settings_name_placeholder),
                    onSubmit = { settingsVM.changeName(it) }
                )

                ChangeSection(
                    title = stringResource(id = R.string.settings_change_password),
                    placeholder = stringResource(id = R.string.settings_password_placeholder),
                    onSubmit = { settingsVM.changePassword(it) }
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(id = R.string.settings_change_Language), style = MaterialTheme.typography.titleMedium)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = languages[selectedLanguage] ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(id = R.string.settings_language_placeholder)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            languages.forEach { (code, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        selectedLanguage = code
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { settingsVM.changeLanguage(selectedLanguage) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    ) {
                        Text(stringResource(id = R.string.settings_language_button))
                    }
                }

                Spacer(modifier = Modifier.weight(1f)) // Pushes logout to the bottom

                Button(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true } // Clears the entire backstack
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.Logout_button),
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}

@Composable
fun ChangeSection(
    title: String,
    placeholder: String,
    onSubmit: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Button(
            onClick = { onSubmit(input) },
            enabled = input.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(id = R.string.submit_button))
        }
    }
}