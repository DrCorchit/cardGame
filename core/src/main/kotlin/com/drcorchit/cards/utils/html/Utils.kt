package com.drcorchit.cards.utils.html

import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File


fun String.bold(): String {
	return HtmlObject("b").withContent(this).render()
}

fun String.underline(): String {
	return HtmlObject("u").withContent(this).render()
}

fun String.italicise(): String {
	return HtmlObject("i").withContent(this).render()
}

fun String.capitalizeAll(): String {
	return this.replace(" [a-z]".toRegex()) { it.value.uppercase() }
}

fun toFullLink(link: String): String {
	return "https://all-that-glitters.net/version/${generator.version}/$link"
}

fun <T> File.deserialize(deserializer: (JsonObject) -> T): List<T> {
	return this.readText().let {
		try {
			JsonParser.parseString(it)
		} catch (e: Exception) {
			throw IllegalArgumentException("Error parsing file $this (invalid json)", e)
		}
	}.asJsonArray.map {
		try {
			deserializer.invoke(it.asJsonObject)
		} catch (e: Exception) {
			val name = it.asJsonObject.get("name")?.asString
			throw IllegalArgumentException("Error deserializing file $this: $name", e)
		}
	}
}

fun makeTooltip(text: String, tooltip: String): Renderable {
	return makeTooltip(text, HtmlString(tooltip))
}

fun makeTooltip(text: String, tooltip: Renderable): Renderable {
	return HtmlObject("a")
		.withClass("tooltip")
		.withContent(text.underline())
		.withContent(HtmlObject("span").withContent(tooltip))
}

fun <K2, V2, K1 : K2, V1 : V2> Map<K1, V1>.inherit(parent: Map<K2, V2>): Map<K2, V2> {
	return (this.keys + parent.keys).associateWith { this.getOrDefault(it, parent[it])!! }
}

