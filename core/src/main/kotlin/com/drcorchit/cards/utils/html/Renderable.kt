package com.drcorchit.cards.utils.html

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

interface Renderable {

    fun render(): String

    fun wrap(tag: String): HtmlObject {
        return HtmlObject(tag).withContent(this)
    }

    fun asContent(): HtmlContent {
        return HtmlContent().withContent(this)
    }

    companion object {
        val wrapInParagraph: (JsonPrimitive) -> Renderable = {
            HtmlObject("p").withContent(it.asString)
        }

        val leaveAsString: (JsonPrimitive) -> Renderable = {
            HtmlString(it.asString)
        }

        fun deserialize(
            ele: JsonElement,
            rule: (JsonPrimitive) -> Renderable = leaveAsString
        ): Renderable {
            return if (ele.isJsonObject) {
                HtmlObject.deserialize(ele.asJsonObject)
            } else if (ele.isJsonArray) {
                ele.asJsonArray
                    .fold(HtmlContent()) { html, e -> html.withContent(deserialize(e, rule)) }
            } else {
                rule.invoke(ele.asJsonPrimitive)
            }
        }
    }
}
