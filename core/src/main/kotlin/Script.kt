import com.badlogic.gdx.utils.Json
import com.google.gson.JsonObject
import java.io.File

class Script {


    companion object {
        val regex = Regex("(?<name>.*): (?<type>\\w+), ((?<power>\\d+)/)?(?<cost>\\d+)p, (?<tags>\\w+(/\\w+)*)\\. (?<ability>.*)")

        @JvmStatic
        fun main(vararg args: String) {
            val file = File("cards.txt")

            file.readLines().filter { it.isNotBlank() }.forEach { parse(it) }
        }

        fun parse(string: String) {
            try {
                val groups = regex.matchEntire(string)!!.groups

                val name = groups["name"]!!.value
                val type = groups["type"]!!.value
                val power = groups["power"]?.value?.toInt() ?: 0
                val cost = groups["cost"]!!.value.toInt()
                val tags = groups["tags"]!!.value.split("/")
                val ability = groups["ability"]!!.value

                val json = JsonObject()
                json.addProperty("name", name)
                json.addProperty("type", type)
                if (power > 0) json.addProperty("power", power)
                json.addProperty("cost", cost)
                //tags
                json.addProperty("ability", ability)

            } catch (e: Exception) {
                println("Could not parse $string")
            }
        }
    }
}
