package com.drcorchit.cards.utils.html

class Collapsible(buttonText: String) : Renderable {
	val button = HtmlObject("button")
		.withClass("collapsible")
		.withContent(buttonText)
	val innerDiv = HtmlObject("div")
		.withClass("content")
	val outerDiv = HtmlObject("div")
		.withClass("background-inner")
		.withContent(button).withContent(innerDiv)

	fun hasContent(): Boolean {
		return innerDiv.hasContent()
	}

	fun withContent(content: Renderable): Collapsible {
		innerDiv.withContent(content)
		return this
	}

	override fun render(): String {
		return outerDiv.render()
	}
}
