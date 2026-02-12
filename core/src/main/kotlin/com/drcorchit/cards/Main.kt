package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.drcorchit.cards.SpaceCard2LargeWindow.Companion.cardbacks
import com.drcorchit.cards.SpaceCard2LargeWindow.Companion.disasters
import com.drcorchit.cards.fantasy.CardType
import com.drcorchit.cards.fantasy.City
import com.drcorchit.cards.fantasy.FantasyCard.Companion.abilityTextW
import com.drcorchit.cards.fantasy.FantasyCard.Companion.keywordHelpW
import com.drcorchit.cards.fantasy.FantasyCard.Companion.totalAbilityTextH
import com.drcorchit.cards.fantasy.FantasyCards
import com.drcorchit.cards.fantasy.Rarity
import com.drcorchit.cards.graphics.CardActor
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Fonts
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.logging.Logger
import com.drcorchit.justice.utils.math.MathUtils
import java.io.File
import kotlin.random.Random

/**
 * [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.
 */
class Main : ApplicationAdapter() {
    var index = 0
    val stage by lazy { Stage() }

    val card by lazy { CardActor(cards[index]) }

    companion object {
        private val logger = Logger.getLogger(Main::class.java)

        //was 750x1050
        //was 1000x1400
        const val BORDER = 36f
        const val IMAGE_W = 822
        const val IMAGE_H = 1122
        const val W = IMAGE_W - (BORDER * 2)
        const val H = IMAGE_H - (BORDER * 2)

        val spaceCards by lazy {
            SpaceCards.cards + disasters + cardbacks
        }

        val fantasyCards by lazy { FantasyCards.baseSet.cards + FantasyCards.expac1.cards }
        val fantasyTokens by lazy { FantasyCards.tokens.cards }

        val cards by lazy { fantasyCards }
    }

    override fun create() {
        //Load the batch
        Draw.batch
        LocalAssets.getInstance().load()
        stage.addActor(card)

        //card sanity checks
        val tagsCount = cards.flatMap { it.tags }.groupBy { it }.mapValues { it.value.size }
        tagsCount.forEach { (tag, count) -> println("Tag [$tag]: $count") }

        val keywordsCount =
            cards.flatMap { it.keywords }.groupBy { it }.mapValues { it.value.size }
        keywordsCount.forEach { (keyword, count) -> println("Keyword [${keyword.name}]: $count") }

        val cardsByCity = cards.groupBy { card -> card.city }
            .mapValues { it.value.groupBy { card -> card.rarity } }

        println("\nCards by city:")
        cardsByCity.entries
            .forEach { entry ->
                fun count(rarity: Rarity): Int {
                    return entry.value[rarity]?.size ?: 0
                }

                val str = " %-12s %3d %3d %3d".format(
                    entry.key,
                    count(Rarity.Common),
                    count(Rarity.Rare),
                    count(Rarity.Legendary)
                )
                println(str)
            }

        println("\nCards by rarity:")
        val cardsByRarity = cards.groupBy { it.rarity }
        cardsByRarity.forEach { (rarity, cards) -> println(" $rarity ${cards.size}") }

        println("\nCards by type:")
        val cardsByType = cards.groupBy { it.type }
        cardsByType.forEach { (type, cards) -> println(" $type ${cards.size}") }

        println("\nToughness count:")
        cardsByType[CardType.Unit]!!
            .groupBy { it.power + it.armor }
            .entries.sortedBy { it.key }
            .forEach {
                val str = "%-2d -> %d".format(it.key, it.value.size)
                println(str)
            }

        println("\nTotal unique cards: ${cards.size}")

        fun factionCount(city: City): Int {
            val cards = cardsByCity[city]

            fun rarityCount(rarity: Rarity): Int {
                val count = cards?.get(rarity)?.size ?: 0
                return if (rarity == Rarity.Common) count * 2 else count
            }
            return Rarity.entries.sumOf { rarityCount(it) }
        }

        val count = City.entries.sumOf { factionCount(it) }
        println("Total printable cards: $count")

        val cardNames = mutableSetOf<String>()
        val cardQuotes = mutableSetOf<String>()

        //print card issues here
        cards.forEach {
            if (!cardNames.add(it.name.normalize())) {
                println("Duplicate Card name: ${it.name}")
            }
            if (it.image == null) {
                println("Card ${it.name} has no art! (checked ${it.name.normalize()}.png and .jpg)")
            }
            if (it.quote.isBlank()) {
                println("Card ${it.name} has no quote!")
            } else if (!cardQuotes.add(it.quote.normalize())) {
                println("Duplicate Card quote: ${it.quote}")
            }

            it.tags.forEach { tag ->
                if (tag.length > 12) {
                    println("Card ${it.name} has a long tag: $tag (${it.tags})")
                }
            }

            val abilityTextH = it.abilityTextHandler.calculateHeight(abilityTextW)

            val keywordTextH =
                Draw.calculateDimensions(
                    Fonts.keywordHelpFont,
                    it.keywordText,
                    keywordHelpW
                ).second
            val overlap = totalAbilityTextH - (keywordTextH + abilityTextH)
            if (overlap < 0) {
                println("Card has overlap: ${it.name} $overlap")
            } else if (overlap < 20) {
                println("Card has near overlap: ${it.name} $overlap")
            }
        }


    }

    override fun resize(width: Int, height: Int) {
        Gdx.app.graphics.setWindowedMode(width, height)

        println("$width x $height")
    }

    override fun render() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            index = MathUtils.modulus(index + 1, cards.size)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            index = MathUtils.modulus(index + 20, cards.size)
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            index = MathUtils.modulus(index - 1, cards.size)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            index = MathUtils.modulus(index - 20, cards.size)
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            card.drawable.updateGraphic()
        }


        card.drawable = cards[index]

        Draw.batch.begin()
        stage.draw()
        Draw.batch.end()

    }

    override fun dispose() {
        Draw.batch.dispose()
    }
}
