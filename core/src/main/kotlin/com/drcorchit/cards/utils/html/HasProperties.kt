package com.drcorchit.cards.utils.html

import com.drcorchit.justice.utils.StringUtils.normalize

interface HasProperties {
	fun lookup(keys: List<String>, name: String?): String {
		if (keys.isEmpty()) {
			throw IllegalStateException("No key to lookup!")
		}

		val key = keys.first().normalize()
		val value = getProperty(key)
			?: throw IllegalStateException("No property named \"$key\" found in $this")

		return if (keys.size == 1) {
			convertToString(value, name)
		} else if (value is HasProperties) {
			val rest = keys.drop(1)
			value.lookup(rest, name)
		} else {
			throw IllegalArgumentException("value $")
		}
	}

	fun getProperty(property: String): Any?

	companion object {
		fun convertToString(temp: Any, name: String?): String {
			return when (temp) {
				is HtmlFile -> temp.linkTo(name ?: temp.title).render()
				is Subsection -> temp.linkTo(name ?: temp.title).render()
				else -> temp.toString()
			}
		}
	}
}
