package com.drcorchit.cards.fantasy

import java.io.File

class FantasyCards(path: String) {
    val cards = readFrom(path)

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

        @JvmStatic
        fun parse(str: String): FantasyCard? {
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

                return FantasyCard(name, power, cost, armor, tags, abilities, quote, strategyTags)
            } catch (e: Exception) {
                println("Error parsing line: $str")
                e.printStackTrace()
                return null
            }
        }

        val baseSet by lazy { FantasyCards("assets/txt/fantasy_cards/base_set") }
        val expac1 by lazy { FantasyCards("assets/txt/fantasy_cards/expac_1") }
        val tokens by lazy { FantasyCards("assets/txt/fantasy_cards/tokens") }

        @JvmStatic
        fun readFrom(filename: String): List<FantasyCard> {
            return File(filename).listFiles()!!
                .flatMap { file ->
                    if (file.extension.equals("txt", true)) {
                        file.readLines().mapNotNull { parse(it) }
                    } else listOf()
                }
        }
    }
}
