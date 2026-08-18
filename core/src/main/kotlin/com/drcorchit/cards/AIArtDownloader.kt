package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.drcorchit.cards.AIUtils.createImage
import com.drcorchit.cards.AIUtils.createImageForFantasyCard
import com.drcorchit.cards.AIUtils.customPrompts
import com.drcorchit.cards.AIUtils.model
import com.drcorchit.cards.AIUtils.uniqueFile
import com.drcorchit.cards.AIUtils.runs
import com.drcorchit.cards.AIUtils.skipExistingCards
import com.drcorchit.cards.AIUtils.style
import com.drcorchit.cards.fantasy.FantasyCards
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.space.SpaceCard
import com.drcorchit.cards.space.SpaceCards
import com.drcorchit.justice.utils.StringUtils.normalize
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


class AIArtDownloader : ApplicationAdapter() {

    override fun create() {
        Draw.batch
        LocalAssets.getInstance().load()
        //downloadFantasyCardArts()
        //downloadSpaceCardArts()
        //downloadRobots()
        downloadWizards()
        dispose()
    }

    override fun dispose() {
        Gdx.app.exit()
    }

    fun downloadWizards() {
        val corePrompt = "Create an image of %s Use landscape orientation. Do not include any text on the image."

        val wizardPrompts = mapOf(
            "Alice the Wonder" to "a witch named \"Alice the Wonder\". She wears a green dress with a plunging V neckline and a long and tight fitting satin skirt. Her dress has a subtle hexagonal pattern. She has black hair with piercing green eyes, and strikes an assertive and active pose, shooting magic from her bare hands towards the camera. In the background is a mysterious green palace interior.",
            "Bertcalf the Tall" to "a wizard named \"Bertcalf the Tall\" He wears luxurious turquoise robes with gold trim and a tall green wizard hat with gold stars. He has wispy white eyebrows and a beard, and a slightly nefarious expression on his face. Give him white gloves and a golden wizard staff. The background features a desk cluttered with trinkets and a stack of gold coins.",
            "Lutis the Mad" to "a wizard named \"Lutis the Mad\", the crazed archmage who downed a fifth of vodka and disappeared into the Abyss forever. Give the image an edgy and serious tone, but make Lutis a younger man with wild white hair.",
            "Newtonia the White" to "a witch named \"Newtonia the White\". She wears a white hat and a short frilly white dress with a plunging neckline. She has pale white skin and piercingly intelligent yellow eyes, and wields a gnarled white staff. The background features a subtle checkerboard pattern of black and white.",
            "Octicia the Cunning" to "a witch named \"Octicia the Cunning\". She wears a black witch hat and a tight fitting and well worn leather dress. She has gray skin and piercing purple eyes, with magic crackling from the fingers of her left hand. She strikes a seductive pose and octopus tentacles sprout from her back ominously. The background features a bleak gray fortress perched on a cliff.",
            "Rhodango the Red" to "a wizard named \"Rhodango the Red\". He wears red and burgundy robes and a crimson pointed wizard hat. He has a full curly red beard and red hair, and a slightly angry expression on his face. Give him black gloves and a gnarled brown wizard staff. The background features a dense and foggy autumn forest.",
            "Samray the Swift" to "a wizard named \"Samray the Swift\". He wears light blue and white satin robes and has jet black hair, with a large scar running down one side of his face over his eye. He has a devious expression on his face, blue eyes, and a silver pendant dangles from his left hand. The background features a large placid lake seen through dense fog.",
            "Takulev the Undying" to "a wizard named \"Takulev the Undying\". He wears a dark black robe and has gray skin with piercing yellow eyes. The background is a gray and misty graveyard scene.",
            "Vizimir the Golden" to "a wizard named \"Vizimir the Golden\". He wears ornate black robes with gold trim, a black turban, and black gloves. He has a black mustache and dark skin with yellow eyes, and a boldly confident look on his face. He holds a crystal ball aloft in one of his hands. The background is comprised of a dark purple satin curtain."
        )

        repeat(1) {
            wizardPrompts.mapValues { String.format(corePrompt, it.value) }
                .forEach {
                    val prompt = it.value
                    //val file = uniqueFile("assets/images/wizards/${it.key}", "png")
                    //createImage(prompt, file, true)
                    println(prompt)
                }
        }
    }

