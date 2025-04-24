package com.drcorchit.cards

object SpaceCards {

    val countRegex = "(?<count>\\d+)x"
    val nameRegex = "(?<name>.*?)"
    val costRegex = "(?<cost>\\d+)k"
    val powerRegex = "(?<power>\\d+)p"
    val abilityRegex = "(?<abilities>.*?)"

    val regex =
        Regex("$countRegex\\s+$nameRegex\\s+$costRegex\\s+($powerRegex\\s+)?\\|\\s+$abilityRegex")

    @JvmStatic
    fun parse(type: SpaceCard.Type, str: String): SpaceCard? {
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
                .trim()
            val cost = match["cost"]!!.value.toInt()
            val power = match["power"]?.value?.toInt() ?: 0
            val abilities = match["abilities"]!!.value
                .replace("(?<!\\w)\"(?=\\w)".toRegex(), "“")
                .replace("\"", "”")
                .replace("(?<!\\w)'(?=\\w)".toRegex(), "‘")
                .replace("'", "’")
                .split(";")
                .map { it.trim() }
            val count = match["count"]!!.value.toInt()

            return SpaceCard2(name, cost, power, type, abilities, count)
        } catch (e: Exception) {
            println("Error parsing line: $str")
            //e.printStackTrace()
            return null
        }
    }

    val cards by lazy {
        SpaceCard.Type.entries.flatMap { type ->
            if (type.file == null) listOf()
            else type.file.readLines().mapNotNull { parse(type, it) }
        }
    }

    init {
        cards.groupBy { it.type }
            .forEach { group, cards ->
                val count = cards.sumOf { it.count }
                val avgPower = cards.sumOf { it.power * it.count } / count.toFloat()
                val avgCost = cards.sumOf { it.cost * it.count } / count.toFloat()
                println("${group.text}: $count Average Power: $avgPower Average Cost: $avgCost")
            }
        val total = cards.sumOf { it.count }
        println("Unique: ${cards.size} Total: $total")


    }
}
