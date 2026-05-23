package com.drcorchit.cards.cyberpunk

import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.Main.Companion.BORDER
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Drawable
import com.drcorchit.cards.graphics.Fonts
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.math.Compass

class CyberpunkCredit(override val name: String, val amount: Int, override val multiplicity: Int) : Drawable {

    val combined = "$name — $$amount"

    private var image: AnimatedSprite? = null

    override fun draw() {
        ccBack.draw(Draw.batch, BORDER, BORDER, W, H)

        Draw.drawText(centerX, amountY, Fonts.creditAmountFont3, "$$amount", 1000f, Compass.CENTER, amountColor)
        Draw.drawText(centerX, nameY, Fonts.creditNameFont3, name, 1000f, Compass.CENTER, nameColor)
    }

    override fun updateGraphic(): AnimatedSprite? {
        //NO-OP, graphic is null
        return null
    }


    override val outputLocation =
        "output/images/temporary/cyberpunk/credits/${name.normalize()}.png"

    companion object {
        val credits = listOf(
            CyberpunkCredit("Gift Card", 200, 2),
            CyberpunkCredit("Debt Limit Increase", 300, 2),
            CyberpunkCredit("Payday", 400, 2),
            CyberpunkCredit("Tax Refund", 500, 1),
            CyberpunkCredit("New Credit Card", 700, 1)
        )

        val ccBack = Textures.ccBack.asSprite()

        val centerX = BORDER + W / 2

        val amountY = BORDER + 400f
        val amountColor = Color(1f, 1f, .85f, 1f)
        val nameY = amountY + 250f
        val nameColor = Color(.85f, .85f, .85f, 1f)

    }
}
