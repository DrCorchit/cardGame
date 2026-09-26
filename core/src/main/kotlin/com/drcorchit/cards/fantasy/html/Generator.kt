package com.drcorchit.cards.fantasy.html

import com.drcorchit.cards.utils.html.*
import com.drcorchit.justice.utils.IOUtils
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.Utils
import com.drcorchit.justice.utils.logging.Logger
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files


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

    val appendices by lazy {
        listOf<HtmlFile>()
    }

    val chapters by lazy {
        listOf<HtmlFile>(
            CardDatabase("Avalon"),
            CardDatabase("Metropolis"),
            CardDatabase("Thalassa"),
            CardDatabase("Transylvania"),
            CardDatabase("Vulcania"),
            CardDatabase("Unaffiliated")
        )
    }

    private val files by lazy { (appendices + chapters).associateBy { it.fileName } }

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

    fun makeChapter(i: Int) {
        val nav = Navigation.forChapter(i)
        chapters[i - 1].appendHeader()
            //.appendElement("h2", "Chapter $i")
            .appendTitle().append(nav)
            .appendBody().append(nav)
            .appendScripts()
            .save()
    }

    fun makeAppendix(i: Int) {
        val prev = if (i > 0) {
            appendices[i - 1]
        } else null
        val next = if (i < appendices.size - 1) {
            appendices[i + 1]
        } else null
        val nav = Navigation(
            prev?.let { it.title to it.fileName },
            next?.let { it.title to it.fileName })

        appendices[i]
            .appendHeader()
            .appendTitle().append(nav)
            .appendBody().append(nav)
            .appendScripts()
            .save()
    }

    fun generate() {
        copyFiles()

        Index.appendHeader()
            .appendTitle("h1")
            .appendBody()
            .save(File(Server.serviceDir, "index.html"))

        Rules.appendHeader()
            .appendTitle("h1")
            .appendBody()
            .save(File(Server.serviceDir, "rules.html"))

        ToC.appendHeader()
            .appendElement("h1", "Wizard Wars")
            .appendTitle("h2")
            .appendBody()
            .save()

        for (i in 1..chapters.size) makeChapter(i)
        for (i in appendices.indices) makeAppendix(i)
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
