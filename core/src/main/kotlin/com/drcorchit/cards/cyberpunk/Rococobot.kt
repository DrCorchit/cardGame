package com.drcorchit.cards.cyberpunk

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.LocalAssets
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite

enum class Rococobot(val color: Color) {
    Rose(Color.RED),
    Flamme(Color.ORANGE),
    Fuschia(Color.PINK),
    Violet(Color.PURPLE),
    Lotus(Color.WHITE),
    Noir(Color.BLACK),
    Viridia(Color.GREEN),
    Sapphire(Color.BLUE);

    val image = LocalAssets.getInstance().create("$name.png").asSprite()
    //val image80s = LocalAssets.getInstance().create("${name}_80s.png").asSprite()
    val signature = AnimatedSprite(Textures.signatures.getFrames()).let { it.index = ordinal.toDouble(); it }

}
