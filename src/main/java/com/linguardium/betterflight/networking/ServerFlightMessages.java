package com.linguardium.betterflight.networking;

import com.linguardium.betterflight.networking.packets.STCElytraChargePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerFlightMessages {

    public static void sendToPlayer(ServerPlayerEntity player, int stamina)
    {
        if (ServerPlayNetworking.canSend(player,STCElytraChargePacket.TYPE)) {
            ServerPlayNetworking.send(player,new STCElytraChargePacket(stamina));
        }
    }
}
