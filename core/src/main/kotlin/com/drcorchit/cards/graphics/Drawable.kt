package com.drcorchit.cards.graphics

interface Drawable {
    val name: String
    fun draw()
    fun updateGraphic(): AnimatedSprite?
    val outputLocation: String
}
