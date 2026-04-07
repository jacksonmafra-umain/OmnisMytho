package com.umain.omnismytho.presentation.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.domain.model.*
import com.umain.omnismytho.presentation.ui.theme.OmnisMythoTheme

/**
 * Preview wrapper that applies the Omnis Mytho dark theme.
 * Use in all @Preview composables for consistency.
 */
@Composable
fun OmPreviewSurface(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    OmnisMythoTheme(darkTheme = darkTheme) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        ) {
            content()
        }
    }
}

// ── Sample Data ──────────────────────────────────────────────────────────────

object SampleData {

    val mythology = Mythology(
        id = "greek",
        name = "Greek Mythology",
        origin = "Ancient Greece",
        description = "The gods and creatures of Mount Olympus, born from Chaos itself.",
        entityCount = 6,
    )

    val mythologies = listOf(
        mythology,
        Mythology("norse", "Norse Mythology", "Scandinavia", "From the frost of Niflheim.", 5),
        Mythology("egyptian", "Egyptian Mythology", "Ancient Egypt", "The Netjeru rule.", 5),
        Mythology("hindu", "Hindu Mythology", "Indian Subcontinent", "The Trimurti.", 5),
        Mythology("japanese", "Japanese Mythology", "Japan", "Kami dwell in all things.", 4),
        Mythology("christian", "Christian Demonology & Angelology", "Abrahamic Tradition", "The hosts of Heaven.", 5),
    )

    val entity = Entity(
        id = "zeus-001",
        name = "Zeus",
        type = EntityType.GOD,
        title = "King of Olympus",
        description = "Sovereign of the sky and lord of thunder, Zeus cast down his father Kronos to claim dominion over gods and mortals alike. His wrath falls as lightning; his justice, though capricious, shapes the fate of all.",
        appearance = "Towering bearded figure on a marble throne, lightning bolt in hand, eagle perched on shoulder.",
        powers = listOf("Thunderbolts", "Weather control", "Shapeshifting", "Omniscience"),
        symbols = listOf("Lightning bolt", "Eagle", "Oak tree", "Aegis"),
        personality = "Authoritative, passionate, prone to jealousy",
        alignment = Alignment.NEUTRAL,
        mythologyId = "greek",
        imagePrompt = "",
    )

    val entities = listOf(
        entity,
        Entity("athena-002", "Athena", EntityType.GOD, "Goddess of Wisdom", "Born fully armoured from the skull of Zeus.", "", listOf("Strategy", "Wisdom"), listOf("Owl", "Spear"), "Wise, strategic", Alignment.GOOD, "greek", ""),
        Entity("medusa-004", "Medusa", EntityType.CREATURE, "The Gorgon", "Once a maiden of surpassing beauty, cursed by Athena.", "", listOf("Petrification"), listOf("Snakes", "Stone"), "Tragic, wrathful", Alignment.CHAOTIC, "greek", ""),
        Entity("hades-003", "Hades", EntityType.GOD, "Lord of the Underworld", "Ruler of the dead.", "", listOf("Necromancy"), listOf("Cerberus", "Bident"), "Stoic, just", Alignment.NEUTRAL, "greek", ""),
        Entity("lucifer-502", "Lucifer", EntityType.DEMON, "The Morning Star", "Once the most beautiful of all angels.", "", listOf("Temptation"), listOf("Morning star"), "Proud, eloquent", Alignment.EVIL, "greek", ""),
        Entity("kitsune-404", "Kitsune", EntityType.SPIRIT, "The Fox Spirit", "Shapeshifting foxes.", "", listOf("Illusion"), listOf("Nine tails"), "Cunning, playful", Alignment.NEUTRAL, "japanese", ""),
    )

    val entitySummary = EntitySummary(
        id = "zeus-001",
        name = "Zeus",
        type = EntityType.GOD,
        title = "King of Olympus",
        alignment = Alignment.NEUTRAL,
        mythologyId = "greek",
    )
}
