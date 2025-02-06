package com.globallogic.rdkb.remotemanagement.view.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import rdkbremotemanagement.app.generated.resources.Res
import rdkbremotemanagement.app.generated.resources.RobotoMono_Bold
import rdkbremotemanagement.app.generated.resources.RobotoMono_BoldItalic
import rdkbremotemanagement.app.generated.resources.RobotoMono_ExtraLight
import rdkbremotemanagement.app.generated.resources.RobotoMono_ExtraLightItalic
import rdkbremotemanagement.app.generated.resources.RobotoMono_Italic
import rdkbremotemanagement.app.generated.resources.RobotoMono_Light
import rdkbremotemanagement.app.generated.resources.RobotoMono_LightItalic
import rdkbremotemanagement.app.generated.resources.RobotoMono_Medium
import rdkbremotemanagement.app.generated.resources.RobotoMono_MediumItalic
import rdkbremotemanagement.app.generated.resources.RobotoMono_Regular
import rdkbremotemanagement.app.generated.resources.RobotoMono_SemiBold
import rdkbremotemanagement.app.generated.resources.RobotoMono_SemiBoldItalic
import rdkbremotemanagement.app.generated.resources.RobotoMono_Thin
import rdkbremotemanagement.app.generated.resources.RobotoMono_ThinItalic
import rdkbremotemanagement.app.generated.resources.UbuntuMono_Bold
import rdkbremotemanagement.app.generated.resources.UbuntuMono_BoldItalic
import rdkbremotemanagement.app.generated.resources.UbuntuMono_Italic
import rdkbremotemanagement.app.generated.resources.UbuntuMono_Regular

val FontFamily.Companion.UbuntuMono: FontFamily
    @Composable get() = FontFamily(
    Font(Res.font.UbuntuMono_Bold, weight = FontWeight.Bold),
    Font(Res.font.UbuntuMono_BoldItalic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(Res.font.UbuntuMono_Italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(Res.font.UbuntuMono_Regular, weight = FontWeight.Normal),
)
val FontFamily.Companion.RobotoMono: FontFamily
    @Composable get() = FontFamily(
    Font(Res.font.RobotoMono_Bold, weight = FontWeight.Bold),
    Font(Res.font.RobotoMono_BoldItalic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(Res.font.RobotoMono_ExtraLight, weight = FontWeight.ExtraLight),
    Font(Res.font.RobotoMono_ExtraLightItalic, weight = FontWeight.ExtraLight, style = FontStyle.Italic),
    Font(Res.font.RobotoMono_Italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(Res.font.RobotoMono_Light, weight = FontWeight.Light),
    Font(Res.font.RobotoMono_LightItalic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(Res.font.RobotoMono_Medium, weight = FontWeight.Medium),
    Font(Res.font.RobotoMono_MediumItalic, weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(Res.font.RobotoMono_Regular, weight = FontWeight.Normal),
    Font(Res.font.RobotoMono_SemiBold, weight = FontWeight.SemiBold),
    Font(Res.font.RobotoMono_SemiBoldItalic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
    Font(Res.font.RobotoMono_Thin, weight = FontWeight.Thin),
    Font(Res.font.RobotoMono_ThinItalic, weight = FontWeight.Thin, style = FontStyle.Italic),
)
