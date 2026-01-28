package com.drcorchit.cards.fantasy

enum class Race {
    //Standard races
    Human, Elf, Dwarf,

    //Forest races
    Fairy, Dryad, Nymph, Treant,

    //Other races
    Beast, Insectoid, Dragon, Ogroid, Deity,

    //Monster Races
    Vampire, Undead, Monster,

    //Nonliving races
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
