package com.drcorchit.cards.utils

object Utils {

    fun String.replaceQuotes(): String {
        return this.replace("(?<!\\w)\"(?=\\w)".toRegex(), "“")
            .replace("\"", "”")
            .replace("(?<!\\w)'(?=\\w)".toRegex(), "‘")
            .replace("'", "’")
    }

}
