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
import java.io.File
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

        val output = File("output/images/full")
        output.deleteRecursively()
    }

    override fun render() {
        renderCards()
        Gdx.app.exit()
    }

    fun renderCards() {
        cards.forEachIndexed { index, card ->
            Draw.batch.begin()
            card.draw()
            Draw.batch.end()
            screenshot(card)

            val percent = (index+1) * 100f / cards.size
            println("%.1f%% complete - %s".format( percent, card.name))
        }
    }

    fun screenshot(card: Card) {
        val pixmap = Pixmap.createFromFrameBuffer(0, 0, W, H)

        fun save(name: String) {
            val file = FileHandle("output/images/full/${card.rarity.name}/$name.png")
            PixmapIO.writePNG(file, pixmap, Deflater.DEFAULT_COMPRESSION, true)
        }

        if (card.rarity == Rarity.Common) {
            save("${card.name}_1")
            save("${card.name}_2")
        } else {
            save(card.name)
        }
    }

    override fun dispose() {
        Draw.batch.dispose()
    }
}
