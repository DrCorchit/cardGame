package com.drcorchit.cards.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.drcorchit.cards.Keyword
import com.drcorchit.cards.fantasy.FantasyCard
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.math.Compass

class KeywordHandler(
    val text: String,
    val keywords: Map<String, Keyword>,
    val abilityColor: Color = defaultAbilityColor,
    val keywordColor: Color = defaultKeywordColor,
    val abilityFont: BitmapFont = Fonts.abilityFont,
    val keywordFont: BitmapFont = Fonts.keywordFont,
    val lineHeight: Float = 34f
) {
    private val spriteSize = lineHeight - 4

    private val spriteVoff = lineHeight - 10
    private val lines = text.split("\n").mapNotNull { makeLine(it) }

    fun render(x: Float, y: Float, width: Float) {
        var posY = y

        lines.forEach {
            posY = it.render(x, posY, width)
        }
    }

    private fun makeLine(string: String): Line? {
        return if (string.isBlank()) null else Line(string.trim())
    }

    inner class Line(text: String) {
        val words = wordSplit.split(text).map { makeWord(it) }

        private fun makeWord(string: String): Word {
            if (sprites[string.normalize()] != null) return SpriteWord(string)
            if (keywords[string.normalize()] != null) return KeywordWord(string)
            return TextWord(string)
        }

        fun render(x: Float, y: Float, width: Float): Float {
            var posX = x
            var posY = y

            words.forEach {
                val remainWidth = width - (posX - x)
                if (it.width > remainWidth && it.width < width) {
                    //word wrap
                    posX = x
                    posY -= lineHeight
                }

                //Draw.drawRectangle(posX, posY - lineHeight, it.width, 10f, Color.RED)
                it.render(posX, posY)
                posX += it.width + 2f
            }
            return posY - lineHeight
        }

        override fun toString(): String {
            return "Line(${words.joinToString { it.text }})"
        }
    }

    abstract inner class Word(val text: String) {
        abstract val width: Float

        abstract fun render(x: Float, y: Float)

        override fun toString(): String {
            return "Word($text)"
        }
    }

    inner class KeywordWord(text: String) : Word(text) {
        override val width = Draw.calculateDimensions(keywordFont, text, 10000f).first
        override fun render(x: Float, y: Float) {
            Draw.drawText(x, y, keywordFont, text, 10000f, Compass.SOUTHEAST, keywordColor)
        }
    }

    inner class TextWord(text: String) : Word(text) {
        override val width = Draw.calculateDimensions(abilityFont, text, 10000f).first
        override fun render(x: Float, y: Float) {
            Draw.drawText(x, y, abilityFont, text, 10000f, Compass.SOUTHEAST, abilityColor)
        }
    }

    inner class SpriteWord(text: String) : Word(text) {
        val sprite: AnimatedSprite = sprites[text.normalize()]!!

        override val width = spriteSize

        override fun render(x: Float, y: Float) {
            sprite.draw(Draw.batch, x, y - spriteVoff, spriteSize, spriteSize)
        }
    }

    companion object {
        val wordSplit = Regex("\\b")

        val defaultAbilityColor = FantasyCard.textColor
        val defaultKeywordColor = FantasyCard.keywordColor

        val airSpr = Textures.air.asSprite()
        val darkSpr = Textures.dark.asSprite()
        val earthSpr = Textures.earth.asSprite()
        val fireSpr = Textures.fire.asSprite()
        val lightSpr = Textures.light.asSprite()
        val waterSpr = Textures.water.asSprite()

        val sprites = mapOf(
            "air_mana" to airSpr,
            "dark_mana" to darkSpr,
            "earth_mana" to earthSpr,
            "fire_mana" to fireSpr,
            "light_mana" to lightSpr,
            "water_mana" to waterSpr,
        )
    }
}
