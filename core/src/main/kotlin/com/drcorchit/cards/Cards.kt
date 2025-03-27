package com.drcorchit.cards

import com.drcorchit.cards.Card.Companion.abilityTextW
import com.drcorchit.cards.Card.Companion.totalAbilityTextH
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.justice.utils.StringUtils.normalize
import java.io.File

object Cards {
    val nameRegex = "(?<name>.*)"
    val powerRegex = "(?<power>\\d+)"
    val costRegex = "(?<cost>\\d+)"
    val armorRegex = "(?<armor>\\d+)a"
    val statsRegex = "($armorRegex)? *($powerRegex\\/)?${costRegex}p"
    val tagsRegex = "(?<tags>.*?)"
    val abilityRegex = "(?<abilities>.*?)"
    val quoteRegex = "(?<quote>.*?)"
    val strategyRegex = "(?<strategy>.*?)"

    val regex =
        Regex("$nameRegex: *$statsRegex *\\[$tagsRegex] *\\[$abilityRegex] *\\[$quoteRegex]( *\\[$strategyRegex])?")

    @JvmStatic
    fun parse(str: String): Card? {
        if (str.isBlank() || str.startsWith("#")) {
            return null
        }

        try {
            val match = regex.matchEntire(str)!!.groups
            val name = match["name"]!!.value
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

            return Card(name, power, cost, armor, tags, abilities, quote, strategyTags)
        } catch (e: Exception) {
            println("Error parsing line: $str")
            e.printStackTrace()
            return null
        }
    }

    val factions = setOf(
        Motive.Greed,
        Motive.Justice,
        Motive.Neutral,
        Motive.Peace,
        Motive.Rage,
        Motive.Vice,
        Motive.Wisdom
    ).map { "$it.txt" }

    fun readFrom(filename: String): List<Card> {
        return File(filename).listFiles()!!
            .flatMap { file ->
                if (factions.contains(file.name)) {
                    file.readLines().mapNotNull { parse(it) }
                } else listOf()
            }
    }

    val cards by lazy { readFrom("assets/cards") }


    init {
        val folder =
            File("assets/images/cards/used").listFiles()!!
                .filter { it.isDirectory }
                .flatMap { it.listFiles()!!.asList() }
                .map { it.nameWithoutExtension.normalize() }
                .toMutableSet()
        folder.removeAll(cards.map { it.name.normalize() }.toSet())
        if (folder.isNotEmpty()) {
            //println("Unused card arts {\n  ${folder.joinToString("\n  ")}\n}")
        }

        val cardsByMotive = cards.groupBy { card -> card.motive }
            .mapValues { it.value.groupBy { card -> card.rarity } }

        cardsByMotive.entries
            .forEach { entry ->
                fun count(rarity: Rarity): Int {
                    return entry.value[rarity]?.size ?: 0
                }

                val str = "%-10s %3d %3d %3d".format(
                    entry.key,
                    count(Rarity.Common),
                    count(Rarity.Rare),
                    count(Rarity.Legendary)
                )
                println(str)

            }

        val cardsByRarity = cards.groupBy { it.rarity }
        cardsByRarity.forEach { (rarity, cards) -> println("$rarity ${cards.size}") }
        println("Total cards: ${cards.size}")

        fun factionCount(motive: Motive): Int {
            val cards = cardsByMotive[motive]

            fun rarityCount(rarity: Rarity): Int {
                val count = cards?.get(rarity)?.size ?: 0
                return if (rarity == Rarity.Common) count * 2 else count
            }
            return Rarity.entries.sumOf { rarityCount(it) }
        }

        val count = Motive.entries.sumOf { factionCount(it) }
        println("Total printable cards: $count")

        val tagsCount = cards.flatMap { it.tags }.groupBy { it }.mapValues { it.value.size }
        tagsCount.forEach { (tag, count) -> println("Tag [$tag]: $count") }

        val keywordsCount =
            cards.flatMap { it.keywords }.groupBy { it }.mapValues { it.value.size }
        keywordsCount.forEach { (keyword, count) -> println("Keyword [${keyword.name}]: $count") }

        //print card issues here
        cards.forEach {
            if (it.image == null) {
                println("Card ${it.name} has no art!")
            }
            if (it.quote.isBlank()) {
                println("Card ${it.name} has no quote!")
            }

            val abilityTextH =
                Draw.calculateDimensions(Fonts.abilityFont, it.abilityText, abilityTextW).second
            val keywordTextH =
                Draw.calculateDimensions(Fonts.keywordFont, it.keywordText, abilityTextW).second
            val overlap = totalAbilityTextH - (keywordTextH + abilityTextH)
            if (overlap < 0) {
                println("Card has overlap: ${it.name} $overlap")
            } else if (overlap < 20) {
                println("Card has near overlap: ${it.name} $overlap")
            }
        }
    }
}
