package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.drcorchit.cards.AIUtils.createImage
import com.drcorchit.cards.AIUtils.createImageForCard
import com.drcorchit.cards.AIUtils.customPrompts
import com.drcorchit.cards.AIUtils.model
import com.drcorchit.cards.AIUtils.uniqueFile
import com.drcorchit.cards.AIUtils.runs
import com.drcorchit.cards.AIUtils.skipExistingCards
import com.drcorchit.cards.AIUtils.style
import com.drcorchit.cards.fantasy.FantasyCards
import com.drcorchit.cards.graphics.Draw
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
        downloadCardArts()
        dispose()
    }

    override fun dispose() {
        Gdx.app.exit()
    }

    fun downloadCardArts() {
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
                    createImageForCard(it, style)
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
                        createImage(value, uniqueFile("assets/images/fantasy_cards/cards/ChatGPT/Realistic/$key"))
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
                            uniqueFile("assets/images/fantasy_cards/statuses/fancy/$key")
                        )
                        println("progress: $i/$runs")
                    }
                }
            }.forEach { executor.execute(it) }
        }
    }
}
