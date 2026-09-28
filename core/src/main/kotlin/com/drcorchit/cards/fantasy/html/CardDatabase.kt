package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.cards.utils.html.HtmlFile
import com.drcorchit.cards.utils.html.HtmlObject
import java.io.File
import java.util.*

class CardDatabase(val factionName: String) :
    HtmlFile("$factionName Faction Cards", "$factionName.html", generator.inputDir) {

    val imageFolder = File(generator.inputDir, "images/cards/$factionName")

    val cards = imageFolder.listFiles { file -> file.extension == "png" }!!

    override fun appendBody(): HtmlFile {
        cards.forEach {
            val html = HtmlObject(
                "img", TreeMap(
                    mapOf(
                        "src" to "/images/cards/$factionName/${it.name}",
                        "alt" to it.nameWithoutExtension,
                        "class" to "databaseImage"
                    )
                )
            )
            body.withContent(html)
        }
        return this
    }
}
