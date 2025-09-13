package com.example.question_bank.pages.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.example.question_bank.ui.theme.MainColor
import com.example.question_bank.ui.theme.TextColor

@Composable
fun CreateDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(MainColor),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Create New Section", color = TextColor, fontSize = 18.sp)

                TextField(
                    value = name,
                    onValueChange = {it ->
                        name =  it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MainColor, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = { onDismiss() }, colors = ButtonDefaults.elevatedButtonColors(MainColor)) {
                        Text("Cancel", color = TextColor)
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onCreate(name)
                            }
                        },
                        colors = ButtonDefaults.elevatedButtonColors(MainColor)
                    ) {
                        Text("Create", color = TextColor)
                    }
                }
            }
        }
    }
}