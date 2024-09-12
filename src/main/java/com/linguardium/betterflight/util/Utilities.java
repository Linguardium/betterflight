package com.linguardium.betterflight.util;

import net.minecraft.util.Identifier;

import static com.linguardium.betterflight.BetterFlight.MODID;

public class Utilities {
    public static Identifier id(String path) {
        return Identifier.of(MODID,path);
    }
}
