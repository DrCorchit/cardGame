package com.drcorchit.cards.fantasy

enum class Race {
    //Unit races
    Human,
    Elf,
    Dwarf,
    Fairy,
    Dryad,
    Nymph,
    Treant,
    Beast,
    Dragon,
    Vampire,
    Undead,
    Machine,
    Ship;


    companion object {
        fun detectRacialTag(tags: List<String>, cardType: CardType): String {
            return when (cardType) {
                CardType.Unit -> Race.entries.firstOrNull { tags.contains(it.name) }?.name ?: "Unit"
                else -> cardType.name
            }
        }
    }
}
