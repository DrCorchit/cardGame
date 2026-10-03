package com.drcorchit.cards.fantasy

import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils
import com.google.gson.JsonObject

class Leader(json: JsonObject) {
    val name = json["name"].asString
    val description = json["description"].asString

    companion object {
        val leaders = JsonUtils.parseFromFile("assets/json/leaders.json")!!
            .first.asJsonArray.map { Leader(it.asJsonObject) }.associateBy { it.name.normalize() }
    }
}
