package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject

object LoreToC : HtmlFile("Lore Database", "lore.html", generator.inputDir) {

    override fun appendBody(): HtmlFile {
        val list = HtmlObject("ol").withAll(
            generator.loreEntries.map { database ->
                HtmlObject("li").withContent(database.linkTo())
                    .withContent(
                        HtmlObject("ol").withAttribute("type", "i")
                            .withAll(database.subsections.map { subsection ->
                                HtmlObject("li").withContent(subsection.linkTo())
                            })
                    )
            }
        )

        val appendices = HtmlObject("li").withContent("Appendices")
            .withContent(
                HtmlObject("ol").withAttribute("type", "i")
                    .withAll(generator.loreEntries.map { HtmlObject("li").withContent(it.linkTo()) })
            )
        //list.withContent(appendices)
        append(list)
        return this
    }
}
