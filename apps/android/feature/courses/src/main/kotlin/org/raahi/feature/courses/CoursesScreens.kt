package org.raahi.feature.courses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.BackRow
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.CourseCard
import org.raahi.core.designsystem.component.DidiBubble
import org.raahi.core.designsystem.component.EnText
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.PricePill
import org.raahi.core.designsystem.component.PriceType
import org.raahi.core.designsystem.component.RaahiCard
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.component.TrapChip
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.designsystem.R as DsR

enum class CourseFilter(val label: Int) {
    MyPath(R.string.s14_filter_career),
    Exams(R.string.s14_filter_exams),
    Language(R.string.s14_filter_language),
}

data class CourseItem(
    val title: String,
    val author: String,
    val initials: String,
    val details: String,
    val price: PriceType,
    val verified: Boolean,
)

data class CoursesUiState(val filter: CourseFilter, val courses: List<CourseItem>, val teacherCourses: List<CourseItem>)

/** S14 Courses: verified sources and teachers. Ratings appear only once real reviews exist. */
@Composable
fun CoursesScreen(
    state: CoursesUiState,
    onFilter: (CourseFilter) -> Unit,
    onCourse: (CourseItem) -> Unit,
    onExamPrep: () -> Unit,
    onPayAfterEarn: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.s14_title), onBack)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            CourseFilter.entries.forEach { f ->
                ChoiceTile(
                    label = stringResource(f.label),
                    onClick = { onFilter(f) },
                    tone = if (f == state.filter) Tone.Neel else Tone.Line,
                    selected = f == state.filter,
                    minHeight = 48.dp,
                    contentPadding = 12.dp,
                    labelStyle = RaahiTheme.typography.card,
                )
            }
        }
        state.courses.forEach { c ->
            CourseCard(c.title, c.author, c.initials, c.details, c.price, verified = c.verified, onClick = { onCourse(c) })
        }
        if (state.teacherCourses.isEmpty()) {
            RaahiCard(container = RaahiColors.Surface2, borderColor = RaahiColors.Surface2) {
                HearText(stringResource(R.string.s14_teacher_empty), RaahiTheme.typography.body.copy(color = RaahiColors.Muted))
            }
        } else {
            state.teacherCourses.forEach { c ->
                CourseCard(c.title, c.author, c.initials, c.details, c.price, verified = c.verified, onClick = { onCourse(c) })
            }
        }
        ChoiceTile(stringResource(R.string.s14_exam_prep), onExamPrep, Modifier.fillMaxWidth(), icon = RaahiIcons.Calendar, tone = Tone.Gold)
        ChoiceTile(stringResource(R.string.s14_what_pay_after), onPayAfterEarn, Modifier.fillMaxWidth(), icon = RaahiIcons.Help, tone = Tone.Clay)
    }
}

data class PlanItem(val trap: String, val action: String, val icon: Int)

data class ExamPlanUiState(
    val exams: List<String>,
    val selectedExam: Int,
    val examDate: String?,
    val today: List<PlanItem>,
    val cleared: Int,
    val totalTraps: Int,
)

