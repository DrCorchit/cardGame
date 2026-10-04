package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject
import java.io.File

object DatabaseToC : HtmlFile(
    "Card Database", null,
    File(generator.outputDir, "database.html")
) {

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
