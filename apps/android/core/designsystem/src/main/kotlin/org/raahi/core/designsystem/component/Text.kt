package org.raahi.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import org.raahi.core.designsystem.speech.tapToHear
import org.raahi.core.designsystem.theme.LocalReadingLevel
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.model.ReadingLevel

/** Text that speaks itself when tapped. Wraps; never ellipsizes. */
@Composable
fun HearText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        style = style,
        color = color,
        textAlign = textAlign,
        modifier = modifier.tapToHear(text),
    )
}

/** English sublabel (`.en`). Hidden at low reading level, where Didi speaks instead. */
@Composable
fun EnText(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified, textAlign: TextAlign? = null) {
    if (LocalReadingLevel.current == ReadingLevel.Low) return
    HearText(text, RaahiTheme.typography.en, modifier, color, textAlign)
}

/** Screen title (`.ttl`) with an optional English sublabel underneath. */
@Composable
fun TitleBlock(title: String, en: String? = null, modifier: Modifier = Modifier) {
    Column(modifier) {
        HearText(title, RaahiTheme.typography.title)
        if (en != null) EnText(en)
    }
}
