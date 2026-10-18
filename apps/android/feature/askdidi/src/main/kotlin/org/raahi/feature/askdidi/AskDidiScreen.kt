package org.raahi.feature.askdidi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.component.Bubble
import org.raahi.core.designsystem.component.ChoiceTile
import org.raahi.core.designsystem.component.DidiBubble
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.MicDock
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.SourceLine
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.component.TileGrid
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone
import org.raahi.core.designsystem.R as DsR

data class AskDidiUiState(
    val online: Boolean,
    val question: String,
    val voiceSeconds: Int,
    /** null while waiting; queued offline questions show "saved, will send when network comes". */
    val answer: String?,
    val source: String?,
)

/**
 * S5 Ask Didi: the answer always carries a SOURCE line; Didi speaks it by default.
 * Offline, the question is saved and answered later (never an error screen).
 */
@Composable
fun AskDidiScreen(
    state: AskDidiUiState,
    onListenAgain: () -> Unit,
    onDocuments: () -> Unit,
    onMic: () -> Unit,
    onType: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier, bottom = { MicDock(stringResource(DsR.string.speak_or_tap), onMic, onType) }) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HearText(stringResource(R.string.s5_title), RaahiTheme.typography.title, Modifier.weight(1f))
            StatusPill(
                stringResource(if (state.online) DsR.string.online else DsR.string.offline),
                if (state.online) Tone.Leaf else Tone.Muted,
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Bubble(
                text = state.question,
                en = stringResource(R.string.s5_voice_meta, state.voiceSeconds),
                tone = Tone.Solid,
                modifier = Modifier.fillMaxWidth(0.86f),
            )
        }
        if (state.answer != null) {
            DidiBubble(text = state.answer, didiWidth = 36.dp, alignTop = true)
            if (state.source != null) SourceLine(state.source)
            TileGrid(listOf(0, 1)) { i, m ->
                if (i == 0) {
                    ChoiceTile(stringResource(DsR.string.listen_again), onListenAgain, m, icon = RaahiIcons.Speaker, tone = Tone.Neel, iconSize = 20.dp, labelStyle = RaahiTheme.typography.card)
                } else {
                    ChoiceTile(stringResource(R.string.s5_doc_list), onDocuments, m, icon = RaahiIcons.List, tone = Tone.Leaf, iconSize = 20.dp, labelStyle = RaahiTheme.typography.card)
                }
            }
        } else {
            DidiBubble(text = stringResource(DsR.string.saved_will_send), didiWidth = 36.dp, alignTop = true)
        }
    }
}

/** Phase 1 design reference: the conversation in docs/ui-reference.html S5. */
@Composable
fun askDidiDesignReference(online: Boolean = true) = AskDidiUiState(
    online = online,
    question = stringResource(R.string.designref_s5_question),
    voiceSeconds = 4,
    answer = if (online) stringResource(R.string.designref_s5_answer) else null,
    source = stringResource(R.string.designref_s5_source),
)
