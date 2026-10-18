package org.raahi.feature.teacher

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.BackRow
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.EnText
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.OpportunityCard
import org.raahi.core.designsystem.component.PricePill
import org.raahi.core.designsystem.component.PriceType
import org.raahi.core.designsystem.component.RaahiCard
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.component.TileShape
import org.raahi.core.designsystem.component.TrapChip
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.designsystem.R as DsR

@Composable
private fun SectionLabel(text: String) = EnText(text, color = RaahiColors.Neel)

/** T2 Assign a video segment or short test to the children who are stuck. Works offline. */
@Composable
fun AssignScreen(
    topic: String,
    segment: String,
    questions: Int,
    stuckChildren: Int,
    saved: Boolean,
    onSend: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.t2_title), onBack)
        SectionLabel(stringResource(R.string.t2_what))
        TrapChip(topic)
        ChoiceTile(stringResource(R.string.t2_segment, segment), {}, Modifier.fillMaxWidth(), icon = RaahiIcons.Video, tone = Tone.Neel, selected = true)
        ChoiceTile(stringResource(R.string.t2_test, questions), {}, Modifier.fillMaxWidth(), icon = RaahiIcons.Edit, tone = Tone.Neel, selected = true)
        SectionLabel(stringResource(R.string.t2_to_whom))
        ChoiceTile(stringResource(R.string.t2_stuck_children, stuckChildren), {}, Modifier.fillMaxWidth(), icon = RaahiIcons.Users, tone = Tone.Clay, selected = true)
        SectionLabel(stringResource(R.string.t2_when))
        ChoiceTile(stringResource(R.string.t2_today), {}, Modifier.fillMaxWidth(), icon = RaahiIcons.Calendar, tone = Tone.Line, selected = true)
        ChoiceTile(stringResource(R.string.t2_send), onSend, Modifier.fillMaxWidth(), icon = RaahiIcons.Share, tone = Tone.Solid)
        if (saved) HearText(stringResource(DsR.string.saved_will_send), RaahiTheme.typography.caption.copy(color = RaahiColors.Leaf))
    }
}

data class TaggedQuestion(val topic: String, val segment: String)

/** T3 Course studio: record/upload, automatic transcript (online), tag questions to topic + segment. */
@Composable
fun CourseStudioScreen(
    tagged: List<TaggedQuestion>,
    onRecord: () -> Unit,
    onUpload: () -> Unit,
    onTag: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.t3_title), onBack)
        StepLabel(1, stringResource(R.string.t3_record))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            ChoiceTile(stringResource(R.string.t3_record_btn), onRecord, Modifier.weight(1f), icon = RaahiIcons.Mic, tone = Tone.Clay, vertical = true, labelStyle = RaahiTheme.typography.card)
            ChoiceTile(stringResource(R.string.t3_upload_btn), onUpload, Modifier.weight(1f), icon = RaahiIcons.Upload, tone = Tone.Neel, vertical = true, labelStyle = RaahiTheme.typography.card)
        }
        EnText(stringResource(R.string.t3_rules))
        StepLabel(2, stringResource(R.string.t3_transcript))
        Row { StatusPill(stringResource(R.string.t3_needs_network), Tone.Gold, icon = RaahiIcons.WifiOff) }
        StepLabel(3, stringResource(R.string.t3_tag))
        tagged.forEachIndexed { i, q ->
            RaahiCard(onClick = { onTag(i) }) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HearText(stringResource(R.string.t3_question_n, i + 1), RaahiTheme.typography.card, Modifier.weight(1f))
                    TrapChip(q.topic)
                }
                EnText(q.segment)
            }
        }
        ChoiceTile(stringResource(R.string.t3_next_pricing), onNext, Modifier.fillMaxWidth(), icon = RaahiIcons.Chevron, tone = Tone.Solid)
    }
}

@Composable
private fun StepLabel(n: Int, text: String) {
    HearText("$n. $text", RaahiTheme.typography.tile)
}

