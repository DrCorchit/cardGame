package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.City.Companion.cities
import com.drcorchit.cards.fantasy.City.Companion.unaffiliated
import com.drcorchit.cards.fantasy.Leader.Companion.leaders
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject
import com.drcorchit.justice.utils.StringUtils.normalize

object FAQs : HtmlFile("FAQs", "faqs.html") {
    override fun appendBody(): HtmlFile {
        super.appendBody()

        val cityList = HtmlObject("ul")
        cities.values
            .filter { it != unaffiliated }
            .forEach { city ->
            val li = HtmlObject("li")
            li.withBoldedEntry(city.name, city.description)
            li.withContent("{{files.db.${city.name}}}")
            li.withContent("{{files.lore.${city.name}}}")
            cityList.withContent(li)
        }

        val leaderList = HtmlObject("ul")
        leaders.values.forEach { leader ->
            val li = HtmlObject("li")
            li.withBoldedEntry(leader.name, leader.description)
            li.withContent("{{files.lore.${leader.name.normalize()}}}")
            leaderList.withContent(li)
        }

        append(cityList)
        append(leaderList)
        return this
    }
}
