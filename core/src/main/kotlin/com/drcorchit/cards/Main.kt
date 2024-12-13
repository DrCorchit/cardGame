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
    //lateinit var card: CardActor
    //lateinit var stage: Stage
    val stage by lazy { Stage() }
    val card by lazy { CardActor(cards[index]) }

    override fun create() {
        //Load the batch
        Draw.batch
        LocalAssets.getInstance().load()
        stage.addActor(card)

        val folder =
            File("assets/images/cards/used").listFiles()!!
                .filter { it.isDirectory }
                .flatMap { it.listFiles()!!.asList() }
                .map { it.nameWithoutExtension.normalize() }
                .toMutableSet()
        folder.removeAll(cards.map { it.name.normalize() }.toSet())
        if (folder.isNotEmpty()) {
            println("Unused card arts {\n  ${folder.joinToString("\n  ")}\n}")
        }
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

        //was 750x1050
        const val W: Int = 1000
        const val H: Int = 1400
    }
}
