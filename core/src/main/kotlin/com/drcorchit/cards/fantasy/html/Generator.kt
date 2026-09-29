package com.drcorchit.cards.fantasy.html

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

    override fun getProperty(property: String): Any? {
        return when (property) {
            "version" -> version
            "strings" -> object : HasProperties {
                override fun getProperty(property: String): Any? {
                    return strings[property]
                }
            }

            "files" -> object : HasProperties {
                override fun getProperty(property: String): Any {
                    return lookupFile(property)
                }
            }

            else -> null
        }
    }

    init {

        val builder = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
        deserializer = builder.create()
    }

    val loreEntries by lazy {
        val loreDir = File(inputDir, "lore")
        loreDir.listFiles { it.extension == "html" }
            ?.mapNotNull { file ->
                HtmlFile(file.nameWithoutExtension, file, File(outputDir, "lore/${file.name}"))
            } ?: listOf()
    }

    val cardDatabases by lazy {
        listOf<HtmlFile>(
            CardDatabase("Avalon"),
            CardDatabase("Metropolis"),
            CardDatabase("Thalassa"),
            CardDatabase("Transylvania"),
            CardDatabase("Vulcania"),
            CardDatabase("Unaffiliated")
        )
    }

    private val files by lazy { (loreEntries + cardDatabases).associateBy { it.inputFile.name } }

    fun lookupFile(file: String): HtmlFile {
        val key = file.normalize() + ".html"
        return files[key] ?: throw NoSuchElementException("No file named \"$file.html\"")
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
            cardDatabases[i - 1]
        } else cardDatabases.last()
        val next = if (i < cardDatabases.size - 1) {
            cardDatabases[i + 1]
        } else cardDatabases.first()
        val nav = Navigation(
            "Back to Database" to "/database.html",
            prev.let { it.title to it.outputRelativePath },
            next.let { it.title to it.outputRelativePath })

        cardDatabases[i].appendHeader()
            //.appendElement("h2", "Chapter $i")
            .appendTitle().append(nav)
            .appendBody().append(nav)
            .appendScripts()
            .save()
    }

    fun makeLoreEntry(i: Int) {
        val prev = if (i > 0) {
            loreEntries[i - 1]
        } else null
        val next = if (i < loreEntries.size - 1) {
            loreEntries[i + 1]
        } else null
        val nav = Navigation(
            "Back to Lore" to "/lore.html",
            prev?.let { it.title to it.outputRelativePath },
            next?.let { it.title to it.outputRelativePath })

        loreEntries[i]
            .appendHeader()
            .appendTitle().append(nav)
            .appendBody().append(nav)
            .appendScripts()
            .save()
    }

    fun generate() {
        copyFiles()

        val returnToIndex = Navigation()

        Index.appendHeader()
            .appendTitle("h1")
            .appendBody()
            .save(File(Server.serviceDir, "index.html"))

        Rules.appendHeader()
            .appendTitle("h1")
            .append(returnToIndex)
            .appendBody()
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
            .save(File(Server.serviceDir, "faqs.html"))

        for (i in cardDatabases.indices) makeCardDatabase(i)
        for (i in loreEntries.indices) makeLoreEntry(i)
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
