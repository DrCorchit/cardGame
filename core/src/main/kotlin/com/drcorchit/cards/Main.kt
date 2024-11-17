package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.drcorchit.cards.Card.Companion.cards
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.justice.utils.IOUtils
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.json.JsonUtils.parseFromFile
import com.drcorchit.justice.utils.logging.Logger
import java.io.File
import java.util.zip.Deflater
import kotlin.random.Random

/**
 * [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.
 */
class Main : ApplicationAdapter() {
    //27 = Kali
    //52 = Neromir
    //78 = Allmother
    var index = 0

    override fun create() {
        //Load the batch
        Draw.batch
        LocalAssets.getInstance().load()

        index = Random.nextInt(cards.size)
    }

    override fun render() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            index = (index + 1) % cards.size
        }

        val card = cards[index]

        Draw.batch.begin()
        card.draw()
        Draw.batch.end()

        //generatePrintableCards()
    }

    private fun generatePrintableCards() {
        renderCards()
        assembleHtml()
        Gdx.app.exit()
    }

    fun renderCards() {
        cards.forEach {
            Draw.batch.begin()
            it.draw()
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
        val printedPageWidth = 720
        val columns = 3
        val space = 10
        val totalColumnsWidth = (columns + 1) * space
        val cardsTotalWidth = printedPageWidth - totalColumnsWidth
        val cardWidth = cardsTotalWidth / columns
        val cardHeight = cardWidth * Card.cardRatio

        try {
            val style = ".grid { " +
                "display: grid; " +
                "grid-template-columns:${" auto".repeat(columns)}; " +
                "gap: ${space}px; } "
            val images = File("output/images")
                .listFiles()!!
                .joinToString("\n") { "<img width=$cardWidth height=$cardHeight src=\"../images/${it.name}\" alt=${it.name}/>" }

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
        private val logger = Logger.getLogger(Main::class.java)

        const val W: Int = 750
        const val H: Int = 1050
    }
}
