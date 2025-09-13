package com.example.question_bank.pages.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.question_bank.ui.theme.BoxColor
import com.example.question_bank.ui.theme.IconColor
import com.example.question_bank.ui.theme.TextColor

@Composable
fun ButtonField(buttonName : String, onClick: () -> Unit, icon : ImageVector, iconDescription : String) {

    ElevatedButton(onClick = onClick, colors = ButtonDefaults.elevatedButtonColors(
        containerColor = BoxColor,
        contentColor = TextColor
    )) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(buttonName, color = TextColor)
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = IconColor
            )
        }
    }

}