package com.drcorchit.cards

import com.drcorchit.justice.utils.json.JsonUtils

data class Keyword(
    val id: Int,
    val name: String,
    val description: String,
    val synonyms: Set<String>
) {
    companion object {
        val regex = Regex("\\b\\w+\\b")

        var count = 0
        val keywordsList = JsonUtils.parseFromFile("assets/json/keywords.json")!!
            .first.asJsonArray
            .map { it.asJsonObject }
            .map { it ->
                val id = count++
                val name = it["name"].asString
                val description = it["description"].asString
                val synonyms = it["synonyms"]?.asJsonArray?.map { it.asString }?.toSet() ?: setOf()
                Keyword(id, name, description, synonyms)
            }

        val keywordsDictionary = mutableMapOf<String, Keyword>()

        init {
            //TODO check for dupe syns?
            keywordsList.forEach {
                keywordsDictionary.putIfAbsent(it.name, it)
                it.synonyms.forEach { syn -> keywordsDictionary.putIfAbsent(syn, it) }
            }
        }
    }
}
