package com.linguardium.betterflight.networking.packets;

import com.rejahtavi.betterflight.common.FlightActionType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import static com.linguardium.betterflight.BetterFlight.MODID;

/**
 * Client->Server packet, keeps server up to date when a client flaps an elytra
 */
public record CTSFlightEffectsPacket(FlightActionType flightUpdate) implements FabricPacket
{
    public static final PacketType<CTSFlightEffectsPacket> TYPE = PacketType.create(Identifier.of(MODID,"flight_effects"), CTSFlightEffectsPacket::new);

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Override
    public void write(PacketByteBuf buffer)
    {
        buffer.writeEnumConstant(this.flightUpdate);
    }
    public CTSFlightEffectsPacket(PacketByteBuf buffer)
    {
        this(buffer.readEnumConstant(FlightActionType.class));
    }

}

