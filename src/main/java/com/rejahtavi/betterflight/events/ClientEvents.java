package com.rejahtavi.betterflight.events;

import com.rejahtavi.betterflight.BetterFlight;
import com.rejahtavi.betterflight.client.HUDOverlay;
import com.rejahtavi.betterflight.client.Keybinding;
import com.rejahtavi.betterflight.common.BetterFlightCommonConfig;
import com.rejahtavi.betterflight.util.ActionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = BetterFlight.MODID, value = Dist.CLIENT)
public class ClientEvents {

    //INDEV
    static Logger logger = LogManager.getLogger(BetterFlight.MODID);

    // Player state
    public static boolean isElytraEquipped = false;
    private static boolean hasFlapped = false;
    public static boolean isFlaring = false;
    public static int offGroundTicks = 0;

    // elytra damage
    public static double elytraDurability = 0.5D;
    public static int elytraDurabilityLeft = 1;

    // timers
    public static int cooldown = 0;

    /**
     * default to full elytra meter on startup
     */
    public static void init() {
        ActionHandler.charge = BetterFlightCommonConfig.maxCharge;
    }

    @Mod.EventBusSubscriber(modid = BetterFlight.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(Keybinding.takeOffKey);
            event.register(Keybinding.flapKey);
            event.register(Keybinding.flareKey);
            event.register(Keybinding.widgetPosKey);
        }
    }

    // key event handling
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {

        Minecraft instance = Minecraft.getInstance();
        if (instance.player == null) return;

        //TODO This works as a check for close to ground. Might be worth adding some coyote time so the player can take off smoother
        //if (Keybinding.takeOffKey.isDown() && !instance.player.isFallFlying() && !checkForAir(instance.level, instance.player)) {
        if (Keybinding.takeOffKey.isDown() && !instance.player.isFallFlying()) {
            ActionHandler.tryTakeOff(instance.player);
            //hasFlapped = true;
        }
        if (Keybinding.flapKey.isDown() && instance.player.isFallFlying() && !hasFlapped) {
            ActionHandler.tryFlap(instance.player);
            hasFlapped = true;
        }

        if (event.getKey() == Keybinding.widgetPosKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
            HUDOverlay.cycleWidgetLocation();
        }

        //INDEV remove this later. Just trying to check scanner
//        if (Keybinding.flareKey.isDown()) {
//            logger.info("isAir: " + checkForAir(instance.player.level,instance.player));
//        }

    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        // track ground state for takeoff logic
        if (player.isOnGround()) {
            offGroundTicks = 0;
        }
        else {
            offGroundTicks++;
        }

        // decrement timers
        HUDOverlay.borderTick();
        if (cooldown > 0) cooldown--;

        ItemStack elytraStack = ActionHandler.findEquippedElytra(player);
        if(elytraStack != null)
        {
            isElytraEquipped = true;
            elytraDurabilityLeft = elytraStack.getMaxDamage() - elytraStack.getDamageValue();
            elytraDurability = (float) elytraStack.getDamageValue()/(float) elytraStack.getMaxDamage();
        }
        else { isElytraEquipped = false;}
        ActionHandler.handleRecharge(player);
        ActionHandler.tryFlare(player);

        if (!Keybinding.flapKey.isDown() || !Keybinding.takeOffKey.isDown() && hasFlapped) {
            hasFlapped = false;}
    }

    //region INDEV experimental blocks scanner

    //TODO Scan area around player for air
    //Referencing https://github.com/VentureCraftMods/MC-Gliders/blob/2a2df716fd47f312e0b1c0b593cb43437019f53e/common/src/main/java/net/venturecraft/gliders/util/GliderUtil.java#L183
    public static boolean checkForAir(Level world, LivingEntity player) {
        AABB boundingBox = player.getBoundingBox().move(0, -2, 0);
        // contract(2,5,2)
        // tp dev 432 75 -412
        // 430 74 -414
        // 432 71 -412
        //
        //contract(0,2,0) captures block at players feet and the block below.
        List<BlockState> blocks = world.getBlockStatesIfLoaded(boundingBox).toList();
        for(BlockState n : blocks)
            logger.debug(n);
        //Block.isShapeFullBlock();
        //TODO Exclude non-solid, non-cube blocks in the filter, like minecraft:grass and minecraft:torch
        Stream<BlockState> filteredBlocks = blocks.stream().filter(blockState -> !blockState.isAir());
        if (filteredBlocks.toList().isEmpty()) {
            return true;
        }
        return false;
    }

}