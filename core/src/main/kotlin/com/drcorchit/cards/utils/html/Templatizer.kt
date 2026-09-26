package com.drcorchit.cards.utils.html

import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import com.drcorchit.justice.utils.logging.Logger
import kotlin.text.get

val logger = Logger.getLogger(Templatizer::class.java)

class Templatizer private constructor(private val parent: Templatizer? = null) {
	val rules: MutableMap<Regex, (MatchResult) -> String> = mutableMapOf()

	fun withRule(regex: String, rule: (MatchResult) -> String): Templatizer {
		rules[regex.toRegex()] = rule
		return this
	}

	fun extend(): Templatizer {
		return Templatizer(this)
	}

	fun replace(template: String): String {
		return templateRegex.replace(template) { matchResult ->
			val value = matchResult.groupValues[1].trim()
			replace(attemptMatch(value))
		}
	}

	private fun attemptMatch(value: String): String {
		return rules.entries.firstNotNullOfOrNull { entry ->
			entry.key.matchEntire(value)?.let { match -> entry.value.invoke(match) }
		} ?: parent?.attemptMatch(value)
		?: throw UnsupportedOperationException("Unable to match \"$value\" to any rule")
	}

	companion object {
		val templateRegex = "\\{\\{(.*?)}}".toRegex()

		fun create(): Templatizer {
			return Templatizer()
		}

		fun getDefault(): Templatizer {
			return create()
				.withRule("(?<parts>\\w+(\\.\\w+)*)(#(?<name>.*))?") {
					val parts = it.groups["parts"]!!.value.split(".")
					val text = it.groups["name"]?.value
					val output = generator.lookup(parts, text)
					logger.info("${it.value} -> $output")
					output
				}
		}

	}
}
