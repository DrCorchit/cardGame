package com.drcorchit.cards.utils.html

import com.drcorchit.cards.fantasy.html.Generator.Companion.generator

class Navigation(
	val prev: Pair<String, String>?,
	val next: Pair<String, String>?
) : Renderable {

	override fun render(): String {
		val output = HtmlContent()
		output.withContent(HorizontalRule)
		val div = HtmlObject("div")
			.withClass("row")
			.withStyle("justify-content:space-evenly; margin: 0")

		if (prev != null) {
			div.withContent(
				HtmlObject("div")
					.withClass("column-shrink")
					.withContent(
						HtmlObject("a")
							.withAttribute("href", prev.second)
							.withContent(prev.first)
					)
			)
		}

		div.withContent(
			HtmlObject("div")
				.withClass("column-shrink")
				.withContent(
					HtmlObject("a")
						.withAttribute("href", "phb_toc.html")
						.withContent("Back to the Table of Contents")
				)
		)

		if (next != null) {
			div.withContent(
				HtmlObject("div")
					.withClass("column-shrink")
					.withContent(
						HtmlObject("a")
							.withAttribute("href", next.second)
							.withContent(next.first)
					)
			)
		}
		output.withContent(div)
		output.withContent(HorizontalRule)
		return output.render()
	}

	companion object {
		fun forChapter(i: Int): Navigation {
			val max = generator.chapters.size
			val prev = if (i > 1) {
				"Retreat to Chapter ${i - 1}" to "c${i - 1}.html"
			} else null
			val next = if (i < max) {
				"Advance to Chapter ${i + 1}" to "c${i + 1}.html"
			} else null

			return Navigation(prev, next)
		}
	}
}
