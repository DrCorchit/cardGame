package com.drcorchit.cards.fantasy

import com.drcorchit.cards.utils.html.HasProperties
import com.drcorchit.cards.utils.html.HtmlObject
import com.drcorchit.cards.utils.html.Renderable
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils
import com.google.gson.JsonObject

class Leader(json: JsonObject) : Renderable, HasProperties {
    val name = json["name"].asString
    val description = json["description"].asString

    val rawDbLink = "/database/unaffiliated.html#${name.normalize()}"
    val rawLoreLink = "/lore/leaders/${name.normalize()}.html"

    val dbLink by lazy {
        val text = "$name Database Entry"
        HtmlObject("a")
            .withAttribute("href", rawDbLink)
            .withContent(text)
    }

    val loreLink by lazy {
        HtmlObject("a")
            .withAttribute("href", rawLoreLink)
            .withContent(name)
    }

    override fun getProperty(property: String): Any? {
        return when (property) {
            "name" -> name
            "description" -> description
            "db_link" -> dbLink
            "lore_link" -> loreLink
            else -> null
        }
    }

    override fun render(): String {
        return HtmlObject("p")
            .withContent(HtmlObject("b").withContent(name))
            .withContent(": $description").render()
    }

    companion object : HasProperties {
        val leaders = JsonUtils.parseFromFile("assets/json/leaders.json")!!
            .first.asJsonArray.map { Leader(it.asJsonObject) }.associateBy { it.name.normalize() }

        override fun getProperty(property: String): Any? {
            return leaders[property]
        }
    }
}
