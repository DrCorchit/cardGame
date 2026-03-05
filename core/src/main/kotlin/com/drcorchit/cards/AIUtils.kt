package com.drcorchit.cards

import com.drcorchit.cards.fantasy.City
import com.drcorchit.cards.fantasy.FantasyCard
import com.drcorchit.cards.fantasy.Race
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
import javax.imageio.ImageIO
import kotlin.random.Random

object AIUtils {

    val chatGptApiKey = File("chat_gpt_api_key.txt").readLines().first()
    val chatGptClient = OpenAIOkHttpClient.builder().apiKey(chatGptApiKey).build()
    val geminiApiKey = File("gemini_api_key.txt").readLines().first()
    val geminiClient = Client.Builder().apiKey(geminiApiKey).build()
    val geminiConfig = GenerateContentConfig.builder()
        .responseModalities("IMAGE")
        .build()

    val model = Model.ChatGPT
    val style = AIStyle.Realistic
    val runs = 3
    val skipExistingCards = true

    enum class Model(val model: String, val concurrency: Int) {
        GeminiNanoBananaPro("gemini-3-pro-image-preview", 10),
        GeminiNanoBanana2("gemini-3.1-flash-image-preview", 1),
        ChatGPT("gpt-image-1.5", 2);

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

    val prefix = "Give me a ${style.description} image of"
    val customPrompts = mapOf(
        "arondight" to "$prefix a sword being held aloft from the hilt by a caucasian female, with a large lake in the background. The scene is shown from a low camera angle.",
        "dryad_ranger" to "$prefix a green-skinned dryad clad in leaves, crouching in the undergrowth. She wields a bow threateningly.",
        "elven_archer" to "$prefix a male elven archer, standing with his bow at the ready atop a craggy hillside.",
        "fairy_ring" to "$prefix a ring of mushrooms surrounding a dancing fairy, seen from a low camera angle.",
        "hydriad" to "$prefix a blue-skinned nymph who swims happily beneath the waters of a small pond. Let the camera angle be underwater, pointing slightly upwards toward the surface of the water from below.",
        "tree_elf" to "$prefix a female elf perched on a tree branch, wearing barely any clothing or carrying any gear.",
        "undine" to "$prefix an elven woman with pale blue skin, clad in a leaf bikini while wading through shallow water. She faces the camera with a suggestive smile.",

        "codex_veritas" to "$prefix a large open book laying on a black marble table, atop a purple cloth and surrounded by white and yellow particle effects.",
        "eriathorn_gemini" to "$prefix a male elf wielding green magic, wearing green robes.",
        "plexi_glass" to "$prefix a sleek silver female robot. Make the background a brutalist industrial laboratory.",
        "regeneration_weave" to "$prefix a magical woven technology being used to heal an elf's wounded arm.",
        "shuramorn_gemini" to "$prefix a female elf wielding green magic, wearing green robes with a leather corset.",

        "admiral_boom" to "$prefix a courageous sea captain standing tall on his ship amidst a frightening thunderstorm. The camera views him from a low angle.",
        "blackbeard" to "$prefix blackbeard, but without any smoke or firecrackers in his beard.",
        "captain_flint" to "$prefix a pirate captain trying to steer his ship desperately during a torrential rainstorm.",
        "captain fribley" to "$prefix a pirate captain with a round face and a sneering expression smoking a pipe.",
        "davy_jones" to "$prefix a skeletal pirate. Avoid having ships in the background.",
        "davy_jones_locker" to "$prefix a decaying shipwreck on the bottom of the sea.",
        "howitzer" to "$prefix a large cannon on a sailing ship.",
        "keel_haul" to "$prefix an unfortunate sailor being keel-hauled, bound with ropes as he is dragged beneath the hull of a ship.",
        "long_john_silver" to "$prefix Long John Silver, the notorious pirate captain with a hard facial expression and a handcannon leveled threateningly at the camera.",
        "lower_the_boom" to "$prefix an large iron cannon being fired on the deck of a ship, with a sailor reacting to the explosion. Make sure the cannon is pointing away from the ship, toward the sea.",

        "casimir_blood_rival" to "$prefix Casimir, a victorian era occultist who wields a small magic hammer. Casimir is cleanshaven with long wavy hair, and wears a tunic.",
        "pogrom" to "$prefix a mob of angry humans with torches storming up to a barricaded victorian-era building.",
        "raj_blood_rival" to "$prefix Raj, a Victorian era occultist who wields a magic dagger. Raj wears a black turban and tunic, has dark skin, and a thin nose with a thin goatee.",

        "battering_ram" to "$prefix a battering ram. Make sure the ram is lifted up away from the chassis by chains.",
        "doco_the_paladin" to "$prefix a firbolg paladin with a large sword and a rubber duck hanging from his pack.",
        "fugg_raulner" to "$prefix an imposing male automaton made of iron.",
        "meezurk_arisen" to "$prefix a donkey with dragon wings.",
        "meteorite_strike" to "$prefix a meteor descending through the earth's atmosphere.",
        "mortar_bomb" to "$prefix a round black bomb with the fuse unlit, laying on a cluttered wooden workbench.",
        "multitool" to "$prefix a multitool from the medieval era, with no more than 4 attachments.",
        "queen_of_hearts" to "$prefix a cruel queen clad in a red dress decorated with white diamonds. She draws her finger across her neck, warning of an impending execution.",
        "skeleton_key" to "$prefix an old key laying atop a cluttered desk.",
        "zark_used_axe_salesman" to "$prefix a half-orc with a fez dressed in a shabby suit with an axe slung across his shoulders.",

        "bloody_slash" to "$prefix a dragon slashing wildly with a foreclaw, with blood trailing from the claws.",
        "drinking_contest" to "$prefix a drinking contest between two dwarves with a fight in the background.",
        "gold_rush" to "$prefix three dwarves prospecting for gold, with one dwarf gleefully holding up a gold nugget.",
        "myla" to "$prefix a beardless female dwarf wearing mining equipment with a pickaxe slung across her shoulders.",
        "neromir_warrior_dragon" to "$prefix a dragon perched on a mountaintop with his wings spread imposingly.",
        "scaly_feast" to "Give me a realistic image of two dragons feasting on a carcass, but minimize gore.",
        "snort" to "$prefix a black dragon exhaling smoke from his nostrils, with a vivid orange sunset in the background.",
        "treasure_of_all_treasures" to "$prefix a stone hallway filled with gold",
        "volcanic_eruption" to "$prefix a large volcano spewing flames and smoke, surrounded by dark rocky terrain.",
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
            "Human" -> "The image must focus on a human."
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
        println("model=${model} file=${file} prompt=$prompt")

        when (model) {
            Model.GeminiNanoBanana2, Model.GeminiNanoBananaPro -> {
                val response: GenerateContentResponse = geminiClient.models.generateContent(
                    model.model,
                    prompt,
                    geminiConfig
                )

                println("Got response: $response")

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
