package com.drcorchit.cards.fantasy

enum class Race {
    Human, Elf, Dwarf,

    Fairy, Dryad, Nymph, Treant, Beast, Insect,

    Dragon, Vampire, Undead, Demon,

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
