package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Textures.asSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.justice.utils.json.JsonUtils.parseFromFile
import com.drcorchit.justice.utils.math.Compass
import com.google.gson.JsonArray
import java.io.File

/**
 * [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.
 */
class Main() : ApplicationAdapter() {

    private var font1: BitmapFont? = null
    private var font2: BitmapFont? = null
    private var font3: BitmapFont? = null
    private var font4: BitmapFont? = null

    val diamondOffsetX = 50f
    val diamondOffsetY = 100f

    val starSize = 70f

    val abilityBufferX = 20f
    val abilityBufferY = 20f
    val abilityBufferW = W - 120f
    val abilityBufferH = 350f
    val abilityBufferMargin = 10f
    val abilityTextW = abilityBufferW - 2 * abilityBufferMargin

    val imageX = W / 2f;
    val imageY = H - 240f;

    override fun create() {
        //Load the batch
        Draw.batch

        font1 = initFont("enchanted_land.otf", 96)
        font2 = initFont("enchanted_land.otf", 72)
        font3 = initFont("lora.ttf", 20)
        font4 = initFont("lora.ttf", 16)

        LocalAssets.getInstance().load()
    }

    private fun drawCard(card: Card) {
        val diamond = Textures.diamond.asSprite().setOffset(Compass.CENTER)
        diamond.blend = card.motive.color

        val slate = Textures.slate.asSprite()

        val diamondBorder = Textures.diamondBorder.asSprite().setOffset(Compass.CENTER)
        diamondBorder.blend = card.motive.color

        if (card.image != null) {
            val scale = W.toFloat() / card.image.getFrames().width
            card.image.draw(Draw.batch, imageX, imageY, scale, scale, 0f)
        }
        slate.draw(Draw.batch, 0f, 0f)

        diamond.draw(Draw.batch, diamondOffsetX, H - diamondOffsetY)
        diamondBorder.draw(Draw.batch, diamondOffsetX, H - diamondOffsetY)

        diamond.draw(Draw.batch, W - diamondOffsetX, diamondOffsetY)
        diamondBorder.draw(Draw.batch, W - diamondOffsetX, diamondOffsetY)

        Draw.drawRectangle(
            abilityBufferX,
            abilityBufferY,
            abilityBufferW,
            abilityBufferH,
            Compass.NORTHEAST,
            Color.BLACK
        )

        Draw.drawText(20f, 440f, font2, card.name, W - 50f, Compass.EAST, Color.WHITE)
        if (card.power == 0) {
            val star = Textures.star.asSprite().setOffset(Compass.CENTER)
            star.draw(Draw.batch, diamondOffsetX, H - diamondOffsetY, starSize, starSize)
        } else {
            Draw.drawText(
                diamondOffsetX,
                H - diamondOffsetY,
                font1,
                card.power.toString(),
                100f,
                Compass.CENTER,
                Color.WHITE
            )
        }

        Draw.drawText(
            W - diamondOffsetX,
            diamondOffsetY,
            font1,
            card.cost.toString(),
            100f,
            Compass.CENTER,
            Color.WHITE
        )

        val textX = abilityBufferX + abilityBufferMargin
        Draw.drawText(textX, 390f, font4, card.tagsText, 1000f, Compass.EAST, Color.WHITE)

        //Ability Text
        val abilityTextY = abilityBufferY + abilityBufferH - abilityBufferMargin
        Draw.drawText(
            textX,
            abilityTextY,
            font3,
            card.abilityText,
            abilityTextW,
            Compass.SOUTHEAST,
            Color.WHITE
        )
        val quoteTextY = abilityBufferY + abilityBufferMargin
        Draw.drawText(
            textX,
            quoteTextY,
            font3,
            card.quote,
            abilityTextW,
            Compass.NORTHEAST,
            Color.LIGHT_GRAY
        )
    }

