package com.drcorchit.cards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.ScreenUtils
import com.drcorchit.cards.Card.Companion.imageH
import com.drcorchit.cards.Card.Companion.imageRatio
import com.drcorchit.cards.Card.Companion.imageW
import com.drcorchit.cards.Card.Companion.midWidth
import com.drcorchit.cards.Main.Companion.BORDER
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.IMAGE_H
import com.drcorchit.cards.Main.Companion.IMAGE_W
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.graphics.AnimatedSprite
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.graphics.Draw.batch
import com.drcorchit.cards.graphics.Drawable
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.math.Compass
import java.io.File

class Card2(
    val name: String,
    val cost: Int,
    val power: Int,
    val type: Type,
    val abilities: List<String>,
): Drawable {
    var image: AnimatedSprite? = updateGraphic()

    enum class Type(file: String) {
        Computer("computers"),
        Crew("crew"),
        Defense("defense"),
        Engine("engines"),
        Hull("hull"),
        LifeSupport("life_support"),
        Special("specials"),
        Weapon("weapons");

        val file = File("assets/txt/space_cards/$file.txt")
    }

    override fun draw() {
        ScreenUtils.clear(Color.BLACK)
        //Draw card art
        val image = this.image
        if (image != null) {
            val sourceImageRatio = image.getFrames().ratio
            val destImageRatio = imageRatio
            val imageScale =
                if (sourceImageRatio > destImageRatio) {
                    imageW / image.getFrames().width
                } else {
                    imageH / image.getFrames().height
                }

            image.draw(batch, midWidth, H + BORDER - 10f, imageScale, imageScale, 0f)
        }
        Draw.drawRectangle(0f, 0f, IMAGE_W.toFloat(), BORDER, Color.BLACK)
        Draw.drawRectangle(0f, H + BORDER, IMAGE_W.toFloat(), IMAGE_H.toFloat(), Color.BLACK)
        Draw.drawRectangle(0f, 0f, BORDER, IMAGE_H.toFloat(), Color.BLACK)
        Draw.drawRectangle(W + BORDER, 0f, IMAGE_W.toFloat(), IMAGE_H.toFloat(), Color.BLACK)
    }

    override fun updateGraphic(): AnimatedSprite? {
        val normalized = name.normalize()
        val base = "assets/images/space_cards"
        val png = "$base/$normalized.png"
        val jpg = "$base/$normalized.jpg"
        val texture = if (File(png).exists()) Texture(png)
        else if (File(jpg).exists()) Texture(jpg)
        else null

        if (texture == null) {
            //println("Could not load $png or $jpg")
        } else {
            image = texture.asSprite().setOffset(Compass.NORTH)
        }
        return image
    }
}
