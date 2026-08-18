package com.drcorchit.cards.fantasy

import java.io.File

class FantasyCards(path: String, var imageRoot: String = "resources/images/fantasy_cards/cards/ChatGPT/Realistic/") {

    val cards = readFrom(path, this)

    companion object {
        val nameRegex = "(?<name>.*)"
        val powerRegex = "(?<power>\\d+)"
        val costRegex = "(?<cost>\\d+)"
        val armorRegex = "(?<armor>\\d+)a"
        val statsRegex = "($armorRegex)? *($powerRegex\\/)?${costRegex}p"
        val tagsRegex = "(?<tags>.*?)"
        val abilityRegex = "(?<abilities>.*?)"
        val quoteRegex = "(?<quote>.*?)"
        val strategyRegex = "(?<strategy>.*)"

        val regex =
            Regex("$nameRegex: *$statsRegex *\\[$tagsRegex] *\\[$abilityRegex] *\\[$quoteRegex]( *\\[$strategyRegex])?")

        init {
            println(regex.toString())
        }

        @JvmStatic
        fun parse(str: String, cards: FantasyCards): FantasyCard? {
            if (str.isBlank() || str.startsWith("#")) {
                return null
            }

            try {
                val match = regex.matchEntire(str)!!.groups
                val name = match["name"]!!.value
                    .replace("(?<!\\w)\"(?=\\w)".toRegex(), "“")
                    .replace("\"", "”")
                    .replace("(?<!\\w)'(?=\\w)".toRegex(), "‘")
                    .replace("'", "’")
                val armor = match["armor"]?.value?.toInt() ?: 0
                val power = match["power"]?.value?.toInt() ?: 0
                val cost = match["cost"]!!.value.toInt()
                val tags = match["tags"]!!.value.split(",").map { it.trim() }
                val abilities = match["abilities"]!!.value
                    .replace("(?<!\\w)\"(?=\\w)".toRegex(), "“")
                    .replace("\"", "”")
                    .replace("(?<!\\w)'(?=\\w)".toRegex(), "‘")
                    .replace("'", "’")
                    .split(";")
                val quote = match["quote"]!!.value
                    .replace("(?<!\\w)\"(?=\\w)".toRegex(), "“")
                    .replace("\"", "”")
                    .replace("(?<!\\w)'(?=\\w)".toRegex(), "‘")
                    .replace("'", "’")
                val strategyTags = match["strategy"]
                    ?.let { it.value.split(",").map { tag -> tag.trim() } }
                    ?: listOf()

                return FantasyCard(cards, name, power, cost, armor, tags, abilities, quote, strategyTags)
            } catch (e: Exception) {
                println("Error parsing line: $str")
                e.printStackTrace()
                return null
            }
        }

        val baseSet by lazy { FantasyCards("assets/txt/fantasy_cards/base_set") }
        val expac1 by lazy { FantasyCards("assets/txt/fantasy_cards/expac_1") }
        val tokens by lazy { FantasyCards("assets/txt/fantasy_cards/tokens") }

        val baseSet2 by lazy { FantasyCards("assets/txt/fantasy_cards_2/base_set", "resources/images/fantasy_cards_2") }


        @JvmStatic
        fun readFrom(filename: String, cards: FantasyCards): List<FantasyCard> {
            return File(filename).listFiles()!!
                .flatMap { file ->
                    if (file.extension.equals("txt", true)) {
                        file.readLines().mapNotNull { parse(it, cards) }
                    } else listOf()
                }
        }
    }
}
