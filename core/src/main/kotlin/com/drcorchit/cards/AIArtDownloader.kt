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
        dispose()
    }

    override fun dispose() {
        Gdx.app.exit()
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
                File("assets/images/fantasy_cards/cards/${model.name}/${style.name}/${it.city.name}/${it.name.normalize()}.png")
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
                        createImage(value, uniqueFile("assets/images/fantasy_cards/cards/ChatGPT/Realistic/$key"), false)
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
