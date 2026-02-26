package com.drcorchit.cards.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.drcorchit.cards.GenerateCardArtChatGPT;
import com.drcorchit.cards.GenerateCards;
import com.drcorchit.cards.Main;

import static com.drcorchit.cards.Main.IMAGE_H;
import static com.drcorchit.cards.Main.IMAGE_W;

/** Launches the desktop (LWJGL3) application. */
public class GenerateCardArtChatGPTLauncher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new GenerateCardArtChatGPT(), DisplayCardsLauncher.getDefaultConfiguration());
    }
}
