package com.drcorchit.cards.cyberpunk

import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Drawable
import com.drcorchit.justice.utils.StringUtils.normalize

class CyberpunkCard(override val name: String) : Drawable {

    override val outputLocation =
        "output/images/full/cyberpunk/${name.normalize()}.png"

    override fun draw() {
        TODO("Not yet implemented")
    }

    override fun updateGraphic(): AnimatedSprite? {
        TODO("Not yet implemented")
    }
}
