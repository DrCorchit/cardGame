package com.drcorchit.cards.utils.html

class HtmlString(val content: String): Renderable {

    override fun render(): String {
        return content
    }

    override fun toString(): String {
        return content
    }
}
