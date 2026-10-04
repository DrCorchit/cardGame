package com.drcorchit.cards.fantasy

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.cards.utils.html.HasProperties
import com.drcorchit.cards.utils.html.HtmlObject
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils
import com.drcorchit.justice.utils.math.Compass
import com.google.gson.JsonObject
import java.io.File
import kotlin.random.Random

class City(json: JsonObject) : Faction, HasProperties {

    val name = json["name"].asString
    val description = json["description"].asString
    override val color = Color.valueOf(json["color"].asString)
    val adjective = json["adjective"].asString

    val texture by lazy { Textures.initTexture("${name.lowercase()}.png") }
    override val image by lazy { texture.asSprite().setOffset(Compass.CENTER) }
    override val secondaryColor = Color(color.r + .12f, color.g + .12f, color.b + .12f, .35f)

    val rawDbLink = "/database/${name.normalize()}.html"
    val rawLoreLink = "/lore/factions/${name.normalize()}.html"

    val dbLink by lazy {
        val text = "$name Card Database"
        HtmlObject("a")
            .withAttribute("href", rawDbLink)
            .withContent(text)
    }

    val loreLink by lazy {
        val text = "A Thousand Years of $name"
        HtmlObject("a")
            .withAttribute("href", rawLoreLink)
            .withContent(text)
    }

    override fun getProperty(property: String): Any? {
        return when (property) {
            "name" -> name
            "description" -> description
            "adjective" -> adjective
            "db_link" -> dbLink
            "lore_link" -> loreLink
            else -> null
        }
    }

    companion object : HasProperties {
        fun findCity(tags: List<String>): City {
            return tags.firstNotNullOfOrNull { cities[it.normalize()] } ?: unaffiliated
        }

        val cities = JsonUtils.parseFromFile("assets/json/factions.json")!!
            .first.asJsonArray.map { City(it.asJsonObject) }.associateBy { it.name.normalize() }

        val avalon = cities["avalon"]!!
        val metropolis = cities["metropolis"]!!
        val thalassa = cities["thalassa"]!!
        val transylvania = cities["transylvania"]!!
        val vulcania = cities["vulcania"]!!
        val unaffiliated = cities["unaffiliated"]!!

        override fun getProperty(property: String): Any? {
            return cities[property]
        }
    }
}
