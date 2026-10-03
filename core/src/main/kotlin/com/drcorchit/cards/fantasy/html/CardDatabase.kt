package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.City
import com.drcorchit.cards.fantasy.Rarity
import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils
import com.google.gson.JsonObject
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class CardDatabase(val faction: City) :
    HtmlFile(
        "${faction.name} Faction Cards",
        File(generator.inputDir, "database/${faction.name}.html"),
        File(generator.outputDir, "database/${faction.name}.html")
    ) {

    val jsonFile = File(generator.inputDir, "database/${faction.name}.json")

    init {
        if (!jsonFile.exists()) {
            throw FileNotFoundException("File is missing: $jsonFile")
        }
    }

    val cards = JsonUtils.parseFromFile(jsonFile.path)?.first?.asJsonArray
        ?: throw IllegalArgumentException("File is not json: $jsonFile")

    override fun appendBody(): HtmlFile {
        fun cardToHtml(card: JsonObject): HtmlObject {
            val name = card.get("name").asString
            val normalized = name.normalize()

            return HtmlObject(
                "img", TreeMap(
                    mapOf(
                        "src" to "/images/cards/${faction.name}/$normalized.png",
                        "alt" to name,
                        "class" to "databaseImage"
                    )
                )
            )
        }

        body.withContent(HtmlObject("h2").withContent("Hover a card to enlarge."))
        cards.map { it.asJsonObject }
            .filter {
                !it.getAsJsonArray("tags")
                    .map { tag -> tag.asString }.contains("Token")
            }
            .groupBy {
                it.getAsJsonArray("tags")
                    .firstNotNullOfOrNull { tag ->
                        try {
                            Rarity.valueOf(tag.asString)
                        } catch (_: Exception) {
                            null
                        }
                    } ?: Rarity.Common
            }
            .mapValues { (_, list) ->
                list.map { json -> cardToHtml(json) }
            }.toSortedMap()
            .forEach { (rarity, cards) ->
                val flex = HtmlObject(
                    "div",
                    mutableMapOf("style" to "display:flex; flex-wrap:wrap; align-content:flex-start;")
                )

                cards.forEach { card -> flex.withContent(card) }

                body.withContent(HtmlObject("h4").withContent("$rarity Cards"))
                body.withContent(flex)
            }
        return this
    }
}
