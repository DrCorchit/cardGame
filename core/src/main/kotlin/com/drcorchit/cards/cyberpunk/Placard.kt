package com.drcorchit.cards.cyberpunk

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Drawable
import com.drcorchit.cards.graphics.Textures
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils.normalize

class Placard(val bot: Rococobot, val prefix: String) : Drawable {

    override val name = bot.name

    var image: AnimatedSprite? = null

    override val outputLocation = "output/images/temporary/cyberpunk/placards/${prefix}_${name.normalize()}.png"

    override val multiplicity = 1

    override fun draw() {
//        val mx = Gdx.input.x.toFloat()
//        val my = Gdx.graphics.height - Gdx.input.y.toFloat()

        ScreenUtils.clear(Color.BLACK)
        image?.draw(Draw.batch, BORDER_X + 125f, BORDER_Y + 130f)
        bot.signatureGlow.draw(Draw.batch, sigX, sigY, rotation = sigAngle)
        bot.signature.draw(Draw.batch, sigX, sigY, rotation = sigAngle)
        screen.draw(Draw.batch, BORDER_X + 136f, BORDER_Y + 136f, 1000f, 1523f)
        placard.draw(Draw.batch, BORDER_X, BORDER_Y)

        //Draw.drawText(100f, 100f, Fonts.abilityFont, "$mx, $my")
    }

    override fun updateGraphic(): AnimatedSprite? {
        image = bot.getImage(prefix)
        return image
    }

    companion object {
        val botsMixed = Rococobot.entries.map { Placard(it, "mixed") }
        val botsBallerina = Rococobot.entries.map { Placard(it, "ballerina") }
        val botsConfident = Rococobot.entries.map { Placard(it, "confident") }
        val botsDramatic = Rococobot.entries.map { Placard(it, "dramatic") }
        val botsPretty = Rococobot.entries.map { Placard(it, "pretty") }
        val botsSilly0 = Rococobot.entries.map { Placard(it, "silly") }
        val botsSilly1 = Rococobot.entries.map { Placard(it, "silly1") }
        val botsSilly2 = Rococobot.entries.map { Placard(it, "silly2") }
        val botsSilly3 = Rococobot.entries.map { Placard(it, "silly3") }

        val oneColor = listOf("pretty", "ballerina", "confident", "silly", "silly1", "silly2", "silly3").map {
            Placard(Rococobot.Flamme, it)
        }

        val all =
            botsBallerina + botsConfident + botsDramatic + botsPretty + botsSilly0 + botsSilly1 + botsSilly2 + botsSilly3

        val bots = botsMixed + botsSilly2

        val placard = Textures.placard2.asSprite()
        val screen = Textures.screen2.asSprite()

        const val BORDER_X = 10f
        const val BORDER_Y = 20f
        val sigX = 750f + BORDER_X
        val sigY = 500f + BORDER_Y
        val sigAngle = 20f

        init {
            screen.blend.set(Color(.4f, .6f, .8f, 1f))
            //screen.blend.set(Color(.8f, .4f, .8f, 1f))
        }
    }
}
