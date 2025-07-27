package io.suroi.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import suroi.composeapp.generated.resources.Inter
import suroi.composeapp.generated.resources.Res
import suroi.composeapp.generated.resources.RussoOne

@Composable
fun inter() = FontFamily(Font(Res.font.Inter))
@Composable
fun russoOne() = FontFamily(Font(Res.font.RussoOne))
@Composable
fun suroiTypography() = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = inter()),
        displayMedium = displayMedium.copy(fontFamily = inter()),
        displaySmall = displaySmall.copy(fontFamily = inter()),
        headlineLarge = headlineLarge.copy(fontFamily = inter()),
        headlineMedium = headlineMedium.copy(fontFamily = inter()),
        headlineSmall = headlineSmall.copy(fontFamily = inter()),
        titleLarge = titleLarge.copy(fontFamily = inter()),
        titleMedium = titleMedium.copy(fontFamily = inter()),
        titleSmall = titleSmall.copy(fontFamily = inter()),
        bodyLarge = bodyLarge.copy(fontFamily =  inter()),
        bodyMedium = bodyMedium.copy(fontFamily = inter()),
        bodySmall = bodySmall.copy(fontFamily = inter()),
    )
}

