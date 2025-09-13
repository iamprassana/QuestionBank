package com.example.question_bank.pages.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.question_bank.ui.theme.MainColor
import com.example.question_bank.ui.theme.TextColor
import com.example.question_bank.ui.theme.TextFieldColor

@Composable
fun CustomTextField(
    value : String,
    onValueChange : (String) -> Unit,
    label : String,
    placeHolder : String,
    trailingIcon : @Composable (() -> Unit) ?= null,
    isPassWord : Boolean,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextColor) },
        placeholder = { Text(placeHolder) },
        trailingIcon = trailingIcon,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(0.8f),
        shape = RoundedCornerShape(20),
        visualTransformation = if (isPassWord) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedTextColor = TextColor,
            unfocusedContainerColor = MainColor,
            focusedContainerColor = TextFieldColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}