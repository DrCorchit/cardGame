package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.drcorchit.cards.fantasy.City
import com.drcorchit.cards.fantasy.FantasyCard
import com.drcorchit.cards.fantasy.Race
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.justice.utils.StringUtils.normalize
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.images.Image
import com.openai.models.images.ImageGenerateParams
import org.bouncycastle.util.encoders.Base64
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random

class AIArtDownloader : ApplicationAdapter() {

    val cards by lazy { Main.cards }
    val skipExistingCards = true

    fun downloadCardArts(style: AIStyle) {
        println("Sending requests...")
        cards.filter {
            val canonicalFile =
                File("assets/images/fantasy_cards/ai/$style/${it.city.name}/${it.name.normalize()}.png")
            if (skipExistingCards && canonicalFile.exists()) {
                println("Art for ${it.name} already exists; skipping.")
                false
            } else true
        }.forEach { createImageForCard(it, style) }
        println("I did whatever I was supposed to do.")
    }

    override fun create() {
        Draw.batch
        LocalAssets.getInstance().load()

        for (i in 1..4) {
            downloadCardArts(AIStyle.Realistic)
        }

        dispose()
    }

    override fun dispose() {
        Gdx.app.exit()
    }

    companion object {

        val apiKey = File("api_key_do_not_commit.txt").readLines().first()
        val client = OpenAIOkHttpClient.builder().apiKey(apiKey).build()
        val style = AIStyle.Realistic

        @JvmStatic
        fun main(args: Array<String>) {
            val runs = 4
            val prompt1 =
                "Give me a photorealistic image of a dragon perched on a mountaintop with his wings spread imposingly."
            val prompt2 =
                "Give me a photorealistic image of a skeletal pirate. Avoid having ships in the background."
            val prompt3 =
                "Give me a realistic image of two dragons feasting on a carcass, but minimize gore."
            val prompt4 =
                "Give me a photorealistic image of a drinking contest between two dwarves with a fight in the background."

            for (i in 1..runs) {
                createImage(prompt4, uniqueFile("assets/images/fantasy_cards/ai/temp"))
                println("progress: $i/$runs")
            }
        }

        enum class AIStyle {
            Realistic, RealisticColored,
            Digital, DigitalColored,
            HandDrawn, HandDrawnColored
        }

        fun makePrompt(card: FantasyCard, style: AIStyle): String {
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
                City.Thalassa -> "The card is for the Thalassa faction, which involves sailors on the open seas. Avoid having ships in the background."
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

                AIStyle.Digital -> "I'm making art for a game which uses a realistic digital art style." +
                    "Please make a realistic image for a card named \"${card.name}\". $raceText $factionText" +
                    "Do not include any card labels. Avoid excessive visual clutter in the background."

                AIStyle.DigitalColored -> "I'm making art for a game which uses a realistic digital art style." +
                    "Please make a realistic image for a card named \"${card.name}\". $raceText $factionText" +
                    "Do not include any card labels. Avoid excessive visual clutter in the background. $colorText"

                else -> throw IllegalArgumentException("AIStyle not supported: $style")
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

        fun createImage(prompt: String, file: File) {
            val params = ImageGenerateParams.builder()
                .size(ImageGenerateParams.Size._1536X1024)
                .prompt(prompt)
                .model("gpt-image-1.5")
                .build()

            val response = client.images().generate(params).data().get()
            response.forEach { saveImageToFile(it, file) }
        }

        fun createImageForCard(card: FantasyCard, style: AIStyle) {
            try {
                print("Getting art for ${card.name}...")
                val prompt = makePrompt(card, style)
                val file = uniqueFile(
                    "assets/images/fantasy_cards/ai/${style.name}/${card.city.name}/${card.name.normalize()}",
                    "png"
                )
                createImage(prompt, file)
                println(" Done!")
            } catch (e: Exception) {
                println(" Error: $e")
            }
        }
    }
}
