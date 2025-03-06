package com.drcorchit.cards

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class CardActor(var card: Card): Actor() {
    override fun draw(batch: Batch?, parentAlpha: Float) {
        card.draw()
    }
}
