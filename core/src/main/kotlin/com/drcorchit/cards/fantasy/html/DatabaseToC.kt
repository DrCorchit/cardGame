package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.utils.html.HasProperties
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject
import com.drcorchit.justice.utils.StringUtils.normalize

object DatabaseToC : HtmlFile("Card Database", "database.html"), HasProperties {

    val dbMap = (generator.dbList)
        .associateBy { it.inputFile.nameWithoutExtension.normalize() }

    override fun getProperty(property: String): Any? {
        return dbMap[property.normalize()]
    }

	override fun appendBody(): HtmlFile {
		val list = HtmlObject("ol").withAll(
			generator.dbList.map { database ->
				HtmlObject("li").withContent(database.linkTo())
					.withContent(
						HtmlObject("ol").withAttribute("type", "i")
							.withAll(database.subsections.map { subsection ->
								HtmlObject("li").withContent(subsection.linkTo())
							})
					)
			}
		)

		append(list)
		return this
	}
}
