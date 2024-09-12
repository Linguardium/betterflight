package com.linguardium.betterflight.mixin.client;

import com.mojang.authlib.GameProfile;
import com.rejahtavi.betterflight.client.events.ClientEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class PlayerTickMixin extends PlayerEntity {
    public PlayerTickMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method="tick", at=@At("HEAD"))
    private void playerTickEvent(CallbackInfo ci) {
        ClientEvents.onPlayerTick(this);
    }
}
