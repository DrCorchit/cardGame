package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject
import com.drcorchit.justice.utils.StringUtils.normalize

object LoreToC : HtmlFile("Lore Database", "lore.html") {

    val loreMap = (generator.leaderLoreEntries + generator.factionLoreEntries)
        .mapNotNull { it.inputFile }
        .associateBy { it.nameWithoutExtension.normalize() }

    override fun getProperty(property: String): Any? {
        return loreMap[property.normalize()]
    }

    override fun appendBody(): HtmlFile {
        super.appendBody()

        fun loreToEntry(lore: HtmlFile): HtmlObject {
            return HtmlObject("li").withContent(lore.linkTo())
                .withContent(
                    HtmlObject("ol").withAttribute("type", "i")
                        .withAll(lore.subsections.map { subsection ->
                            HtmlObject("li").withContent(subsection.linkTo())
                        })
                )
        }

        val list1 = HtmlObject("ol").withAll(
            generator.leaderLoreEntries.map { loreToEntry(it) }
        )

        val list2 = HtmlObject("ol").withAll(
            generator.factionLoreEntries.map { loreToEntry(it) }
        )

        append(HtmlObject("h4").withContent("Leader Stories"))
        append(list1)
        append(HtmlObject("h4").withContent("Faction Timelines"))
        append(list2)
        return this
    }
}
