package com.drcorchit.cards

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.drcorchit.justice.utils.IOUtils.traverse
import com.drcorchit.justice.utils.logging.Logger
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

class LocalAssets {
    private val manager = AssetManager()
    private val localTextures = HashMap<String, Texture>()
    private val unusedTextures = HashMap<String, File>()
    private val localFonts = HashMap<String, BitmapFont>()

    //Method for loading the assets into the manager
    fun load() {
        val sprites = ArrayList<File>()
        artFolder.traverse(Consumer<File?> { file: File? ->
            if (file != null &&
                (file.getName().endsWith(".png")
                    || file.getName().endsWith(".jpg"))
            ) sprites.add(file)
        })

        for (sprite in sprites) {
            val key = sprite.name.lowercase(Locale.getDefault())
            val value = create(sprite.path)
            val oldTexture = localTextures.put(key, value)
            unusedTextures[key] = sprite
            if (oldTexture != null) {
                log.warn("File ${sprite.path} exists in multiple folders. One will be selected at random.")
            }

            log.debug("Loaded sprite from local resources: ${sprite.path}")
        }
    }

    //Base case. Gets a font or makes it if absent
    fun getOrMakeFont(
        name: String,
        relativePath: String,
        params: FreeTypeFontParameter
    ): BitmapFont? {
        val key = name.lowercase(Locale.getDefault())
        if (localFonts.containsKey(key)) {
            return localFonts[key]
        } else {
            val font = makeFont(relativePath, params)
            localFonts[key] = font
            return font
        }
    }

    private fun makeFont(relativePath: String, params: FreeTypeFontParameter): BitmapFont {
        val file = File(relativePath)
        val generator = FreeTypeFontGenerator(FileHandle(file))
        val value = generator.generateFont(params)
        generator.dispose()
        log.debug("Created font from file: " + file.path)
        return value
    }

    fun getTexture(name: String): Texture? {
        val key = name.lowercase(Locale.getDefault())
        unusedTextures.remove(key)
        val file = File(key)
        return localTextures[key]
    }

    //Easy asset disposing, whenever you are done with it just dispose the manager instead of many files.
    fun dispose() {
        manager.dispose()
        for (texture in localTextures.values) {
            texture.dispose()
        }
        for (font in localFonts.values) {
            font.dispose()
        }
    }

    companion object {
        private var instance: LocalAssets? = null

        private val log: Logger = Logger.getLogger(LocalAssets::class.java)

        @JvmStatic
        fun getInstance(): LocalAssets {
            if (instance == null) instance = LocalAssets()
            return instance!!
        }

        private val artFolder = File("assets/images")

        @JvmStatic
        fun showUnused() {
            val unused = instance!!.unusedTextures.values.stream()
                .map { obj: File -> obj.absolutePath }
                .filter { path: String ->
                    !path.contains(
                        "planets4k"
                    )
                }
                .filter { path: String ->
                    !path.endsWith(
                        "mouse.png"
                    )
                }
                .sorted { obj: String, anotherString: String? ->
                    obj.compareTo(
                        anotherString!!
                    )
                }
                .collect(Collectors.joining("\n"))
            log.info("Listing unused assets: \n$unused")
        }

        fun create(file: String?): Texture {
            val output = Texture(Gdx.files.internal(file), true)
            output.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.Linear)
            return output
        }

        fun create(px: Pixmap?): Texture {
            val output = Texture(px, true)
            output.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.Linear)
            return output
        }
    }
}
