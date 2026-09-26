package com.drcorchit.cards.utils.html
import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.justice.utils.StringUtils.normalize
import com.drcorchit.justice.utils.logging.Logger
import org.jsoup.Jsoup
import java.io.File

abstract class HtmlFile(val title: String, val fileName: String, val inputDir: File) :
	HasProperties {
	open val logger = Logger.getLogger(HtmlFile::class.java)
	val outputFile = File(generator.versionedOutputDir, fileName)
	open val templatizer: Templatizer = generator.templatizer

	val subsections = mutableListOf<Subsection>()
	val head = HtmlObject("head").withContent(HtmlObject("title").withContent(title))
	val body = HtmlObject("body")
	open val scripts = listOf<String>()

	fun linkTo(text: String = title): HtmlObject {
		return HtmlObject("a").withAttribute("href", fileName)
			.withContent(text)
	}

	fun addSubsection(title: String, link: String = title.normalize()): HtmlFile {
		val index = subsections.size + 1
		subsections.add(FileSubsection(this, title, link, index.toString()))
		return this
	}

	fun getOutline(): HtmlObject {
		val list = HtmlObject("ol")
		subsections.forEach { list.withContent(HtmlObject("li").withContent(it.linkTo())) }
		return list
	}

	fun appendSubsection(subsection: Subsection) {
		append(subsection.makeHeader())
		append(subsection)
	}

	fun append(renderable: Renderable): HtmlFile {
		body.withContent(renderable)
		return this
	}

	fun append(string: String): HtmlFile {
		body.withContent(string)
		return this
	}

	fun appendElement(tag: String, text: String): HtmlFile {
		return append(HtmlObject(tag).withContent(text))
	}

	fun appendTitle(tag: String = "h3"): HtmlFile {
		return appendElement(tag, title)
	}

	open fun appendHeader(): HtmlFile {
		head.withContent(Header)
		return this
	}

	open fun appendBody(): HtmlFile {
		if (subsections.isEmpty()) {
			append(File(inputDir, fileName).readText())
		} else {
			//append(getOutline())
			subsections.forEach { appendSubsection(it) }
		}
		return this
	}

	fun appendScripts(): HtmlFile {
		if (scripts.isNotEmpty()) {
			val script = HtmlObject("script")
				.withAll(scripts.map { JSFile(it) })
			append(script)
		}
		return this
	}

	fun render(): String {
		logger.debug("Rendering $fileName")
		return "<!DOCTYPE html>\n" + HtmlObject("html")
			.withAttribute("lang", "en")
			.withContent(head)
			.withContent(body)
			.render()
			.let {
				try {
					templatizer.replace(it)
				} catch (e: Exception) {
					throw IllegalArgumentException("Error templatizing file: $outputFile", e)
				}
			}
	}

	fun save(outputFile: File = this.outputFile) {
		val content = render()
		val parsed = Jsoup.parse(content)
		val pretty = parsed.toString()

		outputFile.parentFile.mkdirs()
		val new = outputFile.createNewFile()
		outputFile.writeText(pretty)
		if (new) logger.debug("Created file $outputFile")
		else logger.debug("Wrote file $outputFile")
	}

	override fun getProperty(property: String): Any? {
		return when (property) {
			"title" -> title
			"sections" -> object : HasProperties {
				override fun getProperty(property: String): Any {
					return subsections[property.toInt()]
				}
			}

			else -> null
		}
	}

	override fun toString(): String {
		return "$inputDir/$fileName -> $title"
	}
}
