package com.drcorchit.cards.fantasy

enum class Race {
    Human, Elf, Dwarf,

    Fairy, Dryad, Nymph, Treant, Beast, Insectoid,

    Dragon, Ogroid, Vampire, Undead, Monster,

    Machine, Ship;

    companion object {
        fun detectRacialTag(tags: List<String>, cardType: CardType): String {
            return when (cardType) {
                CardType.Unit -> Race.entries.firstOrNull { tags.contains(it.name) }?.name ?: "Unit"
                else -> cardType.name
            }
        }
    }
}
