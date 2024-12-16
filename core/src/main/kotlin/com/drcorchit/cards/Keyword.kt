package com.drcorchit.cards

import com.drcorchit.justice.utils.json.JsonUtils

data class Keyword(val name: String, val match: String, val description: String) {
    companion object {
        val keywords = JsonUtils.parseFromFile("assets/json/keywords.json")!!
            .first.asJsonArray
            .map { it.asJsonObject }
            .map {
                val name = it["name"].asString
                val match = it["match"]?.asString ?: name
                val description = it["description"].asString
                Keyword(name, match, description) }
            .associateBy { it.match }
    }
}
