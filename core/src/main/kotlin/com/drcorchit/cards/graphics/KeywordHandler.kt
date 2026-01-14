package com.drcorchit.cards.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.drcorchit.cards.Keyword
import com.drcorchit.cards.fantasy.FantasyCard
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.math.Compass

class KeywordHandler(
    val text: String,
    val keywords: Map<String, Keyword>,
    val abilityColor: Color = defaultAbilityColor,
    val keywordColor: Color = defaultKeywordColor,
    val abilityFont: BitmapFont = Fonts.abilityFont,
    val keywordFont: BitmapFont = Fonts.keywordFont,
    val spaceWidth: Float = 7f,
    val lineHeight: Float = 36f
) {
    val lines = text.split("\n").mapNotNull { makeLine(it) }

    fun render(x: Float, y: Float, width: Float) {
        var posY = y

        lines.forEach {
            posY = it.render(x, posY, width)
        }
    }

    private fun makeLine(string: String): Line? {
        return if (string.isBlank()) null else Line(string)
    }

    inner class Line(text: String) {
        val words = text.split(" ").mapNotNull { makeWord(it) }

        private fun makeWord(string: String): Word? {
            return if (string.isBlank()) null else Word(string)
        }

        fun render(x: Float, y: Float, width: Float): Float {
            var posX = x
            var posY = y

            words.forEach {
                val remainWidth = width - posX
                if (it.dimensions.first > remainWidth && it.dimensions.first < width) {
                    //word wrap
                    posX = x
                    posY -= lineHeight
                }

                it.render(posX, posY)
                posX += it.dimensions.first + spaceWidth
            }
            return posY - lineHeight
        }

        override fun toString(): String {
            return "Line(${words.joinToString { it.text }})"
        }
    }

    inner class Word(val text: String) {
        val keyword: Keyword? = keywords[text.normalize()]
        val font = if (keyword == null) abilityFont else keywordFont
        val color = if (keyword == null) abilityColor else keywordColor

        val dimensions = Draw.calculateDimensions(font, text, 10000f)

        fun render(x: Float, y: Float) {
            //Draw.drawRectangle(x, y - dimensions.second, dimensions.first + 2, dimensions.second, Color.RED)
            Draw.drawText(x, y, font, text, 10000f, Compass.SOUTHEAST, color)
        }

        override fun toString(): String {
            return "Word($text)"
        }
    }

    companion object {
        val defaultAbilityColor = Color.WHITE
        val defaultKeywordColor = FantasyCard.keywordColor
    }
}
