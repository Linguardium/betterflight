package com.rejahtavi.betterflight.client.events;


import com.linguardium.betterflight.BetterFlight;
import com.rejahtavi.betterflight.client.ClientData;
import com.rejahtavi.betterflight.client.Keybinding;
import com.rejahtavi.betterflight.client.gui.ClassicHudOverlay;
import com.rejahtavi.betterflight.client.util.InputHandler;
import com.rejahtavi.betterflight.common.BetterFlightCommonConfig;
import com.rejahtavi.betterflight.util.ElytraData;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class ClientEvents
{

    //INDEV
    public static Logger logger = LogManager.getLogger(BetterFlight.MODID);
    // Player state
    private static boolean wasFlapKeyDown = false;
    private static boolean wasToggleKeyDown = false;

    // elytra damage
    public static double elytraDurability = 0.5D;

    // timers
    private static final boolean isDebugButtonDown = false;

    /**
     * default to full elytra meter on startup
     */
    public static void init()
    {
        InputHandler.charge = BetterFlightCommonConfig.maxCharge;
    }
    public static void registerEventListeners() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::onClientEndTick);
        ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::onClientStartTick);
    }
    public static void onKeyRegister()
    {

        KeyBindingHelper.registerKeyBinding(Keybinding.toggleKey);
        KeyBindingHelper.registerKeyBinding(Keybinding.flapKey);
        KeyBindingHelper.registerKeyBinding(Keybinding.flareKey);
        KeyBindingHelper.registerKeyBinding(Keybinding.widgetPosKey);
    }

    // key event handling
    public static boolean onKeyInput(int keyCode, int scanCode, int modifiers, int action)
    {

        MinecraftClient instance = MinecraftClient.getInstance();
        PlayerEntity player = instance.player;
        if (player == null) return false;

        if (Keybinding.widgetPosKey.matchesKey(keyCode,scanCode) && action == GLFW.GLFW_PRESS)
        {
            ClassicHudOverlay.cycleWidgetLocation();
            return true;
        }
        return false;
    }

    //ticks when world is running
    public static void onPlayerTick(PlayerEntity player)
    {
        if (player.isRemoved()) return;
        InputHandler.handleRecharge(player);
    }

    public static void onClientEndTick(MinecraftClient client) {


    }
    public static void onClientStartTick(MinecraftClient mc)
    {

        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        //logger.info("Speed:" + player.getDeltaMovement().length());
        updateWingStatus(player);

        // track ground state for takeoff logic

        if (player.isOnGround())
        {
            ClientData.setOffGroundTicks(0);
        } else
        {
            ClientData.tickOffGround();
        }

        // decrement timers
        ClassicHudOverlay.borderTick();
        if (ClientData.getCooldown() > 0) ClientData.subCooldown(1);


        InputHandler.tryFlare(player);

//            while(Keybinding.flareKey.consumeClick())
//            {
//                if(!isDebugButtonDown)
//                {
//                    InputHandler.checkForAir(mc.level,player);
//                    isDebugButtonDown = true;
//                }
//            }


        if (Keybinding.flapKey.isPressed() && !wasFlapKeyDown)
        {
             if (ClientData.getCooldown() <= 0 && ClientData.isFlightEnabled())
            {
                if (BetterFlightCommonConfig.classicMode)
                {
                    InputHandler.classicFlight(player);
                } else InputHandler.modernFlight(player);
            }
            wasFlapKeyDown = true;
        }
        if (!Keybinding.flapKey.isPressed() && wasFlapKeyDown)
            wasFlapKeyDown = false;

        if (Keybinding.toggleKey.isPressed() && !wasToggleKeyDown)
        {
            ClientData.setFlightEnabled(!ClientData.isFlightEnabled());
            wasToggleKeyDown = true;
        }
        if (!Keybinding.toggleKey.isPressed() && wasToggleKeyDown)
        {
            wasToggleKeyDown = false;
        }
//            if (!Keybinding.flareKey.isDown()) {
//                isDebugButtonDown = false;
//            }
    }

    /**
     * Checks if player is wearing functional wings and updates status
     *
     * @param player to check and update
     */
    private static void updateWingStatus(ClientPlayerEntity player)
    {
        ElytraData elytraData = InputHandler.findWings(player);
        if (elytraData != null && elytraData.durabilityRemaining() > 1)
        {
            ClientData.setWingStatus(true);
            elytraDurability = elytraData.durabilityPercent();
        } else
        {
            ClientData.setWingStatus(false);
        }
    }

}
