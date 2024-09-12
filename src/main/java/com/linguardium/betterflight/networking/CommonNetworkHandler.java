package com.linguardium.betterflight.networking;

import com.linguardium.betterflight.networking.packets.CTSFlightEffectsPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CommonNetworkHandler {
    public static void registerPacketHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(CTSFlightEffectsPacket.TYPE,new FlightEffectsPacketHandler());
    }
}
