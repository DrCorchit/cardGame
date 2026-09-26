package com.drcorchit.cards.utils.html

import java.io.File

abstract class Subsection(val parent: HtmlFile, val title: String, val link: String): Renderable {

	fun linkTo(value: String = title): HtmlObject {
		return HtmlObject("a")
			.withAttribute("href", "${parent.fileName}#$link")
			.withContent(value)
	}

	fun makeHeader() : HtmlObject {
		return HtmlObject("h4")
			.withContent(HtmlObject("a").withAttribute("id", link))
			.withContent(title)
	}
}

class FileSubsection(parent: HtmlFile, title: String, link: String, val prefix: String): Subsection(parent, title, link) {
	override fun render(): String {
		return File(parent.inputDir, "${prefix}_$link.html").readText()
	}
}
