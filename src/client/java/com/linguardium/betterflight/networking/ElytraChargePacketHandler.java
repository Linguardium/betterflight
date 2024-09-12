package com.linguardium.betterflight.networking;

import com.linguardium.betterflight.networking.packets.STCElytraChargePacket;
import com.rejahtavi.betterflight.client.util.InputHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;

public class ElytraChargePacketHandler implements ClientPlayNetworking.PlayPacketHandler<STCElytraChargePacket> {

    @Override
    public void receive(STCElytraChargePacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        InputHandler.charge = packet.charge();
    }
}
