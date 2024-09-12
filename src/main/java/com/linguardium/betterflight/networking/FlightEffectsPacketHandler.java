package com.linguardium.betterflight.networking;

import com.linguardium.betterflight.networking.packets.CTSFlightEffectsPacket;
import com.rejahtavi.betterflight.util.FlightHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class FlightEffectsPacketHandler implements ServerPlayNetworking.PlayPacketHandler<CTSFlightEffectsPacket> {

    @Override
    public void receive(CTSFlightEffectsPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        ServerWorld serverWorld = player.getServerWorld();
        if (serverWorld == null) return;
        packet.flightUpdate().playSound(player);
        switch (packet.flightUpdate())
        {
            case STOP:
                player.stopFallFlying();
                break;
            case RECHARGE:
                FlightHandler.handleFlightStaminaExhaustion(player);
                break;
        }
    }
}