    private fun drawCardMinimal(card: Card) {
        val diamondBorder = Textures.diamondBorder.asSprite().setOffset(Compass.CENTER)
        diamondBorder.draw(Draw.batch, diamondOffsetX, H - diamondOffsetY)
        diamondBorder.draw(Draw.batch, W - diamondOffsetX, diamondOffsetY)

        Draw.drawLine(
            abilityBufferX,
            abilityBufferY + abilityBufferH,
            abilityBufferX + abilityBufferW,
            abilityBufferY + abilityBufferH,
            1f,
            Color.BLACK,
        )

        Draw.drawText(20f, 440f, font2, card.name, W - 50f, Compass.EAST, Color.BLACK)
        //type
        if (card.power == 0) {
            val star = Textures.star.asSprite().setOffset(Compass.CENTER)
            star.blend = Color.BLACK
            star.draw(Draw.batch, diamondOffsetX, H - diamondOffsetY, starSize, starSize)
        } else {
            Draw.drawText(
                diamondOffsetX,
                H - diamondOffsetY,
                font1,
                card.power.toString(),
                100f,
                Compass.CENTER,
                Color.BLACK
            )
        }


        Draw.drawText(
            W - diamondOffsetX,
            diamondOffsetY,
            font1,
            card.cost.toString(),
            100f,
            Compass.CENTER,
            Color.BLACK
        )

        val textX = abilityBufferX + abilityBufferMargin
        Draw.drawText(textX, 390f, font4, card.tagsText, 1000f, Compass.EAST, Color.BLACK)

        //Ability Text
        val abilityTextY = abilityBufferY + abilityBufferH - abilityBufferMargin
        Draw.drawText(
            textX,
            abilityTextY,
            font3,
            card.abilityText,
            abilityTextW,
            Compass.SOUTHEAST,
            Color.BLACK
        )
        val quoteTextY = abilityBufferY + abilityBufferMargin
        Draw.drawText(
            textX,
            quoteTextY,
            font3,
            card.quote,
            abilityTextW,
            Compass.NORTHEAST,
            Color.GRAY
        )
    }

    override fun render() {
        //25 = Kali
        //50 = Neromir
        //75 = Allmother
        val card = Card(CARDS[69].asJsonObject)

        ScreenUtils.clear(1f, 1f, 1f, 1f)
        Draw.batch.begin()
        drawCard(card)
        //drawCardMinimal(card)
        Draw.batch.end()
    }

    override fun dispose() {
        Draw.batch.dispose()
    }


    companion object {
        const val W: Int = 640
        const val H: Int = 960

        private val CARDS: JsonArray
        private val CHARACTERS: String

        init {
            val alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            val num = "1234567890"
            val punctuation = "!?.,;:"
            val quotes = "'‘’\"“”"
            val symbols = "()[]{}<>`~@#$€%^&*+="
            val bars = "-–—_/|\\"
            val misc =
                "\u0000\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008F\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009F\u00A0\u00A1\u00A2\u00A3\u00A4\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA\u00AB\u00AC\u00AD\u00AE\u00AF\u00B0\u00B1\u00B2\u00B3\u00B4\u00B5\u00B6\u00B7\u00B8\u00B9\u00BA\u00BB\u00BC\u00BD\u00BE\u00BF\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u00C6\u00C7\u00C8\u00C9\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF\u00D0\u00D1\u00D2\u00D3\u00D4\u00D5\u00D6\u00D7\u00D8\u00D9\u00DA\u00DB\u00DC\u00DD\u00DE\u00DF\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u00E7\u00E8\u00E9\u00EA\u00EB\u00EC\u00ED\u00EE\u00EF\u00F0\u00F1\u00F2\u00F3\u00F4\u00F5\u00F6\u00F7\u00F8\u00F9\u00FA\u00FB\u00FC\u00FD\u00FE\u00FF"
            CHARACTERS = alpha + num + punctuation + quotes + symbols + bars + misc

            CARDS = parseFromFile("cards.json")!!.first.asJsonArray
        }

        private fun initFont(path: String, size: Int): BitmapFont {
            var path = path
            val params = FreeTypeFontParameter()
            params.size = size
            params.characters = CHARACTERS
            path = "assets/fonts/$path"
            val file = File(path)
            val generator = FreeTypeFontGenerator(FileHandle(file))
            val value = generator.generateFont(params)
            generator.dispose()
            return value
        }
    }
}