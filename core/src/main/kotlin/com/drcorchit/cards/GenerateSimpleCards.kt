package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.drcorchit.cards.Card.Companion.cards
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.justice.utils.IOUtils
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.logging.Logger
import java.util.zip.Deflater

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

    fun screenshot(cardName: String) {
        val pixmap = Pixmap.createFromFrameBuffer(0, 0, W, H)
        val name = "output/images/${cardName.normalize()}.png"
        PixmapIO.writePNG(FileHandle(name), pixmap, Deflater.DEFAULT_COMPRESSION, true)
    }

    fun assembleHtml() {
        //page printable width = 720 px = 7 7/16 inches
        val space = 2
        val cardWidth = 242
        val cardHeight = cardWidth * Card.cardRatio

        try {
            val style = ".grid { " +
                "display: flex; " +
                "flex-direction: rows;" +
                "flex-wrap: wrap;" +
                "gap: ${space}px; } "
            val images = cards
                .joinToString("\n") {
                    val img = "<img " +
                        "width=$cardWidth " +
                        "height=$cardHeight " +
                        "src=\"../images/${it.name.normalize()}.png\" " +
                        "alt=${it.name}/>"
                    if (it.rarity == Rarity.Common) "$img\n$img"
                    else img
                }

            val head = "<style>$style</style>"
            val body = "<div class=\"grid\">\n$images\n</div>"
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

        //was 750x1050
        const val W: Int = 1000
        const val H: Int = 1400
    }
}
