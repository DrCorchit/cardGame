package com.drcorchit.cards.graphics

import com.drcorchit.cards.LocalAssets
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.drcorchit.cards.Main
import com.drcorchit.justice.utils.math.Compass
import java.io.File
import java.util.*
import java.util.zip.Deflater
import kotlin.math.*

object Draw {
    val batch: PolygonSpriteBatch = PolygonSpriteBatch()
    private val shape: ShapeRenderer

    init {
        batch.enableBlending()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or (if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0))
        shape = ShapeRenderer()
    }

    fun resize(w: Float, h: Float) {
        batch.projectionMatrix.setToOrtho2D(0f, 0f, w, h)
        shape.projectionMatrix = batch.projectionMatrix
    }

    fun textureToPixmap(tex: Texture): Pixmap {
        val data = tex.textureData
        if (!data.isPrepared) data.prepare()
        return data.consumePixmap()
    }

    fun saveTextureToFile(path: String, tex: Texture) {
        PixmapIO.writePNG(
            FileHandle(File(path)),
            textureToPixmap(tex),
            Deflater.DEFAULT_COMPRESSION,
            false
        )
    }

    fun textureRegionToTexture(region: TextureRegion): Texture {
        val input = textureToPixmap(region.texture)
        val w = region.regionWidth
        val h = region.regionHeight
        val output = Pixmap(w, h, Pixmap.Format.RGBA8888)
        for (x in 0 until w) {
            for (y in 0 until h) {
                val colorInt = input.getPixel(region.regionX + x, region.regionY + y)
                output.drawPixel(x, y, colorInt)
            }
        }

        return LocalAssets.create(output)
    }

    fun precolorTexture(texture: Texture, color: Color): Texture {
        val input = textureToPixmap(texture)
        val w = texture.width
        val h = texture.height
        val output = Pixmap(w, h, Pixmap.Format.RGBA8888)
        for (x in 0 until w) {
            for (y in 0 until h) {
                val colorInt = input.getPixel(x, y)
                val newColor = Color()
                Color.rgba8888ToColor(newColor, colorInt)
                newColor.mul(color)
                output.drawPixel(x, y, Color.rgba8888(newColor))
            }
        }

        return LocalAssets.create(output)
    }

    //Splits a given image into hue and shading components
    fun separateShading(input: Pixmap): Pair<Pixmap, Pixmap> {
        val w = input.width
        val h = input.height
        val baseMap = Pixmap(w, h, Pixmap.Format.RGBA8888)
        val shadeMap = Pixmap(w, h, Pixmap.Format.RGBA8888)

        for (i in 0 until w) {
            for (j in 0 until h) {
                val pixel = Color()
                Color.rgba8888ToColor(pixel, input.getPixel(i, j))
                val hsv = pixel.toHsv(FloatArray(3))
                val base = Color.WHITE.cpy().fromHsv(hsv[0], 1f, 1f)
                baseMap.drawPixel(i, j, Color.rgba8888(base))
                val shade = Color.WHITE.cpy().fromHsv(0f, hsv[1], hsv[2])
                val alpha = 1 - (shade.r - shade.g)
                val rgb = shade.g / alpha
                shade.r = rgb
                shade.g = rgb
                shade.b = rgb
                shade.a = alpha
                shadeMap.drawPixel(i, j, Color.rgba8888(shade))
            }
        }
        return baseMap to shadeMap
    }

    private fun generateLumSatGradient() {
        val px = Pixmap(256, 256, Pixmap.Format.RGBA8888)
        for (i in 0..255) {
            for (j in 0..255) {
                val c = Color.WHITE.cpy().fromHsv(0f, (i / 255f), (j / 255f))
                val alpha = 1 - (c.r - c.g)
                val rgb = c.g / alpha
                c.r = rgb
                c.g = rgb
                c.b = rgb
                c.a = alpha
                px.drawPixel(i, j, Color.rgba8888(c))
            }
        }
        PixmapIO.writePNG(FileHandle(File("grad2.png")), px)
    }

    private fun generateHueGradient() {
        val px = Pixmap(360, 1, Pixmap.Format.RGBA8888)
        for (i in 0..359) {
            val c = Color.WHITE.cpy().fromHsv(i.toFloat(), 1f, 1f)
            px.drawPixel(i, 0, Color.rgba8888(c))
        }
        PixmapIO.writePNG(FileHandle(File("hue_gradient.png")), px)
    }

    @JvmStatic
    fun Compass.getOffset(w: Float, h: Float): Pair<Float, Float> {
        val x: Float = (percentHoriz - 1) * w
        val y: Float = (percentVert - 1) * h
        return x to y
    }

    fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Color?) {
        batch.end()
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = color
        shape.rectLine(x1, y1, x2, y2, thickness)
        shape.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
        batch.begin()
    }

    fun drawArrowPolar(x: Float, y: Float, angle: Float, len: Float, size: Float, color: Color?) {
        val targX = (x + len * cos(Math.toRadians(angle.toDouble()))).toFloat()
        val targY = (y + len * sin(Math.toRadians(angle.toDouble()))).toFloat()
        drawArrowRect(x, y, targX, targY, size, color)
    }

    fun drawArrowRect(x: Float, y: Float, targX: Float, targY: Float, size: Float, color: Color?) {
        val angle = atan2((targY - y).toDouble(), (targX - x).toDouble())
        val arrowAngle = Math.PI / 6

        val x1 = (targX - size * cos(angle + arrowAngle)).toFloat()
        val y1 = (targY - size * sin(angle + arrowAngle)).toFloat()
        val x2 = (targX - size * cos(angle - arrowAngle)).toFloat()
        val y2 = (targY - size * sin(angle - arrowAngle)).toFloat()

        batch.end()
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = color
        shape.line(x, y, targX, targY)
        shape.triangle(targX, targY, x1, y1, x2, y2)
        shape.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
        batch.begin()
    }

    @JvmOverloads
    fun drawArrow(
        x: Float,
        y: Float,
        x2: Float,
        y2: Float,
        color: Color?,
        bendX: Float = (x2 + x) / 2,
        scale: Float = 1f
    ) {
        var x2 = x2
        var bendX = bendX
        val arrowW: Float = Textures.arrowLeft.width * scale
        val arrowH: Float = Textures.arrowLeft.height * scale
        val cornerW: Float = Textures.arrowCorner1.width * scale
        val cornerH: Float = Textures.arrowCorner1.height * scale

        val batchColor = batch.color.cpy()
        batch.color = color
        //We use a try block to make sure batch color gets reset
        try {
            var yDif: Float
            val vertY: Float
            val leftW: Float
            val rightW: Float

            if (x > x2) {
                batch.draw(Textures.arrowLeft, x2, y2 - arrowH / 2, arrowW, arrowH)
                x2 += arrowW
                if (x2 > x) return

                bendX -= cornerW / 2
                if (bendX + cornerW > x || bendX < x2) bendX = (x + x2 - cornerW) / 2
                //No point drawing bend when its like this
                if (bendX + cornerW > x || bendX < x2) return

                leftW = bendX - x2
                rightW = x - (bendX + cornerW)
                if (leftW > 0) batch.draw(Textures.arrowHoriz, x2, y2 - cornerH / 2, leftW, cornerH)
                if (rightW > 0) batch.draw(
                    Textures.arrowHoriz,
                    bendX + cornerW,
                    y - cornerH / 2,
                    rightW,
                    cornerH
                )

                yDif = y - y2
                if (yDif > 0) {
                    vertY = y2 + cornerH / 2
                    batch.draw(Textures.arrowCorner2, bendX, y2 - cornerH / 2, cornerW, cornerH)
                    batch.draw(Textures.arrowCorner4, bendX, y - cornerH / 2, cornerW, cornerH)
                } else if (yDif < 0) {
                    yDif *= -1f
                    vertY = y + cornerH / 2
                    batch.draw(Textures.arrowCorner1, bendX, y - cornerH / 2, cornerW, cornerH)
                    batch.draw(Textures.arrowCorner3, bendX, y2 - cornerH / 2, cornerW, cornerH)
                } else {
                    vertY = y
                    batch.draw(Textures.arrowHoriz, x2, y - cornerH / 2, x - x2, cornerH)
                }
            } else {
                batch.draw(Textures.arrowRight, x2 - arrowW, y2 - arrowH / 2, arrowW, arrowH)
                x2 -= arrowW
                if (x2 < x) return

                bendX -= cornerW / 2
                if (bendX + cornerW > x2 || bendX < x) bendX = (x + x2 - cornerW) / 2
                //No point drawing bend when its like this
                if (bendX + cornerW > x2 || bendX < x) return

                leftW = bendX - x
                rightW = x2 - (bendX + cornerW)
                if (leftW > 0) batch.draw(Textures.arrowHoriz, x, y - cornerH / 2, leftW, cornerH)
                if (rightW > 0) batch.draw(
                    Textures.arrowHoriz,
                    bendX + cornerW,
                    y2 - cornerH / 2,
                    rightW,
                    cornerH
                )

                yDif = y - y2
                if (yDif > 0) {
                    vertY = y2 + cornerH / 2
                    batch.draw(Textures.arrowCorner1, bendX, y2 - cornerH / 2, cornerW, cornerH)
                    batch.draw(Textures.arrowCorner3, bendX, y - cornerH / 2, cornerW, cornerH)
                } else if (yDif < 0) {
                    yDif *= -1f
                    vertY = y + cornerH / 2
                    batch.draw(Textures.arrowCorner2, bendX, y - cornerH / 2, cornerW, cornerH)
                    batch.draw(Textures.arrowCorner4, bendX, y2 - cornerH / 2, cornerW, cornerH)
                } else {
                    vertY = y
                    batch.draw(Textures.arrowHoriz, x2, y - cornerH / 2, x - x2, cornerH)
                }
            }

            if (yDif - cornerH > 0) {
                batch.draw(Textures.arrowVert, bendX, vertY, cornerW, yDif - cornerH)
            }
        } finally {
            batch.color = batchColor
        }
    }

    fun drawRectangle(x: Float, y: Float, w: Float, h: Float, color: Color?) {
        drawRectangle(x, y, w, h, Compass.NORTHEAST, color)
    }

    fun drawRectangle(x: Float, y: Float, w: Float, h: Float, align: Compass, color: Color?) {
        val offsets: Pair<Float, Float> = align.getOffset(w, h)

        val temp = batch.color.cpy()
        batch.color = color
        batch.draw(Textures.white, x + offsets.first, y + offsets.second, w, h)
        batch.color = temp

        /*
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(color);
		shape.rect(x + offsets.first(), y + offsets.second(), w, h);
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
		// */
    }

    fun drawCircle(x: Float, y: Float, radius: Float, color: Color?) {
        batch.end()
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = color
        shape.circle(x, y, radius)
        shape.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
        batch.begin()
    }

    fun drawRectangleOutline(
        a: Actor?,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        align: Compass,
        color: Color?
    ) {
        val src = if (a == null) Vector2(x, y) else a.localToScreenCoordinates(Vector2(x, y))
        src.y = Main.H - src.y

        val offsets: Pair<Float, Float> = align.getOffset(w, h)
        batch.end()
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shape.begin(ShapeRenderer.ShapeType.Line)
        shape.color = color
        shape.rect(src.x + offsets.first, src.y + offsets.second, w, h)
        shape.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
        batch.begin()
    }

    fun drawText(x: Float, y: Float, font: BitmapFont?, text: String?): Pair<Float, Float> {
        return drawText(x, y, font, text, 0f, Compass.CENTER, Color.WHITE)
    }

    fun drawText(
        x: Float,
        y: Float,
        font: BitmapFont?,
        text: String?,
        width: Float,
        align: Compass,
        color: Color?
    ): Pair<Float, Float> {
        var x = x
        var y = y
        if (font == null || text == null) return 0f to 0f
        val layout = GlyphLayout()
        layout.setText(font, text, color, width, align.textAlign, true)
        val w = layout.width
        val h = layout.height
        //Glyph layout partially handles the X offset
        if (width != 0f) {
            x += (align.horiz - 1) * (width / 2)
        }
        val offsets: Pair<Float, Float> = align.getOffset(w, h)
        y += offsets.second + h

        font.draw(batch, layout, x, y)
        return w to h
    }

    //Calculates the dimensions of the text that would be draw without drawing it
    //Isn't any faster than normal, but avoids unnecessary args (font color, x/y pos, alignment etc)
    fun calculateDimensions(font: BitmapFont?, text: String?, width: Float): Pair<Float, Float> {
        if (font == null || text.isNullOrEmpty()) return 0f to 0f
        val layout = GlyphLayout()
        layout.setText(font, text, Color.WHITE, width, Align.left, true)
        return layout.width to layout.height
    }

    fun calculateMaxDimensions(
        font: BitmapFont?,
        text: List<String?>,
        width: Float
    ): Pair<Float, Float> {
        var maxW = 0f
        var maxH = 0f
        for (line in text) {
            val dims = calculateDimensions(font, line, width)
            maxW = max(maxW, dims.first)
            maxH += dims.second
        }
        return maxW to maxH
    }

    fun drawHP(a: Actor?, x: Float, y: Float, w: Float, h: Float, hp: Float) {
        val barColor = if (hp < 20) {
            Color.RED
        } else if (hp < 40) {
            Color.ORANGE
        } else if (hp < 60) {
            Color.YELLOW
        } else {
            Color.WHITE
        }

        val adjustedWidth = w * hp / 100
        drawRectangle(x, y, w, h, Compass.CENTER, Color.BLACK)
        drawRectangle(x - w / 2, y, adjustedWidth, h, Compass.EAST, barColor)
        drawRectangleOutline(a, x, y, w, h, Compass.CENTER, Color.WHITE)
    }

    //vertices are calculated programatically
    fun getHexagon(radius: Float, x: Float, y: Float): Pair<FloatArray, ShortArray> {
        //6 vertices
        val vertices = FloatArray(12)
        for (i in 0..5) {
            val angle = Math.PI * (.5 + i / 3.0)
            vertices[2 * i] = x + (radius * cos(angle)).toFloat()
            vertices[2 * i + 1] = y + (radius * sin(angle)).toFloat()
        }

        //4 triangles
        val triangles = ShortArray(12)
        triangles[0] = 0
        triangles[1] = 1
        triangles[2] = 2
        triangles[3] = 0
        triangles[4] = 2
        triangles[5] = 3
        triangles[6] = 0
        triangles[7] = 3
        triangles[8] = 5
        triangles[9] = 3
        triangles[10] = 4
        triangles[11] = 5

        return vertices to triangles
    }

    fun getHexagonRegion(
        x: Float,
        y: Float,
        xOff: Float,
        yOff: Float,
        radius: Float,
        texture: TextureRegion
    ): PolygonRegion {
        val texW = (2 * radius).toInt()
        val texH = (sqrt(3.0) * radius).toInt()

        texture.texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        val region = TextureRegion(texture, xOff.toInt(), -yOff.toInt(), texW, texH)
        return getHexagonRegion(x, y, radius, region)
    }


    fun getHexagonRegion(x: Float, y: Float, scale: Float, region: TextureRegion?): PolygonRegion {
        val data = getHexagon(x, y, scale)
        return PolygonRegion(region, data.first, data.second)
    }

    fun randomColor(r: Random, saturated: Boolean): Color {
        val hue = r.nextFloat() * 360
        val sat = if (saturated) 1f else r.nextFloat()
        val lum = r.nextFloat()
        return Color.WHITE.cpy().fromHsv(hue, sat, lum)
    }
}
