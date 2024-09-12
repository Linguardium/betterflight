package com.linguardium.betterflight.networking;

import com.linguardium.betterflight.networking.packets.STCElytraChargePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworkHandler {
    public static void registerPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(STCElytraChargePacket.TYPE, new ElytraChargePacketHandler());
    }
}
