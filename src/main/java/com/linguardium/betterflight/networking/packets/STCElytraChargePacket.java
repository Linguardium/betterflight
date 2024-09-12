package com.linguardium.betterflight.networking.packets;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import static com.linguardium.betterflight.BetterFlight.MODID;

public record STCElytraChargePacket(int charge) implements FabricPacket
{

    public static final PacketType<STCElytraChargePacket> TYPE = PacketType.create(Identifier.of(MODID,"elytra_charge"), STCElytraChargePacket::new);

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }


    @Override
    public void write(PacketByteBuf buffer)
    {
        buffer.writeInt(this.charge);
    }

    public STCElytraChargePacket(PacketByteBuf buffer)
    {
        this(buffer.readInt());
    }

}