    fun downloadRobots() {
        val locations = listOf("cyberpunk dance studio",
            "cyberpunk alley",
            "cyberpunk mall")

        val corePrompt =
            "generate an image of a female cyborg %s. Make her look feminine, with her entire body being made of mechanical parts except for her head. Make sure her entire figure is visible, set against a cyberpunk skyline. make her pose slightly suggestive."
        val robotPrompts =
            mapOf(
                "rose" to "made of polished red metal and a black rubber accordion joint around the midsection, and dark red hair",
                "flamme" to "made of polished orange metal, with a look vaguely reminiscent of an orange and black muscle car, and short black hair",
                "fuschia" to "made of glittery pink metal, with a look vaguely reminiscent of a ballerina in a leotard, and long blonde hair",
                "violet" to "made of polished purple metal, with a black pattern reminiscent of fishnets on her legs, and long wavy purple hair",
                "lotus" to "made of polished white metal with black seams running between plates, and platinum blonde hair with bangs",
                "noir" to "made of polished black and gray worn metal, with a subtle goth look",
                "sapphire" to "made of gleaming blue metal, with small airplane wings sprouting from her ankles and forearms. Give her a subtle jet engine intake valve on her chest, and wavy blonde hair",
                "viridia" to "made of polished green metal, with black corrugated tubing running along her thighs in a manner reminiscent of musculature, and medium length wavy brown hair"
            )

        repeat(1) {
            robotPrompts.mapValues { String.format(corePrompt, it.value) }
                .forEach {
                    val prompt = it.value
                    val file = uniqueFile("assets/images/robots/${it.key}", "png")
                    //createImage(prompt, file, true)
                    println(prompt)
                }
        }
    }

    fun downloadSpaceCardArts() {
        val cards = SpaceCards.cards
            .filter { it.type == SpaceCard.Type.Weapon }
            .forEach {
                AIUtils.createImageForSpaceCard(it, AIUtils.AIStyle.Realistic)
            }
    }

    fun downloadFantasyCardArts() {
        val cards = cards.filter {
            val canonicalFile =
                File("resources/images/fantasy_cards/cards/${model.name}/${style.name}/${it.city.name}/${it.name.normalize()}.png")
            if (skipExistingCards && canonicalFile.exists()) {
                println("Art for ${it.name} already exists; skipping.")
                false
            } else true
        }.let { ConcurrentLinkedQueue(it) }

        val threadCount = AtomicInteger(0)

        println("Starting thread pool executor; using model $model for style ${style.name}")
        val executor = Executors.newFixedThreadPool(model.concurrency)
        cards.map {
            Runnable {
                threadCount.incrementAndGet()
                for (i in 1..runs) {
                    println("Downloading card ${it.name} $i/$runs")
                    createImageForFantasyCard(it, style)
                }
                println("thread_count: ${threadCount.decrementAndGet()}")
            }
        }.forEach { executor.execute(it) }
        executor.awaitTermination(12, TimeUnit.HOURS)
        println("I did whatever I was supposed to do.")
    }

    companion object {
        val cards by lazy { FantasyCards.expac1.cards }

        @JvmStatic
        fun main(args: Array<String>) {
            generateCustomCards()
        }

        fun generateCustomCards() {
            val executor = Executors.newFixedThreadPool(model.concurrency)

            customPrompts.map { (key, value) ->
                Runnable {
                    for (i in 1..runs) {
                        createImage(value, uniqueFile("resources/images/fantasy_cards/cards/ChatGPT/Realistic/$key"), false)
                    }
                }
            }.forEach { executor.execute(it) }
        }

        fun generateStatusArts() {
            val statuses = mapOf(
                "stalwart" to "castle",
                "protector" to "shield",
                "invisible" to "eye",
                "immune" to "forbidden",
                "locked" to "lock",
                "poisoned" to "drop",
                "doomed" to "skull",
                "bounty" to "scroll"
            )

            val executor = Executors.newFixedThreadPool(model.concurrency)
            statuses.map { (key, value) ->
                Runnable {
                    for (i in 1..runs) {
                        val prompt =
                            "Give me an icon for the status effect \"$key\". The effect should include the \"$key\" label in a fantasy font, centered below a $value icon."
                        createImage(
                            prompt,
                            uniqueFile("assets/images/fantasy_cards/statuses/fancy/$key"),
                            false
                        )
                        println("progress: $i/$runs")
                    }
                }
            }.forEach { executor.execute(it) }
        }
    }
}
