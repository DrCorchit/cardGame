package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass

enum class Motive(val color: Color) {
    Neutral(Color.valueOf("504020")),

    //Nature and angelic themes.
    //Earth, Water, Air, and Light mana focused
    //Two sub-factions: nature, and angels
    //nature -> earth, water, air, plus fairies/dryads/treants/beasts
    //angels -> angels and human healers, emphasis on resurrection and healing
    Peace(Color.valueOf("106010")),

    //Judgment and warrior themes
    //Fire, Water and Light mana focused
    Justice(Color.valueOf("ceb31c")),

    //Focus on human and angelic scribes
    //Air, Light, and Dark mana focused.
    Wisdom(Color.valueOf("243d93")),

    //Fire focused
    Greed(Color.valueOf("a84d2a")),

    //Water focused
    Vice(Color.valueOf("68135e")),

    //Dark, and Chaos focused.
    Chaos(Color.valueOf("202020"));

    val texture = Textures.initTexture("${name.lowercase()}.png")
    val image = texture.asSprite().setOffset(Compass.CENTER)
}
