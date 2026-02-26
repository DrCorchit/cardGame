package com.drcorchit.cards

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.drcorchit.cards.fantasy.City
import com.drcorchit.cards.fantasy.FantasyCard
import com.drcorchit.cards.graphics.Draw
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.images.ImageGenerateParams
import org.bouncycastle.util.encoders.Base64
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.jvm.optionals.getOrNull

class GenerateCardArtChatGPT : ApplicationAdapter() {

    val apiKey = File("api_key_do_not_commit.txt").readLines().first()
    val client = OpenAIOkHttpClient.builder().apiKey(apiKey).build()

    //val cards = Main.cards.map { it.name }
    val cards by lazy { Main.cards }

    fun makePrompt(card: FantasyCard): String {
        val faction = when (card.city) {
            City.Avalon -> "The card is for the Avalon faction, which is set in large, quiet, and lonely temperate forest."
            City.Metropolis -> "The card is for the Metropolis faction, set in an industrial steampunk city."
            City.Transylvania -> "The card is for the Transylvania faction, which includes a mix of seedy humans and vampires living in a decaying crime-ridden victorian era city."
            City.Thalassa -> "The card is for the Thalassa faction, which involves pirates sailing the open seas."
            City.Vulcania -> "The card is for the Vulcania faction, which is set in a large volcano."
            City.Unaffiliated -> ""
        }

        return "I'm making art for a game which uses a photorealistic art style. $faction Please make a photorealistic image for a card named \"${card.name}\"."
    }

    fun uniqueFileName(card: FantasyCard): File {
        val baseFileName = "assets/images/fantasy_cards/ai/${card.city.name}/${card.name}"
        var index = 1
        var file: File
        do file = File("$baseFileName ${index++}.png") while (file.exists())
        return file
    }

    override fun create() {
        Draw.batch
        LocalAssets.getInstance().load()

        println("Sending requests...")
        cards.forEach {
            val prompt = makePrompt(it)
            val params = ImageGenerateParams.builder()
                .size(ImageGenerateParams.Size._1536X1024)
                .prompt(prompt)
                .model("gpt-image-1.5")
                .build()

            try {
                val response = client.images().generate(params).data().get()

                response.mapNotNull { image -> image.b64Json().getOrNull() }
                    .map { base64 ->
                        val file = uniqueFileName(it)
                        file.parentFile.mkdirs()
                        val bytes = Base64.decode(base64)
                        val image = ImageIO.read(ByteArrayInputStream(bytes));
                        ImageIO.write(image, "png", file)
                    }
            } catch (e: Exception) {
                println("Could not generate art for ${it.name} due to an error")
                e.printStackTrace()
            }
        }
    }

    override fun render() {
        super.render()
        println("I did whatever I was supposed to do.")
        dispose()
    }

    override fun dispose() {
        Gdx.app.exit()
    }


}
