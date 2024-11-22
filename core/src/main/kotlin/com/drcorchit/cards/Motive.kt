package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass

enum class Motive(val color: Color) {
    //Neutral(Color.valueOf("504020")),
    Neutral(Color.valueOf("603000")),

    //Nature and angelic themes.
    //Earth, Water, Air, and Light mana focused
    //Two sub-factions: nature, and angels
    //nature -> earth, water, air, plus fairies/dryads/treants/beasts
    //angels -> angels and human healers, emphasis on resurrection and healing
    Peace(Color.valueOf("106010")),

    //Judgment and warrior themes
    //Fire, Water and Light mana focused
    Justice(Color.valueOf("b09030")),
    //ceb31c

    //Focus on human and angelic scribes
    //Air, Light, and Dark mana focused.
    Wisdom(Color.valueOf("243d93")),

    //Fire focused
    //Greed(Color.valueOf("a84d2a")),
    Greed(Color.valueOf("803000")),

    //Water focused
    Vice(Color.valueOf("68135e")),

    //Dark, and Chaos focused.
    Chaos(Color.valueOf("303030"));

    val texture = Textures.initTexture("${name.lowercase()}2.png")
    val image = texture.asSprite().setOffset(Compass.CENTER)

    val secondaryColor = Color(color.r + .12f, color.g + .12f, color.b + .12f, .40f)
}
