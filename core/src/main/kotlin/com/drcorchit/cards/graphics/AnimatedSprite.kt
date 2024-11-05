package com.drcorchit.cards.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.drcorchit.justice.utils.math.Compass
import java.util.*

/**
 * Handles locally-stored sprites possibly having multiple frames/animation
 * Implements BaseDrawable but sets borders to 0
 */
class AnimatedSprite : BaseDrawable {
    var speed: Double
    var index: Double
    var xOffset: Float
    var yOffset: Float
    var blend: Color
    private val frames: SpriteSheet

    //If true, the sprite frame will be updated whenever a draw() method is called.
    var isSelfAnimated: Boolean
        private set

    constructor(frames: SpriteSheet) : this(frames, Color.WHITE.cpy())

    constructor(frames: SpriteSheet, blend: Color) {
        this.frames = frames
        speed = if (frames.frameCount < 2) 0.0 else 1.0
        index = 0.0
        this.blend = blend.cpy()
        xOffset = 0f
        yOffset = 0f
        isSelfAnimated = false
        resetDims()
    }

    private constructor(other: AnimatedSprite) {
        this.frames = other.frames
        speed = other.speed
        index = 0.0
        this.blend = other.blend.cpy()
        this.xOffset = other.xOffset
        this.yOffset = other.yOffset
        isSelfAnimated = false
        resetDims()
    }

    fun copy(): AnimatedSprite {
        return AnimatedSprite(this)
    }

    fun resetDims() {
        leftWidth = 0f
        rightWidth = 0f
        topHeight = 0f
        bottomHeight = 0f
        if (size() == 0) {
            minWidth = 0f
            minHeight = 0f
        } else {
            minWidth = frames.width.toFloat()
            minHeight = frames.height.toFloat()
        }
    }

    fun draw(batch: Batch, x: Float, y: Float, rotation: Float) {
        draw(batch, x, y, xOffset, yOffset, minWidth, minHeight, 1f, 1f, rotation)
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        if (!canDraw()) return
        val xOff: Float = xOffset * (width / frames.width)
        val yOff: Float = yOffset * (height / frames.height)

        draw(batch, x, y, xOff, yOff, width, height, 1f, 1f, 0f)
    }

    fun draw(batch: Batch, x: Float, y: Float, scaleX: Float, scaleY: Float, rotation: Float) {
        draw(batch, x, y, xOffset, yOffset, minWidth, minHeight, scaleX, scaleY, rotation)
    }

    fun draw(
        batch: Batch,
        x: Float,
        y: Float,
        originX: Float = xOffset,
        originY: Float = yOffset,
        width: Float = minWidth,
        height: Float = minHeight,
        scaleX: Float = 1f,
        scaleY: Float = 1f,
        rotation: Float = 0f
    ) {
        if (!canDraw()) return
        val temp = batch.color.cpy()
        batch.color = blend
        batch.draw(
            currentFrame,
            x - originX,
            y - originY,
            originX,
            originY,
            width,
            height,
            scaleX,
            scaleY,
            rotation
        )
        batch.color = temp
        if (isSelfAnimated) updateFrame()
    }

    fun setOffset(xOffset: Float, yOffset: Float): AnimatedSprite {
        this.xOffset = xOffset
        this.yOffset = yOffset
        return this
    }

    fun setOffset(position: Compass): AnimatedSprite {
        return setOffset(
            frames.width * position.percentHoriz,
            frames.height * position.percentVert
        )
    }

    fun size(): Int {
        return frames.frameCount
    }

    fun setAlpha(alpha: Float) {
        blend.a = alpha
    }

    fun setSelfAnimated(selfAnimated: Boolean): AnimatedSprite {
        this.isSelfAnimated = selfAnimated
        return this
    }

    fun getFrames(): SpriteSheet {
        return frames
    }

    fun canDraw(): Boolean {
        return (getFrames().isLoaded && size() > 0)
    }

    val currentFrame: TextureRegion
        get() = frames.get(index)

    val currentTexture: TextureRegionDrawable
        get() = TextureRegionDrawable(frames.get(index))

    fun updateFrame() {
        index += speed
        if (index >= frames.frameCount) index -= frames.frameCount
    }

    fun randomizeFrame(): AnimatedSprite {
        if (frames.frameCount < 2) return this
        index = Random().nextInt(frames.frameCount).toDouble()
        return this
    }

    override fun toString(): String {
        return "com.drcorchit.cards.graphics.AnimatedSprite:$frames"
    }
}
