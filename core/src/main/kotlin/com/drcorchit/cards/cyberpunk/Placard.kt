package com.drcorchit.cards.cyberpunk

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Drawable
import com.drcorchit.cards.graphics.Fonts
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils.normalize

class Placard(val bot: Rococobot, val isSilly: Boolean): Drawable {

    override val name = bot.name

    val image = if (isSilly) { bot.imageSilly } else bot.image

    override val outputLocation = "output/images/full/cyberpunk/placard/${name.normalize()}.png"

    override fun draw() {
        val mx = Gdx.input.x.toFloat()
        val my = Gdx.graphics.height - Gdx.input.y.toFloat()

        image.draw(Draw.batch, 125f, 130f)
        bot.signature.draw(Draw.batch, 555f, 300f, rotation = 20f)
        screen.draw(Draw.batch, 136f, 136f, 1000f, 1523f)
        placard.draw(Draw.batch, 0f, 0f)

        //Draw.drawText(100f, 100f, Fonts.abilityFont, "$mx, $my")
    }

    override fun updateGraphic(): AnimatedSprite {
        return bot.image
    }

    companion object {
        val bots = Rococobot.entries.map { Placard(it, false) }
        val botsSilly = Rococobot.entries.map { Placard(it, true) }

        val placard = Textures.placard2.asSprite()
        val screen = Textures.screen2.asSprite()

        init {
            screen.blend.set(Color(.4f, .6f, .8f, 1f))
            //screen.blend.set(Color(.8f, .4f, .8f, 1f))
        }
    }
}
