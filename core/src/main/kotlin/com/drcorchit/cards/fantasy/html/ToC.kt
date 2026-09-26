package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject

object ToC : HtmlFile("New Player's Guide", "toc.html", generator.inputDir) {

	override fun appendBody(): HtmlFile {
		appendElement(
			"p",
			"This guide is designed for players new to Wizard Wars."
		)
		val list = HtmlObject("ol").withAll(
			generator.chapters.map { chapter ->
				HtmlObject("li").withContent(chapter.linkTo())
					.withContent(
						HtmlObject("ol").withAttribute("type", "i")
							.withAll(chapter.subsections.map { subsection ->
								HtmlObject("li").withContent(subsection.linkTo())
							})
					)
			}
		)

		val appendices = HtmlObject("li").withContent("Appendices")
			.withContent(
				HtmlObject("ol").withAttribute("type", "i")
					.withAll(generator.appendices.map { HtmlObject("li").withContent(it.linkTo()) })
			)
		list.withContent(appendices)
		append(list)
		return this
	}
}
