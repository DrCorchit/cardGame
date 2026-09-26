package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.Keyword
import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject

object Rules : HtmlFile("Wizard Wars Rules", "rules.html", generator.inputDir) {
    override fun appendBody(): Rules {
        super.appendBody()

        //appendElement("h2", "Keywords")
        appendElement("p", "Here is an exhaustive list of all official keywords used by the game:")

        val list = HtmlObject("ul")
        Keyword.keywordsList.forEach { keyword ->
            keyword.description?.let { list.withBoldedEntry(keyword.name, it) }
        }

        append(list)
        return Rules
    }
}
