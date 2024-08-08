package com.example.langbridge.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.langbridge.UserInfo
import com.example.langbridge.contacts.ui.RadioButtonGroup


@Composable
fun ChooseLanguageDialog(onDialogDismiss: (String?) -> Unit = {}) {
    val languages = mapOf(
        "en" to "English",
        "ru" to "Russian",
        "zh" to "Chinese"
    )
    var selectedOption by remember { mutableStateOf(UserInfo.language) }

    Dialog(onDismissRequest = { onDialogDismiss.invoke(selectedOption) }) {
        // Customize your dialog content here
        Surface(
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Settings",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(20.dp)
                )

                RadioButtonGroup(
                    options = languages,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        onDialogDismiss.invoke(selectedOption)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("OK")
                }
            }
        }
    }
}
