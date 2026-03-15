package com.drcorchit.cards.graphics

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class CardActor(var drawable: Drawable): Actor() {
    override fun draw(batch: Batch?, parentAlpha: Float) {
        drawable.draw()
    }
}
