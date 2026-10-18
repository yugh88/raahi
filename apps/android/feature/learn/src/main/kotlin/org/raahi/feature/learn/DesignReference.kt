package org.raahi.feature.learn

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.TranscriptLine

/*
 * Phase 1 design-reference states. S4 copies docs/ui-reference.html; S10–S13 use textbook facts
 * and the trap names from the master prompt. Phase 2 replaces all of this with the Class 10
 * Electricity content pack (real tagged questions, real transcript, real segment times).
 */

@Composable
fun lessonDesignReference(picked: Int? = 1) = LessonQuestionUiState(
    header = stringResource(R.string.designref_s4_topic),
    picture = RaahiIcons.Bulb,
    question = stringResource(R.string.designref_s4_question),
    options = listOf(
        stringResource(R.string.designref_s4_opt1),
        stringResource(R.string.designref_s4_opt2),
        stringResource(R.string.designref_s4_opt3),
    ),
    picked = picked,
    correct = 1,
    reward = 10,
    queuedDoubts = 1,
)

@Composable
fun trapTestDesignReference(picked: Int? = null) = TrapTestUiState(
    topic = stringResource(R.string.designref_topic_electricity),
    index = 1,
    total = 6,
    picture = RaahiIcons.Bulb,
    question = stringResource(R.string.designref_s10_question),
    options = listOf(
        stringResource(R.string.designref_s10_opt1),
        stringResource(R.string.designref_s10_opt2),
        stringResource(R.string.designref_s10_opt3),
    ),
    picked = picked,
)

@Composable
fun trapsDesignReference() = listOf(
    stringResource(R.string.designref_trap_fractions),
    stringResource(R.string.designref_trap_units),
    stringResource(R.string.designref_trap_percent),
)

@Composable
fun videoDesignReference(currentLine: Int = 1, speed: Float = 1f) = VideoSegmentUiState(
    trap = stringResource(R.string.designref_trap_units),
    segmentLabel = stringResource(R.string.designref_s12_segment),
    segmentStart = 0.30f,
    segmentEnd = 0.52f,
    played = 0.38f,
    speed = speed,
    lines = listOf(
        TranscriptLine("02:10", stringResource(R.string.designref_s12_line1)),
        TranscriptLine("02:31", stringResource(R.string.designref_s12_line2)),
        TranscriptLine("02:58", stringResource(R.string.designref_s12_line3)),
        TranscriptLine("03:20", stringResource(R.string.designref_s12_line4)),
    ),
    currentLine = currentLine,
)

@Composable
fun trapClearedDesignReference() = TrapClearedUiState(
    trap = stringResource(R.string.designref_trap_units),
    badge = stringResource(R.string.designref_badge_electricity),
    fromNode = stringResource(R.string.designref_topic_electricity),
    toNode = stringResource(R.string.designref_next_node),
    recheckDays = 3,
)
