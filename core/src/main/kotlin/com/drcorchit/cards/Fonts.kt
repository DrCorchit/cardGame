package com.drcorchit.cards

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Textures
import java.io.File

object Fonts {
    private val CHARACTERS: String

    init {
        val alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val num = "1234567890"
        val punctuation = "!?.,;:"
        val quotes = "'‘’\"“”"
        val symbols = "()[]{}<>`~@#$€%^&*+="
        val bars = "-–—_/|\\"
        val dots = "⋅⸱⸳.•⦁⁃·˙∙˚°˳॰ⴰ⸰º∘⚬◦○"
        val misc =
            "\u0000\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008F\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009F\u00A0\u00A1\u00A2\u00A3\u00A4\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA\u00AB\u00AC\u00AD\u00AE\u00AF\u00B0\u00B1\u00B2\u00B3\u00B4\u00B5\u00B6\u00B7\u00B8\u00B9\u00BA\u00BB\u00BC\u00BD\u00BE\u00BF\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u00C6\u00C7\u00C8\u00C9\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF\u00D0\u00D1\u00D2\u00D3\u00D4\u00D5\u00D6\u00D7\u00D8\u00D9\u00DA\u00DB\u00DC\u00DD\u00DE\u00DF\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u00E7\u00E8\u00E9\u00EA\u00EB\u00EC\u00ED\u00EE\u00EF\u00F0\u00F1\u00F2\u00F3\u00F4\u00F5\u00F6\u00F7\u00F8\u00F9\u00FA\u00FB\u00FC\u00FD\u00FE\u00FF"
        CHARACTERS = alpha + num + punctuation + quotes + symbols + bars + misc + dots
    }

    data class FontSizes (val numberFont: Int, val numberFontSmall: Int, val nameFont: Int, val abilityFont: Int, val quoteFont: Int)

    val medFontSizes = FontSizes(96, 84, 96, 40, 32)
    val smFontSizes = FontSizes(72, 72, 80, 28, 24)
    val fontSizes = smFontSizes

    val numberFont = initFont("enchanted_land.ttf", fontSizes.numberFontSmall)
    val numberFontSmall = initFont("enchanted_land.ttf", fontSizes.numberFontSmall)
    val numberFontXS = initFont("enchanted_land.ttf", 64)

    val nameFont = initFont("enchanted_land.ttf", fontSizes.nameFont)

    //val defaultFont = "roboto_condensed"
    val defaultFont = "lato"
    val abilityFont = initFont("$defaultFont.ttf", fontSizes.abilityFont)
    val tagFont = initFont("$defaultFont.ttf", fontSizes.quoteFont)
    val keywordFont = initFont("$defaultFont.ttf", fontSizes.quoteFont)
    val quoteFont = initFont("${defaultFont}_italic.ttf", fontSizes.quoteFont)

    init {
        fun addManaIconsToFont (font: BitmapFont, region: TextureRegion) {
            font.regions.add(region)

            fun addGlyph(char: Char, i: Int, j: Int) {
                val glyph = BitmapFont.Glyph()
                glyph.id = char.code
                glyph.page = 1
                glyph.u = i / 18f
                glyph.v = (j + 4) / 12f
                glyph.u2 = (i + 4) / 18f
                glyph.v2 = j / 12f
                glyph.srcX = 0
                glyph.srcY = 0
                glyph.yoffset = -(fontSizes.abilityFont + 4)
                glyph.width = fontSizes.abilityFont
                glyph.height = fontSizes.abilityFont
                glyph.xadvance = fontSizes.abilityFont

                font.data.setGlyph(char.code, glyph)
            }

            //Add land glyphs
            addGlyph('\u0010', 1, 1)
            addGlyph('\u0011', 7, 1)
            addGlyph('\u0012', 13, 1)
            addGlyph('\u0013', 1, 7)
            addGlyph('\u0014', 7, 7)
            addGlyph('\u0015', 13, 7)
        }

        addManaIconsToFont(abilityFont, TextureRegion(Textures.land))

        val precoloredFontTexture = Draw.precolorTexture(abilityFont.regions[0].texture, Card.textColor)
        abilityFont.regions[0] = TextureRegion(precoloredFontTexture)

        //default is 34f
        abilityFont.data.setLineHeight(32f)
        //default is 29f
        keywordFont.data.setLineHeight(25f)
    }

    private fun initFont(path: String, size: Int): BitmapFont {
        val params = FreeTypeFontParameter()
        params.size = size
        params.characters = CHARACTERS
        val file = File("assets/fonts/$path")
        val generator = FreeTypeFontGenerator(FileHandle(file))
        val value = generator.generateFont(params)
        value.setUseIntegerPositions(true)
        generator.dispose()
        return value
    }
}
