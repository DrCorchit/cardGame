package com.drcorchit.cards.cyberpunk

import com.badlogic.gdx.Gdx
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Drawable
import com.drcorchit.cards.graphics.Fonts
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils.normalize

class Placard(val bot: Rococobot): Drawable {

    override val name = bot.name

    override val outputLocation = "output/images/full/cyberpunk/placard/${name.normalize()}.png"

    override fun draw() {
        val mx = Gdx.input.x.toFloat()
        val my = Gdx.graphics.height - Gdx.input.y.toFloat()

        bot.image.draw(Draw.batch, 270f, 130f)
        Draw.batch.draw(bot.signature, 300f, 300f)
        placard.draw(Draw.batch, 0f, 0f)

        //Draw.drawText(100f, 100f, Fonts.numberFont, "$mx, $my")
    }

    override fun updateGraphic(): AnimatedSprite {
        return bot.image
    }

    companion object {
        val placard = Textures.placard.asSprite()

        val bots = Rococobot.entries.map { Placard(it) }

    }
}
