package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass

enum class Motive(val color: Color) {
    //542
    Neutral("603000"),

    //Earth + Water + Air focused, fairies/dryads/treants/beasts, harmony/diversity
    Peace("106010"),

    //Light + Dark focused, Judgement/Legal concepts. Angels and biblical characters
    //ceb31c, b93
    Justice("908030"),

    //Air focused, scribes + elves
    //243d93, 47a, 243d93, 468
    Wisdom("4060a0"),

    //Fire focused, Dragons and dwarves.
    //a84d2a, 930, 830
    Greed("d08020"),

    //Fire + Water focused, viking themes
    //68135e, 630, c62, c30
    Rage("a03000"),

    //Chaos and Dark focused. Criminals, necromancers, and demons.
    Vice("444444");

    constructor(color: String) : this(Color.valueOf(color))

    val texture = Textures.initTexture("${name.lowercase()}.png")
    val image = texture.asSprite().setOffset(Compass.CENTER)

    val secondaryColor = Color(color.r + .12f, color.g + .12f, color.b + .12f, .40f)
}
