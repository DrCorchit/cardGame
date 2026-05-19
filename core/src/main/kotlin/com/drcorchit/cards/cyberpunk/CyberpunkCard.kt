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
    val category: Category,
    val cost: Int,
    val rizz: Int,
    val chic: Int,
    val glam: Int,
    val pizzazz: Int,
    val effect: String? = "Card ability text goes here.",
) : Drawable {

    val costStr = "$cost$"

    val stats =
        mapOf(
            Stat.Rizz to rizz,
            Stat.Chic to chic,
            Stat.Glam to glam,
            Stat.Pizzazz to pizzazz
        )
            .filter { it.value > 0 }
            .mapValues { String.format("%-8s%d", it.key, it.value) }

    private var image: AnimatedSprite? = null

    override val outputLocation =
        "output/images/full/cyberpunk/${name.normalize()}.png"

    companion object {
        val card = Textures.card3.asSprite()

        val textColor = Color(.85f, .85f, .85f, 1f)
        val monoFont = Fonts.statsFont3

        val imageX = BORDER + (W / 2f)
        val imageY = BORDER + 950f
        val imageW = 650f
        val imageH = 650f

        val costX = BORDER + 78f
        val costY = BORDER + H - 95f

        val categoryX = BORDER + (W / 2f)
        val categoryY = BORDER + 955f

        val nameX = BORDER + W / 2f
        val nameY = BORDER + 290f

        val divider = Textures.divider2.asSprite().setOffset(Compass.CENTER)
        val dividerX = nameX
        val dividerY = nameY - 32
        val dividerW = W * .7f
        val dividerH = 2f //was dividerW / 20f

        val textX = BORDER + 80f
        val textY = BORDER + 240f
        val textVSep = 35f

        val effectX = BORDER + 300f
        val effectY = textY
        val effectW = 370f
    }

    override fun draw() {
        Draw.drawCardImage(image, imageX, imageY, imageW, imageH)

        card.draw(batch, BORDER, BORDER, W, H)

        //Cost
        Draw.drawText(costX, costY, Fonts.costFont3, costStr, 1000f, Compass.CENTER, textColor)

        //Category
        Draw.drawText(categoryX, categoryY, Fonts.nameFont3, category.displayName, 1000f, Compass.CENTER, textColor)

        //Name
        Draw.drawText(nameX, nameY, Fonts.categoryFont3, name, 1000f, Compass.CENTER, textColor)

        divider.draw(batch, dividerX, dividerY, dividerW, dividerH)


        //Stats
        val x = textX
        var y = textY
        stats.forEach { (stat, string) ->
            //val color = Color(.85f, .85f, .85f, 1f)
            Draw.drawText(x, y, monoFont, string, 1000f, Compass.SOUTHEAST, stat.color)
            y -= textVSep
        }

        if (effect != null) {
            Draw.drawText(effectX, effectY, monoFont, effect, effectW, Compass.SOUTHEAST, textColor)
        }
    }

    override fun updateGraphic(): AnimatedSprite? {
        val normalized = name.normalize()

        val base = "assets/images/cyberpunk_cards/fashion/${category.name.lowercase()}"

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
