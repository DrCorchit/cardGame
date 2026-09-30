package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.Rarity
import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils
import com.google.gson.JsonArray
import java.io.File
import java.util.*

class CardDatabase(val factionName: String) :
    HtmlFile(
        "$factionName Faction Cards",
        File(generator.inputDir, "$factionName.html"),
        File(generator.outputDir, "database/$factionName.html")
    ) {

    val cards = JsonUtils.parseFromFile("assets/json/$factionName.json")?.first?.asJsonArray ?: JsonArray()

    override fun appendBody(): HtmlFile {
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
                list.map { json ->
                    val name = json.get("name").asString
                    val normalized = name.normalize()

                    HtmlObject(
                        "img", TreeMap(
                            mapOf(
                                "src" to "/images/cards/$factionName/$normalized.png",
                                "alt" to name,
                                "class" to "databaseImage"
                            )
                        )
                    )
                }
            }.toSortedMap()
            .forEach { rarity, cards ->
                val flex = HtmlObject(
                    "div",
                    mutableMapOf("style" to "display:flex; flex-wrap:wrap; align-content:flex-start;")
                )

                cards.forEach { card ->
                    flex.withContent(card)
                }

                body.withContent(HtmlObject("h4").withContent("$rarity Cards"))
                body.withContent(flex)
            }
        return this
    }
}
