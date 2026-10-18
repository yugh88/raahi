package org.raahi.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.raahi.core.designsystem.R
import org.raahi.core.designsystem.speech.tapToHear
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone

val CardShape = RoundedCornerShape(14.dp)

/** Outlined card (`.card`): 14dp corners, 1.5dp line border. Tappable when [onClick] is set. */
@Composable
fun RaahiCard(
    modifier: Modifier = Modifier,
    borderColor: Color = RaahiColors.Line,
    container: Color = RaahiColors.Surface,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val click = if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(CardShape)
            .background(container)
            .border(1.5.dp, borderColor, CardShape)
            .then(click)
            .padding(horizontal = 11.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        content = content,
    )
}

/** Small rounded label (`.pill`). Not interactive. */
@Composable
fun StatusPill(text: String, tone: Tone, modifier: Modifier = Modifier, @DrawableRes icon: Int? = null) {
    val bg = if (tone == Tone.Line) RaahiColors.Surface else tone.container
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .tapToHear(text)
            .padding(horizontal = 9.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (icon != null) Icon(painterResource(icon), null, tint = tone.content, modifier = Modifier.size(14.dp))
        Text(text, style = RaahiTheme.typography.pill, color = tone.content)
    }
}

/** Title + pill on one row, sublabel below: scholarships, apprenticeships, courses (S6). */
@Composable
fun OpportunityCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    pill: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    RaahiCard(modifier = modifier, onClick = onClick) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HearText(title, RaahiTheme.typography.card, Modifier.weight(1f))
            pill?.invoke()
        }
        EnText(subtitle)
    }
}

/** "Offline · last synced …" line on Home: shown plainly, never as an error. */
@Composable
fun OfflineBanner(text: String, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Icon(painterResource(RaahiIcons.WifiOff), null, tint = RaahiColors.Muted, modifier = Modifier.size(14.dp))
        HearText(text, RaahiTheme.typography.body.copy(fontSize = 12.sp, color = RaahiColors.Muted))
    }
}

/** "Source: … · checked …" box under every AI answer and every scheme record. */
@Composable
fun SourceLine(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = RaahiColors.Line,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
                    style = Stroke(1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))),
                )
            }
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        HearText(text, RaahiTheme.typography.en)
    }
}

/** A topic the student got stuck on (S11, S15, T1). Encouraging colours: clay = stuck, leaf = cleared. */
@Composable
fun TrapChip(text: String, modifier: Modifier = Modifier, cleared: Boolean = false) {
    val tone = if (cleared) Tone.Leaf else Tone.Clay
    Row(
        modifier = modifier
            .heightIn(min = 36.dp)
            .clip(CircleShape)
            .background(tone.container)
            .tapToHear(text)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (cleared) Icon(painterResource(RaahiIcons.Check), null, tint = tone.content, modifier = Modifier.size(16.dp))
        Text(text, style = RaahiTheme.typography.card, color = tone.content)
    }
}

/** The four course pricing types (T4). */
enum class PriceType { Free, PerModule, Subscription, PayAfterEarn }

@Composable
fun PricePill(type: PriceType, modifier: Modifier = Modifier, amount: String? = null) {
    val (label, tone) = when (type) {
        PriceType.Free -> stringResource(R.string.price_free) to Tone.Leaf
        PriceType.PerModule -> stringResource(R.string.price_per_module) to Tone.Neel
        PriceType.Subscription -> stringResource(R.string.price_subscription) to Tone.Gold
        PriceType.PayAfterEarn -> stringResource(R.string.price_pay_after_earn) to Tone.Clay
    }
    StatusPill(if (amount != null) "$label · $amount" else label, tone, modifier)
}

/** Round initials avatar (`.av`), used for shared-phone profiles. */
@Composable
fun ProfileAvatar(initials: String, tone: Tone, modifier: Modifier = Modifier, size: Dp = 40.dp, @DrawableRes icon: Int? = null) {
    Box(
        modifier = modifier.size(size).clip(CircleShape).background(tone.container),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            Icon(painterResource(icon), null, tint = tone.content, modifier = Modifier.size(size * 0.55f))
        } else {
            Text(initials, style = RaahiTheme.typography.card, color = tone.content, textAlign = TextAlign.Center)
        }
    }
}

/**
 * Course card (S14): who made it, subject, verified badge, lessons, download size, price.
 * Ratings are shown only when real reviews exist, so there is no rating slot until Phase 6.
 */
@Composable
fun CourseCard(
    title: String,
    author: String,
    initials: String,
    details: String,
    price: PriceType,
    modifier: Modifier = Modifier,
    verified: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    RaahiCard(modifier = modifier, onClick = onClick) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
            ProfileAvatar(initials, Tone.Neel, size = 44.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                HearText(title, RaahiTheme.typography.card)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    EnText(author)
                    if (verified) StatusPill(stringResource(R.string.verified), Tone.Leaf, icon = RaahiIcons.Shield)
                }
                EnText(details)
                Row(Modifier.padding(top = 2.dp)) { PricePill(price) }
            }
        }
    }
}
