package com.drcorchit.cards.cyberpunk

class StatBox(card: CyberpunkCard) {
    val name = card.name
    val hash = card.rizz * 1000 + card.chic * 100 + card.glam * 10 + card.pizzazz
    val desiredCount = let {
        val stats = card.stats.values.filter { it > 0 }
        if (stats.size == 1) 1
        else if (stats.size == 4) {
            if (stats[0] == 2) 2
            else 4
        } else if (stats.size == 2) {
            if (stats[0] == stats[1]) {
                if (stats[0] == 3) 1 else 2
            } else 1
        } else -1
    }

    override fun equals(other: Any?): Boolean {
        return other is StatBox && hash == other.hash
    }

    override fun hashCode(): Int {
        return hash
    }

    override fun toString(): String {
        return "$name: $hash"
    }

}
