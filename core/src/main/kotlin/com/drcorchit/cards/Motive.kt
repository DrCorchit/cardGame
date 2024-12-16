package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass

enum class Motive(val color: Color) {
    //542
    Neutral("603000"),

    //Strengths: Good combos, engines, and strong mana payoff cards.
    //Limitations: weak bronze tempo, no tall punish
    //Other: Earth/Water/Air focused, diverse racial groups, harmony/diversity
    Peace("106010"),

    //ceb31c, b93
    //Strengths: Excellent control and combos.
    //Limitations: Requires precise mana control.
    //Other: light/dark focused.
    Justice("908030"),

    //Strengths: Excellent tutoring and various answers
    //Limitations: lacking tempo and direct removal
    //Other: Air and light focused. Scribes + Elves
    //243d93, 47a, 243d93, 468
    Wisdom("4060a0"),

    //a84d2a, 930, 830
    //Strengths: Good pointslam, engines, and greed cards.
    //Limitations: weak control and tutoring.
    //Other: relies on fire/earth mana. Dragons and dwarves.
    Greed("d08020"),

    //68135e, 630, c62, c30
    //Strengths: Good bronze cards and direct removal
    //Limitations: Limited tutoring and indirect removal.
    //Flavor: Fire and Water mana focused, vikings and pirates
    Rage("a03000"),

    //Strengths: Bronze pointslam and control, and tall punish. Good combat vs mana
    //Weakness: Limited engines and tutoring
    //Flavor: Chaos/Dark mana focused. Crimials, necromancers, and demons.
    Vice("444444");

    constructor(color: String) : this(Color.valueOf(color))

    val texture = Textures.initTexture("${name.lowercase()}.png")
    val image = texture.asSprite().setOffset(Compass.CENTER)

    val secondaryColor = Color(color.r + .12f, color.g + .12f, color.b + .12f, .40f)
}
