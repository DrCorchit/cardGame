package com.drcorchit.cards.fantasy

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass
import kotlin.random.Random

enum class City(override val color: Color, val adj: String) : Faction {
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
    Avalon("106010", "Avalonian"),

    //Air/Light land
    // Strengths: Excellent tutoring and various answers
    //Limitations: lacking tempo and direct removal
    Metropolis("4060a0", "Metropolitan"),

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
    Transylvania("444444", "Transylvanian"),

    //68135e, 630, c62, c30
    //Identity: Fire/Water land focused, vikings and pirates
    //Has:
    // Removal, removal, and more removal
    // Bronzes: mix of pointslam, engines, and control
    //Some:
    // Lock
    //Does not have:
    // Tutoring
    Thalassa("a03000", "Thalassan"),

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
    Vulcania("d07020", "Vulcanian"),

    //Commonly available cards
    Unaffiliated("603000", "Unaffiliated");

    constructor(color: String, adj: String) : this(Color.valueOf(color), adj)

    val texture by lazy { Textures.initTexture("${name.lowercase()}.png") }
    override val image by lazy { texture.asSprite().setOffset(Compass.CENTER) }
    override val secondaryColor = Color(color.r + .12f, color.g + .12f, color.b + .12f, .35f)

    val aiPrompts = mutableListOf<String>()

    fun randomPrompt(): String {
        if (aiPrompts.isEmpty()) return ""
        val index = Random.Default.nextInt(aiPrompts.size)
        return aiPrompts[index]
    }

    companion object {
        init {
            Avalon.aiPrompts.add("The card is for the Avalon faction, which is set in large, quiet, and lonely temperate forest.")
            Metropolis.aiPrompts.add("The card is for the Metropolis faction, with steampunk characters in industrial rooms.")
            Transylvania.aiPrompts.add("The card is for the Transylvania faction, which includes a mix of humans and vampires living in a victorian era city.")
            Thalassa.aiPrompts.add("The card is for the Thalassa faction, which involves sailors and ships on the open seas.")
            Vulcania.aiPrompts.add("The card is for the Vulcania faction, which features deep dark caves.")
        }
    }
}
