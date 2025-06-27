package io.suroi.ui.theme

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
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class DialogType {
    Alert,
    Confirm,
    Prompt,
    Unload
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Dialog(
    type: DialogType = DialogType.Alert,
    title: String = type.toString(),
    message: String,
    defaultValue: String = "",
    onDismissRequest: () -> Unit = {},
    onConfirm: (String?) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var promptInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
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
                    text = title,
                    style = TextStyle(
                        color = White,
                        fontSize = 24.sp,
                    ),
                    modifier = Modifier.padding(bottom = 12.dp).align(Alignment.CenterHorizontally)
                )

                Text(
                    text = message,
                    style = TextStyle(
                        color = White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (type == DialogType.Prompt) {
                    BasicTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        textStyle = TextStyle(color = White, fontSize = 14.sp),
                        cursorBrush = SolidColor(Orange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .heightIn(min = 32.dp, max = 128.dp)
                            .verticalScroll(scrollState)
                            .background(DarkTransparent, RoundedCornerShape(8.dp))
                            .border(
                                BorderStroke(2.dp, Orange),
                                RoundedCornerShape(8.dp)
                            ),
                        singleLine = false,
                        decorationBox = { innerTextField ->
                            if (promptInput.isEmpty()) {
                                Text(
                                    text = defaultValue,
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
                            onConfirm(if (type == DialogType.Prompt) promptInput else null)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, Orange)
                    ) {
                        Text(
                            if (type == DialogType.Unload) "Leave/Reload" else "OK",
                            style = TextStyle(fontSize = 14.sp))
                    }

                    if (type != DialogType.Alert) {
                        Spacer(modifier = Modifier.width(12.dp))
                        TextButton(
                            onClick = onCancel,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Orange,
                            ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.5.dp, Orange)
                        ) {
                            Text("Cancel", style = TextStyle(fontSize = 14.sp))
                        }

                    }
                }
            }
        }
    }
}