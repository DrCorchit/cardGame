package com.drcorchit.cards.fantasy

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass

enum class City(override val color: Color): Faction {
    Yeoman("603000"),

    //Earth/Water/Air land, diverse racial groups
    //Has:
    // Good land sources
    // High-end gold tutoring
    // Resurrection
    // Protect, Deflect, Stalwart
    //Some:
    // Lock, Removal
    // Bronze tempo
    //Does not have:
    // Tempo-thinning
    // Tall-Punish
    Avalon("106010"),

    //Air/Light land
    // Strengths: Excellent tutoring and various answers
    //Limitations: lacking tempo and direct removal
    Technopolis("4060a0"),

    //a84d2a, 930, 830
    //Identity: Fire land, dragons and dwarves.
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
    Mordania("d08020"),

    //68135e, 630, c62, c30
    //Strengths: Good bronze cards and direct removal
    //Limitations: Limited tutoring and indirect removal.
    //Identity: Fire/Water land focused, vikings and pirates
    //Has:
    // Removal, removal, and more removal
    // Bronzes: mix of pointslam, engines, and control
    //Some:
    // Utilities, like Purify and Lock
    // Tall punish
    //Does not have:
    // Deflection
    // Tutoring
    Thalassa("a03000"),

    //Flavor: Chaos/Dark land focused. Criminals, necromancers, and demons.
    //Control plus greed and engines.
    //Has:
    // Assassinate and Seize
    // Locks, and control.
    //Some:
    // Engines and greed
    //Does not have:
    // Protection or deflection
    // Reliable tutoring
    Vulcania("444444");

    constructor(color: String) : this(Color.valueOf(color))

    val texture by lazy { Textures.initTexture("${name.lowercase()}.png") }
    override val image by lazy { texture.asSprite().setOffset(Compass.CENTER) }
    override val secondaryColor = Color(color.r + .12f, color.g + .12f, color.b + .12f, .40f)
}

