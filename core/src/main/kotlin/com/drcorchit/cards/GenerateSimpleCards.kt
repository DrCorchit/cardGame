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
class GenerateSimpleCards : ApplicationAdapter() {
    override fun create() {
        //Load the batch
        Draw.batch
        LocalAssets.getInstance().load()
    }

    override fun render() {
        renderCards()
        assembleHtml()
        Gdx.app.exit()

    }

    fun renderCards() {
        cards.forEach {
            Draw.batch.begin()
            it.drawSimple()
            Draw.batch.end()
            screenshot(it.name)
        }
    }

    private fun rotatePixmap(srcPix: Pixmap): Pixmap {
        val w = srcPix.width
        val h = srcPix.height
        val rotatedPix = Pixmap(h, w, srcPix.format)

        for (x in 0 until h) {
            for (y in 0 until w) {
                rotatedPix.drawPixel(x, y, srcPix.getPixel(y, x))
            }
        }

        srcPix.dispose()
        return rotatedPix
    }

    fun screenshot(cardName: String) {
        val rawPixmap = Pixmap.createFromFrameBuffer(0, 0, W, H)
        val rotatedPixmap = if (rotate90) rotatePixmap(rawPixmap) else rawPixmap
        val name = "output/images/simple/${cardName.normalize()}.png"
        PixmapIO.writePNG(FileHandle(name), rotatedPixmap, Deflater.DEFAULT_COMPRESSION, false)
    }

    fun assembleHtml() {
        val cardHeight = 242
        val cardWidth = round(cardHeight * Card.cardRatio).toInt()
        val space = 1

        try {
            val style = ".flex {" +
                " display: flex; " +
                " flex-direction: rows;" +
                " flex-wrap: wrap;" +
                " gap: ${space}px;" +
                "}\n" +
                "img {" +
                " width: $cardWidth;" +
                " height: $cardHeight;" +
                "}"

            val images = cards
                .joinToString("\n") {
                    val ele =
                        "<img src=\"../images/simple/${it.name.normalize()}.png\" alt=${it.name} />"
                    if (it.rarity == Rarity.Common) "$ele\n$ele" else ele
                }

            val head = "<style>$style</style>"
            val body = "<div class=\"flex\">\n$images\n</div>"
            val html = "<html>\n<head>$head</head>\n<body>$body</body>\n</html>"
            IOUtils.overwriteFile("output/html/output.html", html)
            logger.info("Successfully rendered html")
        } catch (e: Exception) {
            logger.error("Failed to render html", e)
        }

        Gdx.app.net.openURI("http://localhost:63342/CardGame/output/html/output.html")
    }

    override fun dispose() {
        Draw.batch.dispose()
    }

    companion object {
        private val logger = Logger.getLogger(GenerateSimpleCards::class.java)

        //page printable width = 720 px = 7 7/16 inches
        val pageWidthInInches = 7.4375f
        val pageWidthInPx = 720
        val pxToInches = pageWidthInPx / pageWidthInPx

        val rotate90 = true
    }
}
