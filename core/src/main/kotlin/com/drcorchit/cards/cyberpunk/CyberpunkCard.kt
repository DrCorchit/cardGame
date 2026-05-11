package com.drcorchit.cards.cyberpunk

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.drcorchit.cards.Main.Companion.BORDER
import com.drcorchit.cards.Main.Companion.H
import com.drcorchit.cards.Main.Companion.W
import com.drcorchit.cards.graphics.*
import com.drcorchit.cards.graphics.Draw.batch
import com.drcorchit.cards.graphics.Textures.asSprite
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.math.Compass
import java.io.File

class CyberpunkCard(
    override val name: String,
    val cost: Int,
    val rizz: Int,
    val chic: Int,
    val glam: Int,
    val panache: Int,
    val effect: String? = "Card ability text goes here."
) : Drawable {

    val costStr = "$cost$"

    val stats =
        mapOf(
            Stat.Rizz to rizz,
            Stat.Chic to chic,
            Stat.Glam to glam,
            Stat.Panache to panache
        )
            .filter { it.value > 0 }
            .mapValues { String.format("%-8s%d", it.key, it.value) }

    private var image: AnimatedSprite? = null

    override val outputLocation =
        "output/images/full/cyberpunk/${name.normalize()}.png"

    companion object {
        val card = Textures.card3.asSprite()

        val imageX = BORDER + (W / 2f)
        val imageY = BORDER + 900f
        val imageW = 650f
        val imageH = 650f

        val costX = BORDER + 78f
        val costY = BORDER + H - 95f
        val costColor = Color(.85f, .85f, .85f, 1f)

        val nameX = BORDER + (W / 2f)

        //was BORDER + 300f
        val nameY = BORDER + 955f
        val nameColor = Color(.85f, .85f, .85f, 1f)

        val textX = BORDER + 80f
        val textY = BORDER + 285f
        val textVSep = 40f
        val effectColor = Color(.85f, .85f, .85f, 1f)

        val effectX = BORDER + 300f
        val effectY = BORDER + 285f
        val effectW = 370f
    }

    override fun draw() {
        Draw.drawCardImage(image, imageX, imageY, imageW, imageH)
        card.draw(batch, BORDER, BORDER, W, H)

        //Cost
        Draw.drawText(costX, costY, Fonts.costFont3, costStr, 1000f, Compass.CENTER, costColor)

        //Name
        Draw.drawText(nameX, nameY, Fonts.nameFont3, name, 1000f, Compass.CENTER, nameColor)

        //Stats
        val x = textX
        var y = textY
        stats.forEach { (stat, string) ->
            //val color = Color(.85f, .85f, .85f, 1f)
            Draw.drawText(x, y, Fonts.statsFont3, string, 1000f, Compass.SOUTHEAST, stat.color)
            y -= textVSep
        }

        if (effect != null) {
            Draw.drawText(effectX, effectY, Fonts.abilityFont3, effect, effectW, Compass.SOUTHEAST, effectColor)
        }
    }

    override fun updateGraphic(): AnimatedSprite? {
        val normalized = name.normalize()

        val base = "assets/images/cyberpunk_cards/fashion"

        val png = "$base/$normalized.png"
        val jpg = "$base/$normalized.jpg"
        val texture = if (File(png).exists()) Texture(FileHandle(png), true)
        else if (File(jpg).exists()) Texture(FileHandle(jpg), true)
        else null

        if (texture == null) {
            println("Could not load $png or $jpg")
        } else {
            texture.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.MipMap)
            image = texture.asSprite().setOffset(Compass.NORTH)
        }
        return image
    }
}
