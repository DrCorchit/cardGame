package com.drcorchit.cards.fantasy

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.json.JsonUtils
import com.drcorchit.justice.utils.math.Compass
import com.google.gson.JsonObject
import kotlin.random.Random

class City(json: JsonObject) : Faction {

    val name = json["name"].asString
    val description = json["description"].asString
    override val color = Color.valueOf(json["color"].asString)
    val adjective = json["adjective"].asString

    val texture by lazy { Textures.initTexture("${name.lowercase()}.png") }
    override val image by lazy { texture.asSprite().setOffset(Compass.CENTER) }
    override val secondaryColor = Color(color.r + .12f, color.g + .12f, color.b + .12f, .35f)

    companion object {
        val cities = JsonUtils.parseFromFile("assets/json/factions.json")!!
            .first.asJsonArray.map { City(it.asJsonObject) }.associateBy { it.name }

        val avalon = cities["Avalon"]!!
        val metropolis = cities["Metropolis"]!!
        val thalassa = cities["Thalassa"]!!
        val transylvania = cities["Transylvania"]!!
        val vulcania = cities["Vulcania"]!!
        val unaffiliated = cities["Unaffiliated"]!!
    }
}
