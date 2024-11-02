import com.drcorchit.justice.utils.json.JsonUtils.prettyPrint
import com.drcorchit.justice.utils.json.JsonUtils.toJsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.io.File

class Script {


    companion object {
        val regex =
            Regex("(?<name>.*): (?<type>\\w+), ((?<power>\\d+)/)?(?<cost>\\d+)p, (?<tags>\\w+(/\\w+)*)\\. (?<ability>.*)")

        @JvmStatic
        fun main(vararg args: String) {
            val input = File("cards.txt")
            val output = File("cards.json")
            val json = input.readLines().filter { it.isNotBlank() }.map { parse(it) }.toJsonArray()
            output.writeText(json.prettyPrint())
        }

        fun parse(string: String): JsonObject {
            val groups = regex.matchEntire(string)!!.groups

            val name = groups["name"]!!.value
            val type = groups["type"]!!.value
            val power = groups["power"]?.value?.toInt() ?: 0
            val cost = groups["cost"]!!.value.toInt()
            val tags = groups["tags"]!!.value.split("/")
            val ability = groups["ability"]!!.value
            val rarity = if (cost > 4) "rare" else "common"

            val json = JsonObject()
            json.addProperty("name", name)
            json.addProperty("type", type)
            if (power > 0) json.addProperty("power", power)
            json.addProperty("cost", cost)
            json.addProperty("rarity", rarity)
            json.add("tags", tags.map { JsonPrimitive(it) }.toJsonArray())
            json.addProperty("ability", ability)
            return json

        }
    }
}
