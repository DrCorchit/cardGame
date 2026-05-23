package com.drcorchit.cards.cyberpunk

import com.drcorchit.cards.Main.Companion.BORDER
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Drawable
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils.normalize

object CyberpunkCreditBack : Drawable {

    override val name = "Credit Back"

    val ccFront = Textures.ccFront.asSprite()

    override fun draw() {
        ccFront.draw(Draw.batch, BORDER, BORDER, W, H)
    }

    override fun updateGraphic(): AnimatedSprite? {
        //NO-OP, graphic is null
        return null
    }

    override val outputLocation =
        "output/images/temporary/cyberpunk/credits/${name.normalize()}.png"

    override val multiplicity = 1
}
