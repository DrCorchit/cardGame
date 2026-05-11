package com.drcorchit.cards.cyberpunk


import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.drcorchit.cards.LocalAssets
import com.drcorchit.cards.fantasy.FantasyCards
import com.drcorchit.cards.graphics.CardActor
import com.drcorchit.cards.graphics.Draw
import com.drcorchit.cards.space.SpaceCard2LargeWindow.Companion.cardbacks
import com.drcorchit.cards.space.SpaceCard2LargeWindow.Companion.disasters
import com.drcorchit.cards.space.SpaceCards
import com.drcorchit.justice.utils.logging.Logger
import com.drcorchit.justice.utils.math.MathUtils
import java.io.File

/**
 * [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.
 */
class DisplayPlacards : ApplicationAdapter() {
    var index = 0
    val stage by lazy { Stage() }
    val placards by lazy { Placard.bots }
    val card get() = placards[index]
    val actor by lazy { CardActor(card) }

    override fun create() {
        //Load the batch
        Draw.batch
        LocalAssets.getInstance().load()
        stage.addActor(actor)

        card.updateGraphic()
    }

    override fun resize(width: Int, height: Int) {
        Gdx.app.graphics.setWindowedMode(width, height)
        //println("$width x $height")
    }

    override fun render() {
        fun advanceBy(amount: Int) {
            index = MathUtils.modulus(index + amount, placards.size)
            card.updateGraphic()
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) advanceBy(-1)
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) advanceBy(1)

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) actor.drawable.updateGraphic()

        actor.drawable = placards[index]

        Draw.batch.begin()
        stage.draw()
        Draw.batch.end()

    }

    override fun dispose() {
        Draw.batch.dispose()
    }
}

