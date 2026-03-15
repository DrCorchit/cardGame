package com.drcorchit.cards.graphics

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.drcorchit.cards.LocalAssets
import com.drcorchit.justice.utils.logging.Logger

object Textures {

    var log: Logger = Logger.getLogger(Textures::class.java)

    val white = initTexture("white.png")

    val fantasyBorder = initTexture("border_fantasy.png")
    val spaceBorder = initTexture("border_space.png")
    val tray = initTexture("paper.png")
    val brushStroke = initTexture("brush_stroke.png")
    val line = initTexture("line.png")
    val diamondBlack = initTexture("diamond_black.png")

    val armorBack = initTexture("armor.png")
    val armorBlack = initTexture("armor_black.png")

    val costBack = initTexture("provisions.png")
    val provisionsBlack = initTexture("provisions_black.png")

    val land = initTexture("land.png")
    val air = initTexture("air_sharp.png")
    val dark = initTexture("dark_sharp.png")
    val earth = initTexture("earth_sharp.png")
    val fire = initTexture("fire_sharp.png")
    val light = initTexture("light_sharp.png")
    val water = initTexture("water_sharp.png")

    val manaWhite = initTexture("mana_white.png")

    val metal = initTexture("metal.jpg")
    val power = initTexture("power.png")
    val blackHole= initTexture("black_hole.jpg")
    val card3Back = initTexture("cardback.jpg")

    //card
    val cardWithInset = initTexture("card_with_inset.png")
    val cardWithCutaway = initTexture("card_cutaway.png")
    val cardWithText = initTexture("card_with_text.png")

    val titleBar = initTexture("title_bar_2.png")
    val scoreArea = initTexture("score_area.png")
    val artBorder = initTexture("art_border.png")
    val textArea = initTexture("text_area.png")

    //card2
    val border2 = initTexture("border_space.png")
    val card2SmallWindow = initTexture("card2_small_window.png")
    val card2LargeWindow = initTexture("card2_large_window.png")
    val numberBox = initTexture("number_box.png")

    //Arrow
    val arrowLeft = initTexture("arrow_left.png")
    val arrowRight = initTexture("arrow_right.png")
    val arrowHoriz = initTexture("arrow_horiz.png")
    val arrowVert = initTexture("arrow_vert.png")
    val arrowCorner1 = initTexture("arrow_corner_1.png")
    val arrowCorner2 = initTexture("arrow_corner_2.png")
    val arrowCorner3 = initTexture("arrow_corner_3.png")
    val arrowCorner4 = initTexture("arrow_corner_4.png")

    fun Texture.asSprite(
        width: Int = 1,
        height: Int = 1,
        frames: Int = width * height
    ): AnimatedSprite {
        return AnimatedSprite(TextureSheet(this, width, height, frames))
    }

    fun Texture.toNinepatch(left: Int, right: Int, top: Int, bottom: Int): NinePatch {
        return NinePatch(TextureRegion(this), left, right, top, bottom)
    }

    fun initTexture(name: String): Texture {
        return LocalAssets.getInstance().create(name)
    }
}
