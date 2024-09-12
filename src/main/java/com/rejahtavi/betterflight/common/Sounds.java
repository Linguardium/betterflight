package com.rejahtavi.betterflight.common;


import com.linguardium.betterflight.BetterFlight;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import static com.linguardium.betterflight.BetterFlight.MODID;

public class Sounds
{

    //public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BetterFlight.MODID);

    public static final SoundEvent FLAP = createEvent("betterflight.flap");
    public static final SoundEvent BOOST = createEvent("betterflight.boost");

    private static SoundEvent createEvent(String sound)
    {
        Identifier id = Identifier.of(BetterFlight.MODID, sound);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id) );
    }
    public static void init() { }
}
