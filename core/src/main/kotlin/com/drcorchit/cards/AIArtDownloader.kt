package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.drcorchit.cards.fantasy.City
import com.drcorchit.cards.fantasy.FantasyCard
import com.drcorchit.cards.fantasy.FantasyCards
import com.drcorchit.cards.fantasy.Race
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.justice.utils.StringUtils.normalize
import com.google.genai.Client
import com.google.genai.types.Blob
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.GenerateContentResponse
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.images.Image
import com.openai.models.images.ImageGenerateParams
import org.bouncycastle.util.encoders.Base64
import java.io.ByteArrayInputStream
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import kotlin.random.Random


class AIArtDownloader : ApplicationAdapter() {

    val cards by lazy { FantasyCards.expac1.cards }

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

    override fun create() {
        Draw.batch
        LocalAssets.getInstance().load()
        downloadCardArts()
        dispose()
    }

    override fun dispose() {
        Gdx.app.exit()
    }

    companion object {
        val chatGptApiKey = File("chat_gpt_api_key.txt").readLines().first()
        val chatGptClient = OpenAIOkHttpClient.builder().apiKey(chatGptApiKey).build()
        val geminiApiKey = File("gemini_api_key.txt").readLines().first()
        val geminiClient = Client.Builder().apiKey(geminiApiKey).build()

        val model = Model.Gemini
        val style = AIStyle.Realistic
        val runs = 5
        val skipExistingCards = true

        @JvmStatic
        fun main(args: Array<String>) {
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

            println("Proceeding with $model and in a ${style.description} style")
            statuses.forEach { key, value ->
                for (i in 1..runs) {
                    val prompt =
                        "Give me an icon for the status effect \"$key\". The effect should include the \"$key\" label in a fantasy font, centered below a $value icon."
                    createImage(prompt, uniqueFile("assets/images/fantasy_cards/statuses/fancy"))
                    println("progress: $i/$runs")
                }
            }
        }

        enum class Model(val model: String, val concurrency: Int) {
            Gemini("gemini-3-pro-image-preview", 1),
            ChatGPT("gpt-image-1.5", 4);

            override fun toString(): String {
                return "$name model=$model concurrency=$concurrency"
            }
        }

        enum class AIStyle(val description: String) {
            Realistic("photorealistic"),
            RealisticColored("colorful photorealistic"),
            Digital("digital art"),
            DigitalColored("colorful digital art"),
            HandDrawn("hand drawn"),
            HandDrawnColored("colorful hand drawn"),
            Cartoonish("cartoonish"),
            CartoonishColored("colorful cartoonish")
        }

        val customPrompts = mapOf(
            "neromir_warrior_dragon" to "Give me a ${style.description} image of a dragon perched on a mountaintop with his wings spread imposingly.",
            "davy_jones" to "Give me a ${style.description} image of a skeletal pirate. Avoid having ships in the background.",
            "scaly_feast" to "Give me a realistic image of two dragons feasting on a carcass, but minimize gore.",
            "drinking_contest" to "Give me a ${style.description} image of a drinking contest between two dwarves with a fight in the background.",
            "skeleton_key" to "Give me a ${style.description} image of an old key laying atop a cluttered desk.",
            "treasure_of_all_treasures" to "Give me a ${style.description} image of a stone hallway filled with gold",
            "fugg_raulner" to "Give me a ${style.description} image of an imposing male automaton made of iron.",
            "meezurk_arisen" to "Give me a ${style.description} image of a donkey with dragon wings.",
            "battering_ram" to "Give me a ${style.description} image of a battering ram.",
            "snort" to "Give me a ${style.description} image of a black dragon exhaling smoke from his nostrils.",
            "volcanic_eruption" to "Give me a ${style.description} image of a large volcano spewing flames and smoke, surrouneded by dark rocky terrain.",
            "makeshift_bomb" to "Give me a ${style.description} image of a crude shrapnel bomb laying on a workman's table.",
            "bloody_slash" to "Give me a ${style.description} image of a dragon slashing wildly with a foreclaw, with blood trailing from the claws.",
            "zark_used_axe_salesman" to "Give me a ${style.description} image of a half-orc with a fez dressed in a shabby suit.",
            "gold_rush" to "Give me a ${style.description} image of three dwarves prospecting for gold, with one dwarf gleefully holding up a gold nugget.",
            "myla" to "Give me a ${style.description} image of a beardless female dwarf wearing mining equipment with a pickaxe slung across her shoulders."
        )

        fun makePrompt(card: FantasyCard, style: AIStyle): String {
            val temp = customPrompts[card.name.normalize()]
            if (temp != null) {
                println("Using custom prompt for card $card")
                return temp
            }

            val raceText = when (Race.detectRacialTag(card.tags, card.type)) {
                "Beast" -> "The image must feature an animal."
                "Deity" -> "The image must focus on a caucasian human."
                "Dragon" -> "The image must feature a dragon."
                "Dwarf" -> "The image must feature a swarthy dwarf."
                "Elf" -> "The image must feature an elf."
                "Fairy" -> "The image must feature a fairy."
                "Human" -> "The image must focus on a caucasian human."
                "Insectoid" -> "The image must feature a insect."
                "Machine" -> "The image must focus on a steampunk era machine."
                "Monster" -> "" //deliberately omitted
                "Nymph" -> "The image must feature a female dryad with green skin."
                "Ogroid" -> "" //deliberately omitted
                "Ship" -> "The image must focus on a wooden ship."
                "Treant" -> "The image must feature a slender treant."
                "Undead" -> "" //deliberately omitted
                "Vampire" -> "The image must feature a vampire."
                else -> ""
            }

            val factionText = when (card.city) {
                City.Avalon -> "The card is for the Avalon faction, which is set in large, quiet, and lonely temperate forest."
                City.Metropolis -> "The card is for the Metropolis faction, set in a steampunk city or industrial rooms."
                City.Transylvania -> "The card is for the Transylvania faction, which includes a mix of humans and vampires living in a crime-ridden victorian era city."
                //City.Thalassa -> "The card is for the Thalassa faction, which involves sailors on the open seas. Avoid having ships in the background."
                City.Thalassa -> "The card is for the Thalassa faction, which involves viking warriors."
                City.Vulcania -> "The card is for the Vulcania faction, which is set in deep dark caverns."
                City.Unaffiliated -> ""
            }

            val color = when (Random.Default.nextInt(10)) {
                0 -> "dark"
                1 -> "white"
                2 -> "red"
                3 -> "blue"
                4 -> "green"
                5 -> "yellow"
                6 -> "teal"
                7 -> "tan"
                8 -> "brown"
                9 -> "gray"
                else -> ""
            }

            val colorText = "Allow subtle $color hues to predominate."

            return when (style) {
                AIStyle.Realistic -> "I'm making art for a game which uses a realistic art style." +
                    "Please make a photorealistic image for a card named \"${card.name}\". $raceText $factionText" +
                    "Do not include any card labels. Avoid excessive visual clutter in the background."

                AIStyle.RealisticColored -> "I'm making art for a game which uses a realistic art style." +
                    "Please make a photorealistic image for a card named \"${card.name}\". $raceText $factionText" +
                    "Do not include any card labels. Avoid excessive visual clutter in the background. $colorText"

                AIStyle.Digital -> "I'm making art for a game which uses a digital art style." +
                    "Please make art for a card named \"${card.name}\". $raceText $factionText" +
                    "Do not include any card labels. Avoid excessive visual clutter in the background."

                AIStyle.DigitalColored -> "I'm making art for a game which uses a digital art style." +
                    "Please make art for a card named \"${card.name}\". $raceText $factionText" +
                    "Do not include any card labels. Avoid excessive visual clutter in the background. $colorText"

                AIStyle.HandDrawn -> "I'm making art for a game which uses a hand drawn art style." +
                    "Please make art for a card named \"${card.name}\". $raceText $factionText" +
                    "Do not include any card labels. Avoid excessive visual clutter in the background."

                AIStyle.HandDrawnColored -> "I'm making art for a game which uses a hand drawn art style." +
                    "Please make art for a card named \"${card.name}\". $raceText $factionText" +
                    "Do not include any card labels. Avoid excessive visual clutter in the background. $colorText"

                AIStyle.Cartoonish -> "I'm making art for a game which uses a cartoonish but realistic art style." +
                    "Please make art for a card named \"${card.name}\". $raceText $factionText" +
                    "Do not include any card labels. Avoid excessive visual clutter in the background."

                AIStyle.CartoonishColored -> "I'm making art for a game which uses a cartoonish but realistic art style." +
                    "Please make art for a card named \"${card.name}\". $raceText $factionText" +
                    "Do not include any card labels. Avoid excessive visual clutter in the background. $colorText"
            }
        }

        fun uniqueFile(baseFileName: String, ext: String = "png"): File {
            var index = 1
            var file: File
            do file = File("${baseFileName}_${index++}.$ext") while (file.exists())
            return file
        }

        fun saveImageToFile(image: Image, file: File) {
            file.parentFile.mkdirs()
            val base64 = image.b64Json().get()
            val bytes = Base64.decode(base64)
            val png = ImageIO.read(ByteArrayInputStream(bytes));
            ImageIO.write(png, "png", file)
        }

        fun saveImageToFile(blob: Blob, file: File) {
            file.parentFile.mkdirs()
            file.writeBytes(blob.data().get())
        }

        fun createImage(prompt: String, file: File) {

            when (model) {
                Model.Gemini -> {
                    val config = GenerateContentConfig.builder()
                        .responseModalities("IMAGE")
                        .build()

                    val response: GenerateContentResponse = geminiClient.models.generateContent(
                        model.model,
                        "Create a picture of a nano banana dish in a fancy restaurant with a Gemini theme",
                        config
                    )

                    response.parts()!!.first { it.inlineData().isPresent }.let { part ->
                        val blob = part.inlineData().get()
                        if (blob.data().isPresent) {
                            saveImageToFile(blob, file)
                        }
                    }
                }

                Model.ChatGPT -> {
                    val params = ImageGenerateParams.builder()
                        .size(ImageGenerateParams.Size._1536X1024)
                        .prompt(prompt)
                        .model(model.model)
                        .build()

                    val response = chatGptClient.images().generate(params).data().get()
                    response.forEach { saveImageToFile(it, file) }
                }
            }
        }

        fun createImageForCard(card: FantasyCard, style: AIStyle) {
            try {
                val prompt = makePrompt(card, style)
                val file = uniqueFile(
                    "assets/images/fantasy_cards/cards/${model.name}/${style.name}/${card.city.name}/${card.name.normalize()}",
                    "png"
                )
                createImage(prompt, file)
            } catch (e: Exception) {
                println("Error downloading card ${card.name} ($e)")
            }
        }
    }
}
