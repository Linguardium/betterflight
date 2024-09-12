package com.rejahtavi.betterflight.common;

import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

// Simple enum used for CFlightUpdatePackets.
// Lets us send multiple messages with a single universal packet format.
public enum FlightActionType
{
    TAKEOFF(Sounds.FLAP,1F, 2F),
    RECHARGE,
    FLAP(Sounds.FLAP,0.5f, 2f),
    STOP,
    BOOST(Sounds.BOOST,2F, 1F);

    final SoundEvent sound;
    final float volume;
    final float pitch;
    FlightActionType() {
        this(null,0,0);
    }
    FlightActionType(@Nullable SoundEvent sound, float volume, float pitch) {
        this.sound=sound;
        this.volume=volume;
        this.pitch=pitch;
    }
    public void playSound(Entity entity) {
        World world = entity.getWorld();
        if (sound == null) return;
        if (world == null) return;
        world.playSound( entity, entity.getBlockPos(), sound,
                SoundCategory.PLAYERS, volume, pitch);
    }
}