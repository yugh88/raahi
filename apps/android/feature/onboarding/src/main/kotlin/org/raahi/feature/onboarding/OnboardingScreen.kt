package org.raahi.feature.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.DidiBubble
import org.raahi.core.designsystem.component.MicDock
import org.raahi.core.designsystem.component.ProgressDots
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.TileGrid
import org.raahi.core.designsystem.component.TileShape
import org.raahi.core.designsystem.language.enStringResource
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.designsystem.theme.raahiTypography
import org.raahi.core.model.AppLanguage
import org.raahi.core.model.Persona

/** The five onboarding questions, in the order Didi asks them. */
enum class OnboardingStep { Name, Status, Language, Interests, Goal }

enum class Interest(@StringRes val label: Int, @DrawableRes val icon: Int, val tone: Tone) {
    Making(R.string.s2_int_making, RaahiIcons.Tool, Tone.Neel),
    Farming(R.string.s2_int_farming, RaahiIcons.Plant, Tone.Leaf),
    Maths(R.string.s2_int_maths, RaahiIcons.Calc, Tone.Gold),
    People(R.string.s2_int_people, RaahiIcons.Chat, Tone.Clay),
}

enum class Goal(@StringRes val label: Int, @DrawableRes val icon: Int, val tone: Tone) {
    Earn(R.string.s2_goal_earn, RaahiIcons.Coin, Tone.Leaf),
    Study(R.string.s2_goal_study, RaahiIcons.Book, Tone.Neel),
    OwnWork(R.string.s2_goal_business, RaahiIcons.Tool, Tone.Gold),
}

/** The status tiles in screen order (also used to match a spoken answer). */
val StatusOptions = listOf(
    Triple(Persona.Class9To10, R.string.s2_status_9_10, Tone.Neel),
    Triple(Persona.Class11To12, R.string.s2_status_11_12, Tone.Leaf),
    Triple(Persona.GraduateItiDropout, R.string.s2_status_grad, Tone.Gold),
    Triple(Persona.Teacher, R.string.s2_status_teacher, Tone.Clay),
)

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.Name,
    val name: String = "",
    val persona: Persona? = null,
    val language: AppLanguage = AppLanguage.Default,
    val interests: Set<Interest> = emptySet(),
    val goal: Goal? = null,
)

/**
 * S2 Didi onboarding. One question per screen, answered by voice, picture tile or typing.
 * Answers autosave as they are given (Phase 2: into the local profile).
 */
@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onNameChange: (String) -> Unit,
    onPersona: (Persona) -> Unit,
    onLanguage: (AppLanguage) -> Unit,
    onInterest: (Interest) -> Unit,
    onGoal: (Goal) -> Unit,
    onNext: () -> Unit,
    onMic: () -> Unit,
    onType: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (question, en) = when (state.step) {
        OnboardingStep.Name -> stringResource(R.string.s2_q_name) to enStringResource(R.string.s2_q_name)
        OnboardingStep.Status -> stringResource(R.string.s2_q_status) to enStringResource(R.string.s2_q_status)
        OnboardingStep.Language -> stringResource(R.string.s2_q_language) to enStringResource(R.string.s2_q_language)
        OnboardingStep.Interests -> stringResource(R.string.s2_q_interests, state.name) to enStringResource(R.string.s2_q_interests, state.name)
        OnboardingStep.Goal -> stringResource(R.string.s2_q_goal) to enStringResource(R.string.s2_q_goal)
    }
    RaahiScreen(
        modifier = modifier,
        bottom = {
            MicDock(hint = stringResource(org.raahi.core.designsystem.R.string.speak_or_tap), onMic = onMic, onType = onType)
        },
    ) {
        ProgressDots(count = OnboardingStep.entries.size, current = state.step.ordinal)
        DidiBubble(text = question, en = en, alignTop = true)
        when (state.step) {
            OnboardingStep.Name -> NameStep(state.name, onNameChange, onNext)
            OnboardingStep.Status -> TileGrid(StatusOptions) { (persona, label, tone), m ->
                ChoiceTile(
                    label = stringResource(label),
                    en = enStringResource(label),
                    onClick = { onPersona(persona) },
                    tone = tone,
                    selected = state.persona == persona,
                    icon = if (persona == Persona.Teacher) RaahiIcons.Users else RaahiIcons.School,
                    vertical = true,
                    modifier = m,
                )
            }
            OnboardingStep.Language -> TileGrid(AppLanguage.entries) { language, m ->
                ChoiceTile(
                    label = language.nativeName,
                    en = language.englishName,
                    onClick = { onLanguage(language) },
                    tone = if (language == state.language) Tone.Neel else Tone.Line,
                    selected = language == state.language,
                    vertical = true,
                    labelStyle = raahiTypography(language.script).tileLarge,
                    modifier = m,
                )
            }
            OnboardingStep.Interests -> TileGrid(Interest.entries) { interest, m ->
                ChoiceTile(
                    label = stringResource(interest.label),
                    en = enStringResource(interest.label),
                    onClick = { onInterest(interest) },
                    tone = interest.tone,
                    selected = interest in state.interests,
                    icon = interest.icon,
                    vertical = true,
                    modifier = m,
                )
            }
            OnboardingStep.Goal -> Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Goal.entries.forEach { goal ->
                    ChoiceTile(
                        label = stringResource(goal.label),
                        en = enStringResource(goal.label),
                        onClick = { onGoal(goal) },
                        tone = goal.tone,
                        selected = state.goal == goal,
                        icon = goal.icon,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        if (state.step != OnboardingStep.Name) {
            ChoiceTile(
                label = stringResource(R.string.s2_next),
                onClick = onNext,
                tone = Tone.Solid,
                icon = RaahiIcons.Chevron,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun NameStep(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    val style = RaahiTheme.typography.tileLarge
    BasicTextField(
        value = name,
        onValueChange = onNameChange,
        singleLine = true,
        textStyle = style.copy(color = RaahiColors.Ink),
        cursorBrush = SolidColor(RaahiColors.Neel),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .border(1.5.dp, RaahiColors.Neel, TileShape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        decorationBox = { inner ->
            if (name.isEmpty()) Text(stringResource(R.string.s2_name_hint), style = style, color = RaahiColors.Muted)
            inner()
        },
    )
    ChoiceTile(
        label = stringResource(R.string.s2_next),
        onClick = onNext,
        tone = Tone.Solid,
        icon = RaahiIcons.Chevron,
        modifier = Modifier.fillMaxWidth(),
    )
}
