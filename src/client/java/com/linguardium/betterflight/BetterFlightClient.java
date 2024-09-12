package com.linguardium.betterflight;

import com.linguardium.betterflight.networking.ClientNetworkHandler;
import com.rejahtavi.betterflight.client.events.ClientEvents;
import com.rejahtavi.betterflight.client.gui.ClassicHudOverlay;
import com.rejahtavi.betterflight.client.gui.StaminaHudOverlay;
import net.fabricmc.api.ClientModInitializer;

public class BetterFlightClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientEvents.onKeyRegister();
		ClientEvents.init();
		ClientNetworkHandler.registerPacketHandlers();
		ClassicHudOverlay.registerOverlay();
		StaminaHudOverlay.registerOverlay();
		ClientEvents.registerEventListeners();
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}