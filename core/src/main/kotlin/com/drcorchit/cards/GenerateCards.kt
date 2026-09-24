package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Drawable
import java.io.File
import java.util.zip.Deflater

/**
 * [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.
 */
class GenerateCards : ApplicationAdapter() {
    var index = 0

    val cards by lazy { Main.cards }

    override fun create() {
        //Load the batch
        Draw.batch
        LocalAssets.getInstance().load()

        val output = File("resources/images/temporary")
        output.deleteRecursively()
    }

    override fun render() {
        val card = cards[index]
        Draw.batch.begin()
        card.updateGraphic()
        card.draw()
        Draw.batch.end()
        val count = Main.cardCounts[card] ?: 1
        screenshot(card, count)

        val percent = (index + 1) * 100f / cards.size
        println("%.1f%% complete - %s".format(percent, card.name))
        index++
        if (index >= cards.size) Gdx.app.exit()
    }

    fun screenshot(card: Drawable, count: Int) {
        val pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        for (i in 0..<count) {
            val path = "${card.outputLocation}_$i.png"
            val file = FileHandle(path)
            PixmapIO.writePNG(file, pixmap, Deflater.DEFAULT_COMPRESSION, true)
        }
    }

    override fun dispose() {
        Draw.batch.dispose()
    }
}