/** S15 Exam prep plan (GregMat-style): one low monthly plan per exam; daily plan from traps + date. */
@Composable
fun ExamPlanScreen(
    state: ExamPlanUiState,
    onExam: (Int) -> Unit,
    onItem: (Int) -> Unit,
    onLiveClass: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.s14_exam_prep), onBack)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            state.exams.forEachIndexed { i, e ->
                ChoiceTile(
                    label = e,
                    onClick = { onExam(i) },
                    tone = if (i == state.selectedExam) Tone.Neel else Tone.Line,
                    selected = i == state.selectedExam,
                    minHeight = 48.dp,
                    labelStyle = RaahiTheme.typography.card,
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(painterResource(RaahiIcons.Calendar), null, tint = RaahiColors.Muted, modifier = Modifier.size(18.dp))
            HearText(stringResource(R.string.s15_exam_date, state.examDate ?: stringResource(DsR.string.not_available)), RaahiTheme.typography.body, Modifier.weight(1f))
        }
        HearText(stringResource(R.string.s15_today_plan), RaahiTheme.typography.tile)
        state.today.forEachIndexed { i, item ->
            RaahiCard(onClick = { onItem(i) }) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(painterResource(item.icon), null, tint = RaahiColors.Neel, modifier = Modifier.size(24.dp))
                    Column(Modifier.weight(1f)) {
                        TrapChip(item.trap)
                        HearText(item.action, RaahiTheme.typography.card)
                    }
                }
            }
        }
        StatusPill(stringResource(R.string.s15_progress, state.cleared, state.totalTraps), Tone.Leaf, icon = RaahiIcons.Mountain)
        ChoiceTile(stringResource(R.string.s15_live), onLiveClass, Modifier.fillMaxWidth(), icon = RaahiIcons.Users, tone = Tone.Gold)
        RaahiCard {
            HearText(stringResource(R.string.s15_plan_price), RaahiTheme.typography.card)
            Row { PricePill(PriceType.Subscription) }
        }
    }
}

/** S16 Pay-after-you-earn explainer, read aloud. 18+ only; amounts come from each course. */
@Composable
fun PayAfterEarnScreen(
    sponsor: String?,
    onListenAgain: () -> Unit,
    onParentMessage: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val perCourse = stringResource(R.string.s16_set_with_course)
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(DsR.string.price_pay_after_earn), onBack)
        DidiBubble(stringResource(R.string.s16_explainer), alignTop = true)
        RaahiCard(borderColor = RaahiColors.Clay) {
            HearText(stringResource(R.string.s16_threshold, perCourse), RaahiTheme.typography.card)
            HearText(stringResource(R.string.s16_cap, perCourse), RaahiTheme.typography.card)
            StatusPill(stringResource(R.string.s16_no_penalty), Tone.Leaf, icon = RaahiIcons.Shield)
            if (sponsor != null) EnText(sponsor)
        }
        HearText(stringResource(R.string.s16_age_rule), RaahiTheme.typography.body)
        ChoiceTile(stringResource(DsR.string.listen_again), onListenAgain, Modifier.fillMaxWidth(), icon = RaahiIcons.Speaker, tone = Tone.Neel)
        ChoiceTile(stringResource(R.string.s16_parent_msg), onParentMessage, Modifier.fillMaxWidth(), icon = RaahiIcons.Chat, tone = Tone.Leaf)
        EnText(stringResource(R.string.s16_sandbox))
    }
}

@Composable
fun coursesDesignReference() = CoursesUiState(
    filter = CourseFilter.MyPath,
    courses = listOf(
        CourseItem(
            title = stringResource(R.string.designref_ncert_title),
            author = stringResource(R.string.designref_ncert_author),
            initials = "NC",
            details = stringResource(R.string.designref_ncert_meta),
            price = PriceType.Free,
            verified = true,
        ),
    ),
    teacherCourses = emptyList(),
)

@Composable
fun examPlanDesignReference() = ExamPlanUiState(
    exams = listOf(
        stringResource(R.string.exam_class10),
        stringResource(R.string.exam_iti),
        stringResource(R.string.exam_ssc),
        stringResource(R.string.exam_railways),
        stringResource(R.string.exam_neet_jee),
    ),
    selectedExam = 0,
    examDate = null,
    today = listOf(
        PlanItem(stringResource(R.string.designref_trap_units), stringResource(R.string.s15_watch_min, 2), RaahiIcons.Video),
        PlanItem(stringResource(R.string.designref_trap_percent), stringResource(R.string.s15_practice, 5), RaahiIcons.Edit),
        PlanItem(stringResource(R.string.designref_trap_fractions), stringResource(R.string.s15_recheck), RaahiIcons.Retry),
    ),
    cleared = 1,
    totalTraps = 3,
)
