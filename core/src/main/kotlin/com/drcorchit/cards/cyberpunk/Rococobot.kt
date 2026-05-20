package com.drcorchit.cards.cyberpunk

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.LocalAssets
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.math.Compass

enum class Rococobot(val color: Color) {
    Rose(Color.RED),
    Flamme(Color.ORANGE),
    Fuschia(Color.PINK),
    Violet(Color.PURPLE),
    Lotus(Color.WHITE),
    Noir(Color.BLACK),
    Viridia(Color.GREEN),
    Sapphire(Color.BLUE);
//    Spike(Color.RED, true),
//    Turbo(Color.ORANGE, true),
//    Rage(Color.PINK, true),
//    Storm(Color.PURPLE, true),
//    Lightning(Color.WHITE, true),
//    Ash(Color.BLACK, true),
//    Green(Color.GREEN, true),
//    Bolt(Color.BLUE, true);

    //val image = LocalAssets.getInstance().create("$name.png").asSprite()
    //val imageSilly = LocalAssets.getInstance().create("silly_$name.png").asSprite()
    fun getImage(prefix: String): AnimatedSprite {
        return LocalAssets.getInstance().create("${prefix}_$name.png").asSprite()
    }

    val signature = AnimatedSprite(Textures.signatures.getFrames())
        .let {
            it.setOffset(Compass.CENTER)
            it.index = ordinal.toDouble()
            it
        }
    val signatureGlow = AnimatedSprite(Textures.signaturesGlow.getFrames())
        .let {
            it.setOffset(Compass.CENTER)
            it.index = ordinal.toDouble();
            it.blend.set(color)
            it
        }

}