/** T4 Pricing: free | per_module | subscription | pay_after_earn (18+ only, capped, no interest). */
@Composable
fun PricingScreen(
    selected: PriceType,
    amount: String,
    onSelect: (PriceType) -> Unit,
    onAmount: (String) -> Unit,
    onPublish: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.t4_title), onBack)
        listOf(
            PriceType.Free to R.string.t4_free_desc,
            PriceType.PerModule to R.string.t4_module_desc,
            PriceType.Subscription to R.string.t4_sub_desc,
            PriceType.PayAfterEarn to R.string.t4_pae_desc,
        ).forEach { (type, desc) ->
            RaahiCard(borderColor = if (type == selected) RaahiColors.Neel else RaahiColors.Line, onClick = { onSelect(type) }) {
                Row { PricePill(type) }
                HearText(stringResource(desc), RaahiTheme.typography.body)
            }
        }
        if (selected != PriceType.Free) {
            EnText(stringResource(R.string.t4_amount))
            BasicTextField(
                value = amount,
                onValueChange = { v -> onAmount(v.filter { it.isDigit() }) },
                singleLine = true,
                textStyle = RaahiTheme.typography.tileLarge.copy(color = RaahiColors.Ink),
                cursorBrush = SolidColor(RaahiColors.Neel),
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).border(1.5.dp, RaahiColors.Neel, TileShape).padding(horizontal = 14.dp, vertical = 12.dp),
                decorationBox = { inner ->
                    if (amount.isEmpty()) Text("₹", style = RaahiTheme.typography.tileLarge, color = RaahiColors.Muted)
                    inner()
                },
            )
        }
        HearText(stringResource(R.string.t4_share), RaahiTheme.typography.caption)
        ChoiceTile(stringResource(R.string.t4_publish), onPublish, Modifier.fillMaxWidth(), icon = RaahiIcons.Upload, tone = Tone.Solid)
    }
}

/** T5 Earnings. Starts empty: no amounts are shown until real sales or sponsor fees exist. */
@Composable
fun EarningsScreen(onNewCourse: () -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.t5_title), onBack)
        RaahiCard(container = RaahiColors.Surface2, borderColor = RaahiColors.Surface2) {
            HearText(stringResource(R.string.t5_empty), RaahiTheme.typography.tile)
            EnText(stringResource(R.string.t5_empty_meta))
        }
        val dash = "—"
        listOf(
            stringResource(DsR.string.price_per_module),
            stringResource(DsR.string.price_subscription),
            stringResource(R.string.t5_sponsor),
            stringResource(DsR.string.price_pay_after_earn),
        ).forEach { label ->
            Row(Modifier.fillMaxWidth().heightIn(min = 44.dp), verticalAlignment = Alignment.CenterVertically) {
                HearText(label, RaahiTheme.typography.card, Modifier.weight(1f))
                Text(dash, style = RaahiTheme.typography.card, color = RaahiColors.Muted)
            }
        }
        ChoiceTile(stringResource(R.string.t5_new_course), onNewCourse, Modifier.fillMaxWidth(), icon = RaahiIcons.Plus, tone = Tone.Solid)
    }
}

/** T6 Mixed-grade lesson planner (AI, needs network). Offline: the request waits in the outbox. */
@Composable
fun LessonPlannerScreen(
    classes: List<String>,
    selectedClasses: Set<Int>,
    topic: String,
    minutes: Int,
    online: Boolean,
    onClass: (Int) -> Unit,
    onMake: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.t6_title), onBack)
        EnText(stringResource(R.string.t6_classes), color = RaahiColors.Neel)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            classes.forEachIndexed { i, c ->
                ChoiceTile(c, { onClass(i) }, tone = if (i in selectedClasses) Tone.Neel else Tone.Line, selected = i in selectedClasses, minHeight = 48.dp, labelStyle = RaahiTheme.typography.card)
            }
        }
        EnText(stringResource(R.string.t6_topic), color = RaahiColors.Neel)
        Row { TrapChip(topic, cleared = true) }
        EnText(stringResource(R.string.t6_time), color = RaahiColors.Neel)
        Row { StatusPill(stringResource(R.string.t6_minutes, minutes), Tone.Gold, icon = RaahiIcons.Clock) }
        ChoiceTile(stringResource(R.string.t6_make), onMake, Modifier.fillMaxWidth(), icon = RaahiIcons.Sparkle, tone = Tone.Solid)
        if (!online) {
            RaahiCard(container = RaahiColors.GoldTint, borderColor = RaahiColors.GoldTint) {
                HearText(stringResource(R.string.t6_offline), RaahiTheme.typography.body)
            }
        }
    }
}

/** T7 Recommend a student for an opportunity; sent only with the student's or parent's consent. */
@Composable
fun RecommendScreen(
    student: String,
    opportunity: String,
    opportunityMeta: String,
    reasons: List<String>,
    onSend: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.t7_title), onBack)
        HearText(student, RaahiTheme.typography.title)
        EnText(stringResource(R.string.t7_for), color = RaahiColors.Neel)
        OpportunityCard(opportunity, opportunityMeta, pill = { StatusPill(stringResource(R.string.t7_for), Tone.Leaf) })
        EnText(stringResource(R.string.t7_why), color = RaahiColors.Neel)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { reasons.forEach { TrapChip(it, cleared = true) } }
        Box(Modifier.padding(top = 4.dp)) { HearText(stringResource(R.string.t7_consent), RaahiTheme.typography.caption) }
        ChoiceTile(stringResource(R.string.t7_send), onSend, Modifier.fillMaxWidth(), icon = RaahiIcons.Star, tone = Tone.Solid)
    }
}
