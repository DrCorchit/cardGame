package com.drcorchit.cards.fantasy

import com.drcorchit.cards.utils.html.HtmlObject
import com.drcorchit.cards.utils.html.Renderable
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils
import com.google.gson.JsonObject

class Leader(json: JsonObject): Renderable {
    val name = json["name"].asString
    val description = json["description"].asString

    override fun render(): String {
        return HtmlObject("p")
            .withContent(HtmlObject("b").withContent(name))
            .withContent(": $description").render()
    }

    companion object {
        val leaders = JsonUtils.parseFromFile("assets/json/leaders.json")!!
            .first.asJsonArray.map { Leader(it.asJsonObject) }.associateBy { it.name.normalize() }
    }
}
