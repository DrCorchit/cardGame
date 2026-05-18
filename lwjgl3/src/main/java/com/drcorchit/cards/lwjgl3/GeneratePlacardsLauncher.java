package com.drcorchit.cards.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.drcorchit.cards.GenerateCards;

public class GeneratePlacardsLauncher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new GenerateCards(), DisplayPlacardsLauncher.getDefaultConfiguration());
    }
}
