package com.drcorchit.cards.cyberpunk

class StatBox(card: CyberpunkCard) {
    val name = card.name
    val hash = card.rizz * 1000 + card.chic * 100 + card.glam * 10 + card.panache


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
