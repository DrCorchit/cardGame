package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.fantasy.City
import com.drcorchit.cards.fantasy.City.Companion.cities
import com.drcorchit.cards.fantasy.Leader
import com.drcorchit.cards.fantasy.Leader.Companion.leaders
import com.drcorchit.cards.utils.html.*
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.logging.Logger
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.io.File
import java.io.FileNotFoundException


class Generator(val version: String, val inputDir: File, val outputDir: File) : HasProperties {
    val versionedOutputDir = File(outputDir, "version/$version")
    val imagesDir = File(outputDir, "images")
    var backgroundImage: String? = "/images/backdrop.png"

    val stringsFile = File(inputDir, "strings.json")

    val strings: Map<String, String> = stringsFile
        .readText()
        .let { JsonParser.parseString(it).asJsonObject }
        .entrySet().associate { it.key.normalize() to it.value.asString }

    val deserializer: Gson
    val templatizer = Templatizer.getDefault()
        .withRule(".*\\.html") {
            val file = File(inputDir, it.value.trim())
            if (file.exists()) {
                logger.info("  Inserting file: ${file.name}")
                file.readText()
            } else {
                throw FileNotFoundException("Substitution file not found: ${file.absolutePath}")
            }
        }

    val filesMap by lazy {
        mapOf("index" to Index, "rules" to Rules, "lore" to LoreToC, "db" to DatabaseToC, "faqs" to FAQs)
    }

    override fun getProperty(property: String): Any? {
        return when (property) {
            "version" -> version
            "strings" -> object : HasProperties {
                override fun getProperty(property: String): Any? {
                    return strings[property]
                }
            }
            "factions" -> City.Companion
            "leaders" -> Leader.Companion

            "files" -> object : HasProperties {
                override fun getProperty(property: String): Any? {
                    return filesMap[property.normalize()]
                }
            }

            else -> null
        }
    }

    init {
        val builder = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
        deserializer = builder.create()
    }


    val leaderLoreEntries by lazy {
        leaders.values.map {
            val inputFile = File(inputDir, "lore/leaders/${it.name.normalize()}.html")
            val outputFile = File(outputDir, "lore/leaders/${it.name.normalize()}.html")
            HtmlFile(it.name, inputFile, outputFile)
        }
    }

    val factions by lazy { listOf(City.avalon, City.metropolis, City.thalassa, City.transylvania, City.vulcania) }

    val factionLoreEntries by lazy {
        factions.map {
            val title = "A Thousand Years of ${it.name}"
            val inputOutputFile ="lore/factions/${it.name.normalize()}.html"
            HtmlFile(title, inputOutputFile)
        }
    }

    val loreList by lazy { leaderLoreEntries + factionLoreEntries }

    val dbList by lazy {
        City.cities.values.map { CardDatabase(it) }
    }

    fun copyFolder(src: File, dest: File) {
        if (src.isDirectory) {
            dest.mkdirs()
            src.listFiles()?.forEach {
                copyFolder(it, File(dest, it.name))
            }
        } else src.copyTo(dest, true)
    }

    fun copyFiles() {
        imagesDir.mkdir()
        copyFolder(File(inputDir, "images"), imagesDir)
        logger.info("Copied images")

        File(inputDir, "styles.css")
            .copyTo(File(Server.serviceDir, "styles.css"), true)
        logger.info("Copied styles.css")
    }

    fun makeCardDatabase(i: Int) {
        val prev = if (i > 0) {
            dbList[i - 1]
        } else dbList.last()
        val next = if (i < dbList.size - 1) {
            dbList[i + 1]
        } else dbList.first()
        val nav = Navigation(
            "Back to Database" to "/database.html",
            prev.let { it.title to it.outputRelativePath },
            next.let { it.title to it.outputRelativePath })

        dbList[i].appendHeader()
            //.appendElement("h2", "Chapter $i")
            .appendTitle().append(nav)
            .appendBody().append(nav)
            .appendScripts()
            .save()
    }

    fun makeLoreEntry(i: Int) {
        val prev = if (i > 0) {
            loreList[i - 1]
        } else loreList.last()
        val next = if (i < loreList.size - 1) {
            loreList[i + 1]
        } else loreList.first()
        val nav = Navigation(
            "Back to Lore" to "/lore.html",
            prev.let { it.title to it.outputRelativePath },
            next.let { it.title to it.outputRelativePath })

        loreList[i]
            .appendHeader()
            .appendTitle().append(nav)
            .appendBody().append(nav)
            .appendScripts()
            .save()
    }

    fun generate() {
        copyFiles()

        val returnToIndex = Navigation()

        Index
            .appendHeader()
            .appendTitle("h1")
            .appendBody()
            .save(File(Server.serviceDir, "index.html"))

        Rules.appendHeader()
            .appendTitle("h1")
            .append(returnToIndex)
            .appendBody()
            .append(returnToIndex)
            .save(File(Server.serviceDir, "rules.html"))

        LoreToC.appendHeader()
            .appendElement("h1", "Wizard Wars")
            .appendTitle("h2")
            .append(returnToIndex)
            .appendBody()
            .save()

        DatabaseToC.appendHeader()
            .appendElement("h1", "Wizard Wars")
            .appendTitle("h2")
            .append(returnToIndex)
            .appendBody()
            .save()

        FAQs.appendHeader()
            .appendTitle("h1")
            .append(returnToIndex)
            .appendBody()
            .append(returnToIndex)
            .save(File(Server.serviceDir, "faqs.html"))

        for (i in dbList.indices) makeCardDatabase(i)
        for (i in loreList.indices) makeLoreEntry(i)
    }

    override fun toString(): String {
        return "Generator $inputDir --> $outputDir"
    }

    companion object {
        val logger = Logger.getLogger(Generator::class.java)
        val generator = Generator("0", File("resources/html/input"), Server.serviceDir)

        @JvmStatic
        fun main(vararg args: String) {
            generator.generate()
        }
    }
}
