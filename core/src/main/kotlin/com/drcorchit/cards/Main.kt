package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.scenes.scene2d.Stage
import com.drcorchit.cards.Card.Companion.cards
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.justice.utils.IOUtils
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.logging.Logger
import com.drcorchit.justice.utils.math.MathUtils
import java.io.File
import java.util.zip.Deflater

/**
 * [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.
 */
class Main : ApplicationAdapter() {
    //27 = Kali
    //52 = Neromir
    //78 = Allmother
    var index = 0
    val stage by lazy { Stage() }
    val card by lazy { CardActor(cards[index]) }

    override fun create() {
        //Load the batch
        Draw.batch
        LocalAssets.getInstance().load()
        stage.addActor(card)
    }

    override fun resize(width: Int, height: Int) {
        val ratio = ((width / W.toFloat()) + (height / H.toFloat())) / 2
        //super.resize(W * ratio, H * ratio)
        val newW = (W * ratio).toInt()
        val newH = (H * ratio).toInt()

        stage.viewport.setScreenSize(newW, newH)
        stage.viewport.update(newW, newH)
        Draw.resize(newW.toFloat(), newH.toFloat())
        Gdx.app.graphics.setWindowedMode(newW, newH)

        println("$newW x $newH")
    }

    override fun render() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            index = MathUtils.modulus(index + 1, cards.size)
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            index = MathUtils.modulus(index - 1, cards.size)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            card.card.updateGraphic()
        }

        card.card = cards[index]

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
        const val W: Int = 1000
        const val H: Int = 1400
    }
}
