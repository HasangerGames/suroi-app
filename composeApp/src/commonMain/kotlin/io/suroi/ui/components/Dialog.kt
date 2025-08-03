package io.suroi.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.suroi.ui.theme.DarkGray
import io.suroi.ui.theme.DarkTransparent
import io.suroi.ui.theme.Orange
import io.suroi.ui.theme.White
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import suroi.composeapp.generated.resources.Res
import suroi.composeapp.generated.resources.dialog_alert_title
import suroi.composeapp.generated.resources.dialog_auth_password
import suroi.composeapp.generated.resources.dialog_auth_title
import suroi.composeapp.generated.resources.dialog_auth_username
import suroi.composeapp.generated.resources.dialog_button_cancel
import suroi.composeapp.generated.resources.dialog_button_ok
import suroi.composeapp.generated.resources.dialog_button_reload
import suroi.composeapp.generated.resources.dialog_confirm_title
import suroi.composeapp.generated.resources.dialog_prompt_title
import suroi.composeapp.generated.resources.dialog_unload_default_message
import suroi.composeapp.generated.resources.dialog_unload_title

enum class DialogType {
    Alert,
    Confirm,
    Prompt,
    Unload,
    Auth
}

data class DialogData(
    val type: DialogType = DialogType.Alert,
    val title: String = "",
    val message: String = "",
    val defaultValue: String = "",
    val onConfirm: (String?, String?) -> Unit = { _, _ -> },
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
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                    text = when (data.type) {
                        DialogType.Alert -> stringResource(Res.string.dialog_alert_title)
                        DialogType.Confirm -> stringResource(Res.string.dialog_confirm_title)
                        DialogType.Prompt -> stringResource(Res.string.dialog_prompt_title)
                        DialogType.Unload -> stringResource(Res.string.dialog_unload_title)
                        DialogType.Auth -> stringResource(Res.string.dialog_auth_title)
                    },
                    style = TextStyle(
                        color = White,
                        fontSize = 24.sp,
                    ),
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = data.message.ifEmpty {
                        if (data.type == DialogType.Unload) {
                            stringResource(Res.string.dialog_unload_default_message)
                        } else {
                            ""
                        }
                    },
                    style = TextStyle(
                        color = White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    ),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                if (data.type == DialogType.Prompt) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
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
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                } else if(data.type == DialogType.Auth) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(Res.string.dialog_auth_username),
                            modifier = Modifier.padding(end = 12.dp),
                            color = White,
                            fontWeight = FontWeight.Medium
                        )
                        LoginField(
                            value = username,
                            onValueChange = { username = it },
                            defaultValue = data.defaultValue,
                            isPrivate = false
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(Res.string.dialog_auth_password),
                            modifier = Modifier.padding(end = 12.dp),
                            color = White,
                            fontWeight = FontWeight.Medium
                        )
                        LoginField(
                            value = password,
                            onValueChange = { password = it },
                            defaultValue = data.defaultValue,
                            isPrivate = true
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            data.onConfirm(
                                when (data.type) {
                                    DialogType.Prompt -> promptInput
                                    DialogType.Auth -> username
                                    else -> null
                                },
                                if (data.type == DialogType.Auth) password else null
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, Orange)
                    ) {
                        Text(
                            if (data.type == DialogType.Unload) {
                                stringResource(Res.string.dialog_button_reload)
                            } else {
                                stringResource(Res.string.dialog_button_ok)
                            },
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
                            Text(
                                text = stringResource(Res.string.dialog_button_cancel),
                                style = TextStyle(fontSize = 14.sp)
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    defaultValue: String = "",
    isPrivate: Boolean
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(color = White, fontSize = 16.sp),
        cursorBrush = SolidColor(White),
        visualTransformation = if (isPrivate) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions =  if (isPrivate) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .heightIn(min = 32.dp, max = 128.dp)
            .background(DarkTransparent, RoundedCornerShape(8.dp))
            .border(
                BorderStroke(2.dp, Orange),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        singleLine = true,
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = defaultValue,
                    color = White.copy(alpha = 0.3f),
                    fontSize = 16.sp
                )
            }
            innerTextField()
        }
    )
}