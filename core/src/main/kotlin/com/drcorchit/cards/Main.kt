package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.justice.utils.logging.Logger
import com.drcorchit.justice.utils.math.MathUtils

/**
 * [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.
 */
class Main : ApplicationAdapter() {
    var index = 36 * 2
    val stage by lazy { Stage() }
    val card by lazy { CardActor(Cards.cards[index]) }

    override fun create() {
        //Load the batch
        Draw.batch
        LocalAssets.getInstance().load()
        stage.addActor(card)
    }


    override fun resize(width: Int, height: Int) {
//        val ratio = ((width / W) + (height / H)) / 2
//        val newW = (W * ratio).toInt()
//        val newH = (H * ratio).toInt()
//
//        stage.viewport.setScreenSize(newW, newH)
//        stage.viewport.update(newW, newH)
//        Draw.resize(newW.toFloat(), newH.toFloat())
        Gdx.app.graphics.setWindowedMode(width, height)

        println("$width x $height")
    }

    override fun render() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            index = MathUtils.modulus(index + 1, Cards.cards.size)
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            index = MathUtils.modulus(index - 1, Cards.cards.size)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            card.card.updateGraphic()
        }

        card.card = Cards.cards[index]

        Draw.batch.begin()
        stage.draw()
        Draw.batch.end()

    }

    override fun dispose() {
        Draw.batch.dispose()
    }

    companion object {
        private val logger = Logger.getLogger(Main::class.java)

        //was 750x1050
        //was 1000x1400
        const val BORDER = 36f
        const val IMAGE_W = 822
        const val IMAGE_H = 1122
        const val W = IMAGE_W - (BORDER * 2)
        const val H = IMAGE_H - (BORDER * 2)
    }
}
