package com.linguardium.betterflight;

import com.linguardium.betterflight.networking.CommonNetworkHandler;
import com.rejahtavi.betterflight.common.FlightActionType;
import com.rejahtavi.betterflight.common.Sounds;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterFlight implements ModInitializer {
	public static final String MODID = "betterflight";
	public static final String MODNAME = "Better Flight";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MODNAME);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		CommonNetworkHandler.registerPacketHandlers();
		FlightActionType loadFAT = FlightActionType.BOOST;
		Sounds.init();
		LOGGER.info("Hello Fabric world!");
	}
}