package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass

enum class Motive(val color: Color) {
    /*
    Wisdom -> Engines + non-removal control + deck manipulation
    Greed -> Pointslam + Engines
    Peace -> Engines + Mana-based setup/payoff + removal control + tutoring
    Vice ->  Pointslam + assassination control + mana disruption
    Rage -> Removal control + pointslam + discard package
    Justice -> Removal control + engines + mana manipulation
     */

    //542
    Neutral("603000"),

    //Earth/Water/Air mana, diverse racial groups
    //Has:
    // Good mana sources
    // High-end gold tutoring
    // Resurrection
    // Protect, Deflect, Stalwart
    //Some:
    // Lock, Removal
    // Bronze tempo
    //Does not have:
    // Tempo-thinning
    // Tall-Punish
    Peace("106010"),

    //ceb31c, b93
    //Light/Dark mana.
    Justice("908030"),

    //243d93, 47a, 243d93, 468
    //Air/Light mana
    // Strengths: Excellent tutoring and various answers
    //Limitations: lacking tempo and direct removal
    Wisdom("4060a0"),

    //a84d2a, 930, 830
    //Identity: Fire mana, dragons and dwarves.
    //Has:
    // Protection, Engines, Combos.
    // Good bronzes
    // Bonded bronzes
    //Some:
    // Deploy Removal (from dragons)
    // Imbibe removal (from dwarves).
    //Does not have:
    // Deflection, Lock, Purify, Poison
    // Tall punish
    // Removal instants.
    // Reliable tutors for high-end cards
    Greed("d08020"),

    //68135e, 630, c62, c30
    //Strengths: Good bronze cards and direct removal
    //Limitations: Limited tutoring and indirect removal.
    //Identity: Fire/Water mana focused, vikings and pirates
    //Has:
    // Removal, removal, and more removal
    // Bronzes: mix of pointslam, engines, and control
    //Some:
    // Utilities, like Purify and Lock
    // Tall punish
    //Does not have:
    // Deflection
    // Tutoring
    Rage("a03000"),

    //Flavor: Chaos/Dark mana focused. Criminals, necromancers, and demons.
    //Control plus greed and engines.
    //Has:
    // Assassinate and Seize
    // Locks, and control.
    //Some:
    // Engines and greed
    //Does not have:
    // Protection or deflection
    // Reliable tutoring
    Vice("444444");

    constructor(color: String) : this(Color.valueOf(color))

    val texture = Textures.initTexture("${name.lowercase()}.png")
    val image = texture.asSprite().setOffset(Compass.CENTER)

    val secondaryColor = Color(color.r + .12f, color.g + .12f, color.b + .12f, .40f)
}
