package org.raahi.feature.earn

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import org.raahi.core.designsystem.component.EnText
import org.raahi.core.designsystem.component.HearText
import org.raahi.core.designsystem.component.RaahiCard
import org.raahi.core.designsystem.component.RaahiIcons
import org.raahi.core.designsystem.component.RaahiScreen
import org.raahi.core.designsystem.component.StatusPill
import org.raahi.core.designsystem.language.enStringResource
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme
import org.raahi.core.designsystem.theme.Tone

/** Only the status is ever stored (plus dates), never a document number. */
enum class DocStatus { Have, Missing, ExpiringSoon }

data class DocumentItem(@StringRes val name: Int, val status: DocStatus, val whereToGet: String?)

data class DocumentsUiState(val opportunity: String, val items: List<DocumentItem>)

/** S18 Documents ready: have / missing / expiring soon, where to get each, expiry reminders. */
@Composable
fun DocumentsScreen(
    state: DocumentsUiState,
    onToggle: (Int) -> Unit,
    onRemind: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RaahiScreen(modifier = modifier) {
        BackRow(stringResource(R.string.s6_documents), onBack)
        EnText(stringResource(R.string.s18_for, state.opportunity))
        state.items.forEachIndexed { i, item ->
            val (pill, tone, icon) = when (item.status) {
                DocStatus.Have -> Triple(R.string.s18_have, Tone.Leaf, RaahiIcons.Yes)
                DocStatus.Missing -> Triple(R.string.s18_missing, Tone.Clay, RaahiIcons.No)
                DocStatus.ExpiringSoon -> Triple(R.string.s18_expiring, Tone.Gold, RaahiIcons.Clock)
            }
            RaahiCard(onClick = { onToggle(i) }, borderColor = if (item.status == DocStatus.Have) RaahiColors.Line else tone.content) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(painterResource(icon), null, tint = tone.content, modifier = Modifier.size(28.dp))
                    Column(Modifier.weight(1f)) {
                        HearText(stringResource(item.name), RaahiTheme.typography.card)
                        EnText(enStringResource(item.name))
                    }
                    StatusPill(stringResource(pill), tone)
                }
                if (item.status != DocStatus.Have && item.whereToGet != null) {
                    HearText(stringResource(R.string.s18_where, item.whereToGet), RaahiTheme.typography.body.copy(color = RaahiColors.Muted))
                }
            }
        }
        HearText(stringResource(R.string.s18_privacy), RaahiTheme.typography.caption)
        ChoiceTile(
            label = stringResource(R.string.s18_remind),
            onClick = onRemind,
            tone = Tone.Neel,
            icon = RaahiIcons.Bell,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun documentsDesignReference(): DocumentsUiState {
    val office = stringResource(R.string.designref_where_office)
    return DocumentsUiState(
        opportunity = stringResource(R.string.designref_s6_sch),
        items = listOf(
            DocumentItem(R.string.doc_aadhaar, DocStatus.Have, null),
            DocumentItem(R.string.doc_income, DocStatus.ExpiringSoon, office),
            DocumentItem(R.string.doc_caste, DocStatus.Missing, office),
            DocumentItem(R.string.doc_domicile, DocStatus.Missing, office),
            DocumentItem(R.string.doc_marksheet, DocStatus.Have, null),
            DocumentItem(R.string.doc_bank, DocStatus.Have, null),
        ),
    )
}
