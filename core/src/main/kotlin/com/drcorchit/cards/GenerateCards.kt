package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.drcorchit.cards.Main.Companion.IMAGE_H
import com.drcorchit.cards.Main.Companion.IMAGE_W
import com.drcorchit.cards.graphics.Draw
import java.io.File
import java.util.zip.Deflater

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
        Cards.cards.forEachIndexed { index, card ->
            Draw.batch.begin()
            card.draw()
            Draw.batch.end()
            screenshot(card)

            val percent = (index + 1) * 100f / Cards.cards.size
            println("%.1f%% complete - %s".format(percent, card.name))
        }
    }

    fun screenshot(card: Card) {
        val pixmap = Pixmap.createFromFrameBuffer(0, 0, IMAGE_W, IMAGE_H)

        fun save(name: String) {
            val file = FileHandle("output/images/full/${card.motive.name}/$name.png")
            PixmapIO.writePNG(file, pixmap, Deflater.DEFAULT_COMPRESSION, true)
        }

        if (card.rarity == Rarity.Common) {
            save("${card.name}_1")
            pixmap.drawPixel(0, 0, Color.GRAY.toIntBits())
            save("${card.name}_2")
        } else {
            save(card.name)
        }
    }

    override fun dispose() {
        Draw.batch.dispose()
    }
}
