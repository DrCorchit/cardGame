package com.drcorchit.cards.cyberpunk

import java.io.File

class CyberpunkCards(path: String) {
    val cards: List<CyberpunkCard> = readFrom(path)

    companion object {
        val nameRegex = "(?<name>.+)"
        val categoryRegex = "(?<category>\\w+)"
        val costRegex = "(?<cost>\\d+)"
        val statsRegex = "(?<rizz>\\d+), (?<chic>\\d+), (?<glam>\\d+), (?<pizzazz>\\d+)"
        val effectRegex = "(?<effect>\\.*)"

        val regex =
            Regex("$nameRegex: $categoryRegex $costRegex\\$ $statsRegex( | $effectRegex)?")

        @JvmStatic
        fun parse(str: String): CyberpunkCard? {
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
                val category = match["category"]!!.value.let { Category.valueOf(it) }
                val cost = match["cost"]!!.value.toInt()
                val rizz = match["rizz"]!!.value.toInt()
                val chic = match["chic"]!!.value.toInt()
                val glam = match["glam"]!!.value.toInt()
                val pizzazz = match["pizzazz"]!!.value.toInt()
                val effect = match["effect"]?.value

                return CyberpunkCard(name, category, cost, rizz, chic, glam, pizzazz, effect)
            } catch (e: Exception) {
                println("Error parsing line: $str")
                e.printStackTrace()
                return null
            }
        }

        val baseSet by lazy { CyberpunkCards("assets/txt/cyberpunk_cards/fashion") }

        @JvmStatic
        fun readFrom(filename: String): List<CyberpunkCard> {
            return File(filename).listFiles()!!
                .flatMap { file ->
                    if (file.extension.equals("txt", true)) {
                        file.readLines().mapNotNull { parse(it) }
                    } else listOf()
                }
        }
    }


}
