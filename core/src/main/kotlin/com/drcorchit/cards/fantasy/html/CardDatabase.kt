package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.City
import com.drcorchit.cards.fantasy.FantasyCard
import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class CardDatabase(val faction: City) :
    HtmlFile(
        "${faction.adjective} Cards",
        null,
        File(generator.outputDir, "database/${faction.name.normalize()}.html")
    ) {

    val jsonFile = File(generator.inputDir, "database/${faction.name.normalize()}.json")

    init {
        if (!jsonFile.exists()) {
            throw FileNotFoundException("File is missing: $jsonFile")
        }
    }

    val cards = JsonUtils.parseFromFile(jsonFile.path)?.first?.asJsonArray
        ?: throw IllegalArgumentException("File is not json: $jsonFile")

    override fun appendBody(): HtmlFile {
        fun cardToHtml(card: FantasyCard): HtmlObject {
            return HtmlObject(
                "img", TreeMap(
                    mapOf(
                        "src" to "/images/cards/${faction.name}/${card.name.normalize()}.png",
                        "alt" to card.name,
                        "id" to card.name.normalize(),
                        "class" to "databaseImage"
                    )
                )
            )
        }

        body.withContent(HtmlObject("h2").withContent("Hover a card to enlarge."))
        cards.map { it.asJsonObject }
            .map { FantasyCard(it) }
            .filter {
                !it.isToken
            }
            .groupBy {
                it.rarity
            }
            .mapValues { (_, list) ->
                list.sortedBy { it.name }.map { card -> cardToHtml(card) }
            }.toSortedMap()
            .forEach { (rarity, cards) ->
                val flex = HtmlObject("div").withStyle("display:flex; flex-wrap:wrap; justify-content: space-around;")

                cards.forEach { card -> flex.withContent(card) }

                body.withContent(HtmlObject("h4").withContent("$rarity Cards"))
                body.withContent(flex)
            }
        return this
    }
}
