package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.drcorchit.cards.Card.Companion.cards
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.justice.utils.IOUtils
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.logging.Logger
import java.util.zip.Deflater
import kotlin.math.round

/**
 * [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.
 */
class GenerateCards : ApplicationAdapter() {
    override fun create() {
        //Load the batch
        Draw.batch
        LocalAssets.getInstance().load()
    }

    override fun render() {
        renderCards()
        Gdx.app.exit()
    }

    fun renderCards() {
        cards.forEach {
            Draw.batch.begin()
            it.draw()
            Draw.batch.end()
            screenshot(it)
        }
    }

    fun screenshot(card: Card) {
        val pixmap = Pixmap.createFromFrameBuffer(0, 0, W, H)
        val name = "output/images/full/${card.rarity.name}/${card.name.normalize()}.png"
        PixmapIO.writePNG(FileHandle(name), pixmap, Deflater.DEFAULT_COMPRESSION, false)
    }

    override fun dispose() {
        Draw.batch.dispose()
    }
}
