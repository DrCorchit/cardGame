package com.drcorchit.cards.utils.html

import com.drcorchit.cards.fantasy.html.Generator.Companion.generator
import java.io.File

object Header: Renderable {
    val header = File(generator.inputDir, "header.html").readText()

    override fun render(): String {
        return header
    }
}
