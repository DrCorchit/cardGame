package com.drcorchit.cards.cyberpunk

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.LocalAssets
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite

enum class Rococobot(val color: Color, val isMale: Boolean) {
    Rose(Color.RED, false),
    Flamme(Color.ORANGE, false),
    Fuschia(Color.PINK, false),
    Violet(Color.PURPLE, false),
    Lotus(Color.WHITE, false),
    Noir(Color.BLACK, false),
    Viridia(Color.GREEN, false),
    Sapphire(Color.BLUE, false),
    Spike(Color.RED, true),
    Turbo(Color.ORANGE, true),
    Rage(Color.PINK, true),
    Storm(Color.PURPLE, true),
    Lightning(Color.WHITE, true),
    Ash(Color.BLACK, true),
    Greene(Color.GREEN, true),
    Bolt(Color.BLUE, true);

    val image = LocalAssets.getInstance().create("$name.png").asSprite()
    private val signatureStrip = if (isMale) Textures.signatures else Textures.signatures2
    private val signatureIndex = if (isMale) ordinal - 8.0 else ordinal.toDouble()
    val signature = AnimatedSprite(signatureStrip.getFrames()).let { it.index = signatureIndex; it }

}
