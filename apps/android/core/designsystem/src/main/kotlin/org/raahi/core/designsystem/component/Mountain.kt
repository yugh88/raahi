package org.raahi.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.raahi.core.designsystem.theme.RaahiColors
import org.raahi.core.designsystem.theme.RaahiTheme

/** done = leaf, current = neel with the climber, locked = muted; summit = real matched outcomes (gold). */
enum class NodeState { Done, Current, Locked, Summit }

data class MountainNode(
    val title: String,
    val subtitle: String?,
    val state: NodeState,
    @DrawableRes val icon: Int,
)

private val RowHeight = 88.dp
private val NodeSize = 48.dp

/** Zig-zag x positions (fraction of width), repeating from the top. */
private val Xs = listOf(0.5f, 0.28f, 0.68f, 0.32f, 0.7f, 0.3f, 0.66f)

/**
 * The career mountain (S9), roadmap.sh-style but vertical: [nodes] are given from the summit
 * (index 0) down to the base. The path between done nodes is solid leaf; the rest is dashed.
 */
@Composable
fun MountainPath(
    nodes: List<MountainNode>,
    onNodeClick: (Int) -> Unit,
    climberLabel: String,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(RowHeight * nodes.size)
            .drawBehind {
                val w = size.width
                val rh = RowHeight.toPx()
                // Mountain silhouette behind the path.
                val hill = Path().apply {
                    moveTo(0f, size.height)
                    lineTo(w * 0.5f, rh * 0.15f)
                    lineTo(w, size.height)
                    close()
                }
                drawPath(hill, RaahiColors.Surface2)
                val centers = nodes.indices.map { Offset(Xs[it % Xs.size] * w, it * rh + rh / 2) }
                for (i in 0 until centers.lastIndex) {
                    val a = centers[i]
                    val b = centers[i + 1]
                    val path = Path().apply {
                        moveTo(a.x, a.y)
                        cubicTo(a.x, (a.y + b.y) / 2, b.x, (a.y + b.y) / 2, b.x, b.y)
                    }
                    // Segment i→i+1 is walked when the lower node (i+1) is done and the upper one reached.
                    val walked = nodes[i + 1].state == NodeState.Done && nodes[i].state in setOf(NodeState.Done, NodeState.Current)
                    drawPath(
                        path,
                        color = if (walked) RaahiColors.Leaf else RaahiColors.Muted.copy(alpha = 0.5f),
                        style = Stroke(
                            width = (if (walked) 4.dp else 2.5.dp).toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = if (walked) null else PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 6.dp.toPx())),
                        ),
                    )
                }
            },
    ) {
        val width = maxWidth
        nodes.forEachIndexed { i, node ->
            val fx = Xs[i % Xs.size]
            val cx = width * fx
            val top = RowHeight * i + (RowHeight - NodeSize) / 2
            NodeCircle(
                node = node,
                modifier = Modifier.offset(x = cx - NodeSize / 2, y = top),
                onClick = { onNodeClick(i) },
            )
            if (node.state == NodeState.Current) {
                Box(
                    Modifier
                        .offset(x = cx + NodeSize / 2 - 14.dp, y = top - 10.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(RaahiColors.DidiOrange),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(RaahiIcons.Climber), climberLabel, tint = RaahiColors.Ink, modifier = Modifier.size(18.dp))
                }
            }
            // Label goes on the wider side of the node.
            val labelOnRight = fx < 0.5f || (fx == 0.5f && i == 0)
            val labelWidth = if (labelOnRight) width - cx - NodeSize / 2 - 10.dp else cx - NodeSize / 2 - 10.dp
            val labelX = if (labelOnRight) cx + NodeSize / 2 + 8.dp else 0.dp
            val summitTop = i == 0 && fx == 0.5f
            Column(
                Modifier
                    .offset(x = if (summitTop) 0.dp else labelX, y = if (summitTop) top + NodeSize + 2.dp else RowHeight * i + 8.dp)
                    .width(if (summitTop) width else labelWidth)
                    .height(if (summitTop) RowHeight - NodeSize - 2.dp else RowHeight - 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = if (summitTop) Alignment.CenterHorizontally else if (labelOnRight) Alignment.Start else Alignment.End,
            ) {
                if (!summitTop) {
                    val color = when (node.state) {
                        NodeState.Done -> RaahiColors.Leaf
                        NodeState.Current -> RaahiColors.Neel
                        NodeState.Locked -> RaahiColors.Muted
                        NodeState.Summit -> RaahiColors.Gold
                    }
                    HearText(
                        node.title,
                        RaahiTheme.typography.card,
                        color = color,
                        textAlign = if (labelOnRight) TextAlign.Start else TextAlign.End,
                    )
                    if (node.subtitle != null) EnText(node.subtitle, textAlign = if (labelOnRight) TextAlign.Start else TextAlign.End)
                }
            }
        }
    }
}

@Composable
private fun NodeCircle(node: MountainNode, modifier: Modifier, onClick: () -> Unit) {
    val (bg, fg) = when (node.state) {
        NodeState.Done -> RaahiColors.Leaf to RaahiColors.OnNeel
        NodeState.Current -> RaahiColors.Neel to RaahiColors.OnNeel
        NodeState.Locked -> RaahiColors.Surface to RaahiColors.Muted
        NodeState.Summit -> RaahiColors.GoldTint to RaahiColors.Gold
    }
    Box(
        modifier
            .size(NodeSize)
            .clip(CircleShape)
            .background(bg)
            .then(
                when (node.state) {
                    NodeState.Locked -> Modifier.border(2.dp, RaahiColors.Line, CircleShape)
                    NodeState.Summit -> Modifier.border(2.dp, RaahiColors.Gold, CircleShape)
                    NodeState.Current -> Modifier.border(3.dp, RaahiColors.NeelTint, CircleShape)
                    NodeState.Done -> Modifier
                },
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val icon = when (node.state) {
            NodeState.Done -> RaahiIcons.Check
            NodeState.Locked -> RaahiIcons.Lock
            else -> node.icon
        }
        Icon(painterResource(icon), node.title, tint = fg, modifier = Modifier.size(24.dp))
    }
}

/** Compact "climber moved up" strip for S13: a short path with the climber one node higher. */
@Composable
fun ClimbStrip(fromLabel: String, toLabel: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Row(
        modifier.fillMaxWidth().clip(TileShape).background(RaahiColors.LeafTint).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        NodeCircle(MountainNode(fromLabel, null, NodeState.Done, RaahiIcons.Check), Modifier, onClick = {})
        Box(Modifier.weight(1f).height(4.dp).clip(CircleShape).background(RaahiColors.Leaf))
        Box {
            NodeCircle(MountainNode(toLabel, null, NodeState.Current, RaahiIcons.Mountain), Modifier, onClick = {})
            Box(
                Modifier.offset(x = 30.dp, y = (-8).dp).size(24.dp).clip(CircleShape).background(RaahiColors.DidiOrange),
                contentAlignment = Alignment.Center,
            ) { Icon(painterResource(RaahiIcons.Climber), null, tint = RaahiColors.Ink, modifier = Modifier.size(16.dp)) }
        }
    }
    androidx.compose.foundation.layout.Row(modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        Text(fromLabel, style = RaahiTheme.typography.en, modifier = Modifier.weight(1f))
        Text(toLabel, style = RaahiTheme.typography.en, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
    }
}
