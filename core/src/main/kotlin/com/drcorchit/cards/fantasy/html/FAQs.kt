package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.City.Companion.cities
import com.drcorchit.cards.fantasy.City.Companion.unaffiliated
import com.drcorchit.cards.fantasy.Leader.Companion.leaders
import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject
import com.drcorchit.cards.utils.html.HtmlObject.Companion.flexBox
import com.drcorchit.justice.utils.StringUtils.normalize

object FAQs : HtmlFile("FAQs", "faqs.html") {

    override val templatizer = generator.templatizer.extend()
        .withRule("factions") {
            val cityList = flexBox()
            cities.values
                .filter { it != unaffiliated }
                .forEach { city ->
                    val innerList = HtmlObject("ul")
                        .withContent(HtmlObject("li").withContent("{{factions.${city.name.normalize()}.db_link}}"))
                        .withContent(HtmlObject("li").withContent("{{factions.${city.name.normalize()}.lore_link}}"))

                    val div = HtmlObject("div").withClass("roundRect")
                    div.withBoldedEntry(city.name, city.description)
                    div.withContent(HtmlObject("p").withContent("Discover more:"))
                    div.withContent(innerList)

                    cityList.withContent(div)
                }
            cityList.render()
        }
            //TODO: fix transparent leader cards
        .withRule("leaders") {
            val leaderList = flexBox()
            leaders.values.forEach { leader ->
                val innerList = HtmlObject("ul")
                    .withContent(HtmlObject("li").withContent("{{leaders.${leader.name.normalize()}.db_link}}"))
                    .withContent(HtmlObject("li").withContent("{{leaders.${leader.name.normalize()}.lore_link}}"))

                val div = HtmlObject("div").withClass("roundRect")
                div.withBoldedEntry(leader.name, leader.description)
                div.withContent(HtmlObject("p").withContent("Discover more:"))
                div.withContent(innerList)

                leaderList.withContent(div)
            }
            leaderList.render()
        }
}
