package com.linguardium.betterflight.networking;

import com.linguardium.betterflight.networking.packets.CTSFlightEffectsPacket;
import com.linguardium.betterflight.networking.packets.STCElytraChargePacket;
import com.rejahtavi.betterflight.common.FlightActionType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ClientFlightMessages
{

    public static void sendToServer(PlayerEntity player, FlightActionType action)
    {
        action.playSound(player);
        if (ClientPlayNetworking.canSend(CTSFlightEffectsPacket.TYPE)) {
            ClientPlayNetworking.send(new CTSFlightEffectsPacket(action));
        }
    }

}
