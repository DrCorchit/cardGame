package com.drcorchit.cards

import java.io.File

object Cards2 {

    val countRegex = "(?<count>\\d+)x"
    val nameRegex = "(?<name>.*?)"
    val costRegex = "(?<cost>\\d+)k"
    val powerRegex = "(?<power>\\d+)p"
    val abilityRegex = "(?<abilities>.*?)"

    val regex =
        Regex("$countRegex\\s+$nameRegex\\s+$costRegex\\s+($powerRegex\\s+)?\\|\\s+$abilityRegex")

    @JvmStatic
    fun parse(type: Card2.Type, str: String): Card2? {
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

            return Card2(name, cost, power, type, abilities, count)
        } catch (e: Exception) {
            println("Error parsing line: $str")
            //e.printStackTrace()
            return null
        }
    }

    fun readFrom(filename: String): List<Card2> {
        return File(filename).listFiles()!!
            .flatMap { file ->
                val type = Card2.Type.entries.first {
                    it.file == file
                }
                file.readLines().mapNotNull { parse(type, it) }
            }
    }

    val cards by lazy { readFrom("assets/txt/space_cards") }

    init {
        cards.groupBy { it.type }
            .forEach { entry ->
                val count = entry.value.sumOf { it.count }
                println("${entry.key.name}: $count")
            }
        val total = cards.sumOf { it.count }
        println("Unique: ${cards.size} Total: $total")
    }
}
