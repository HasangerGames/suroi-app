package io.suroi.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.suroi.ui.theme.DarkGray
import io.suroi.ui.theme.DarkTransparent
import io.suroi.ui.theme.Orange
import io.suroi.ui.theme.White
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class DialogType {
    Alert,
    Confirm,
    Prompt,
    Unload
}

data class DialogData(
    val type: DialogType = DialogType.Alert,
    val title: String = "",
    val message: String = "",
    val defaultValue: String = "",
    val onConfirm: (String?) -> Unit = {},
    val onCancel: () -> Unit = {},
    val onDismiss: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Dialog(
    data: DialogData
) {
    var promptInput by remember { mutableStateOf("") }
    val promptScrollState = rememberScrollState()

    BasicAlertDialog(
        onDismissRequest = data.onDismiss,
        modifier = Modifier
            .widthIn(max = 400.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkGray,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = data.title,
                    style = TextStyle(
                        color = White,
                        fontSize = 24.sp,
                    ),
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = data.message,
                    style = TextStyle(
                        color = White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    ),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                if (data.type == DialogType.Prompt) {
                    BasicTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        textStyle = TextStyle(color = White, fontSize = 14.sp),
                        cursorBrush = SolidColor(Orange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .heightIn(min = 32.dp, max = 128.dp)
                            .verticalScroll(promptScrollState)
                            .background(DarkTransparent, RoundedCornerShape(8.dp))
                            .border(
                                BorderStroke(2.dp, Orange),
                                RoundedCornerShape(8.dp)
                            ),
                        singleLine = false,
                        decorationBox = { innerTextField ->
                            if (promptInput.isEmpty()) {
                                Text(
                                    text = data.defaultValue,
                                    color = White.copy(alpha = 0.3f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            data.onConfirm(if (data.type == DialogType.Prompt) promptInput else null)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, Orange)
                    ) {
                        Text(
                            if (data.type == DialogType.Unload) "Reload" else "OK",
                            style = TextStyle(fontSize = 14.sp))
                    }

                    if (data.type != DialogType.Alert) {
                        Spacer(modifier = Modifier.width(12.dp))
                        TextButton(
                            onClick = data.onCancel,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Orange,
                            ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(2.dp, Orange)
                        ) {
                            Text("Cancel", style = TextStyle(fontSize = 14.sp))
                        }

                    }
                }
            }
        }
    }
}