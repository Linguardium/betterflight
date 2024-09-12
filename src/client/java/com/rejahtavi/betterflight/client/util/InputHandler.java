package com.rejahtavi.betterflight.client.util;

import com.linguardium.betterflight.networking.ClientFlightMessages;
import com.rejahtavi.betterflight.client.ClientConfig;
import com.rejahtavi.betterflight.client.ClientData;
import com.rejahtavi.betterflight.client.Keybinding;
import com.rejahtavi.betterflight.client.gui.ClassicHudOverlay;
import com.rejahtavi.betterflight.client.gui.StaminaHudOverlay;
import com.rejahtavi.betterflight.common.BetterFlightCommonConfig;
import com.rejahtavi.betterflight.common.FlightActionType;
import com.rejahtavi.betterflight.util.ElytraData;
import com.rejahtavi.betterflight.util.FlightHandler;
import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class InputHandler
{
    private static int rechargeTickCounter = 0;
    private static int flareTickCounter = 0;
    public static int charge = BetterFlightCommonConfig.maxCharge;

    public static boolean classicFlight(PlayerEntity player)
    {
        if (canTakeOff(player))
            return classicTakeOff(player);
        else if (canFlap(player))
            return classicFlap(player);
        return false;
    }

    public static boolean modernFlight(PlayerEntity player)
    {
        if (canFlap(player))
        {
            if (spendCharge(player, BetterFlightCommonConfig.flapCost))
            {
                if (!checkForAir(player.getWorld(), player))
                {
                    FlightHandler.handleModernBoost(player);
                    ClientFlightMessages.sendToServer(player, FlightActionType.BOOST);
                } else
                {
                    FlightHandler.handleModernFlap(player);
                    ClientFlightMessages.sendToServer(player, FlightActionType.FLAP);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean canFlap(PlayerEntity player)
    {
        return ClientData.isWearingFunctionalWings() && !player.isOnGround() && player.isFallFlying();
    }

    private static boolean canTakeOff(PlayerEntity player)
    {
        return ClientData.isWearingFunctionalWings()
                && ClientData.getOffGroundTicks() > BetterFlightCommonConfig.TAKE_OFF_JUMP_DELAY
                && player.isSprinting()
                && !player.isFallFlying()
                && player.getVelocity().length() > BetterFlightCommonConfig.TAKE_OFF_SPEED;
    }


    /**
     * Coordinates client-server classic take off action if player has enough charge
     *
     * @param player
     * @return true if action was successful
     */
    public static boolean classicTakeOff(PlayerEntity player)
    {
        if (spendCharge(player, BetterFlightCommonConfig.takeOffCost))
        {
            FlightHandler.handleClassicTakeoff(player);
            ClientFlightMessages.sendToServer(player, FlightActionType.TAKEOFF);
            return true;
        }
        return false;
    }

    /**
     * Coordinates client-server classic flap action if player has enough charge
     *
     * @param player
     * @return true if action was successful
     */
    public static boolean classicFlap(PlayerEntity player)
    {
        if (spendCharge(player, BetterFlightCommonConfig.flapCost))
        {
            FlightHandler.handleClassicFlap(player);
            ClientFlightMessages.sendToServer(player, FlightActionType.FLAP);
            return true;
        }
        return false;
    }

    /**
     * Handles recharging flight stamina if player is touching the ground and not flaring. Triggers per tick.
     *
     * @param event
     * @side both
     */
    //TODO Neoforge 1.21 break this up to client and server logical
    public static void handleRecharge(PlayerEntity player)
    {
        if (player.isCreative())
        {
            charge = BetterFlightCommonConfig.maxCharge;
            return;
        }

        int chargeThreshold = player.isOnGround() ? BetterFlightCommonConfig.rechargeTicksOnGround : BetterFlightCommonConfig.rechargeTicksInAir;

        if (rechargeTickCounter < chargeThreshold)
        {
            rechargeTickCounter++;
        }

        if (!ClientData.isFlaring() && rechargeTickCounter >= chargeThreshold && charge < BetterFlightCommonConfig.maxCharge)
        {

            if (player.getHungerManager().getFoodLevel() > BetterFlightCommonConfig.minFood)
            {
                charge++;
                rechargeTickCounter = 0;
                ClassicHudOverlay.setRechargeBorderTimer(ClientConfig.BORDER_FLASH_TICKS);
                StaminaHudOverlay.startRegenAnimation();
                ClientFlightMessages.sendToServer(player, FlightActionType.RECHARGE);
            }
        }
    }

    //MAYBE rework flare or introduce a new method to "glide"? Like being able to hold one's position while in the air like a bird.
    public static void tryFlare(PlayerEntity player)
    {
        if (ClientData.isWearingFunctionalWings()
                && ClientData.isFlightEnabled()
                && Keybinding.flareKey.isPressed()
                && ((player.isCreative() || charge > 0) || player.isTouchingWater() || player.isInLava())
                && !player.isOnGround()
                && player.isFallFlying())
        {

            if (player.isTouchingWater() || player.isInLava())
            {
                ClientFlightMessages.sendToServer(player, FlightActionType.STOP);
                return;
            }

            FlightHandler.handleFlare(player);

            flareTickCounter++;
            ClientData.setIsFlaring(true);

            if (flareTickCounter >= BetterFlightCommonConfig.flareTicksPerChargePoint)
            {
                spendCharge(player, 1);
                flareTickCounter = 0;
            }
        } else
        {
            if (flareTickCounter > 0)
            {
                flareTickCounter--;
            }
            ClientData.setIsFlaring(false);
        }
    }

    /**
     * Spends flight stamina if possible.
     *
     * @param player target player
     * @param points how much stamina to spend
     * @return true if creative mode or action was successful
     */
    private static boolean spendCharge(PlayerEntity player, int points)
    {
        if (player.isCreative()) return true;

        if (charge >= points)
        {
            charge = Math.max(0,charge-points);
            rechargeTickCounter = 0;
            ClientData.setCooldown(BetterFlightCommonConfig.cooldownTicks);
            ClassicHudOverlay.setDepletionBorderTimer(ClientConfig.BORDER_FLASH_TICKS);
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * Find equipped and working elytra items from the player
     *
     * @param player to check
     * @return ItemStack wings are found; null if wings not found or broken
     */
    @Nullable
    public static ElytraData findWings(PlayerEntity player)
    {
        ItemStack itemStack = findWingsItemStack(player);
        if (itemStack.isEmpty()) return null;
        int durabilityRemaining = itemStack.getMaxDamage() - itemStack.getDamage();
        float durabilityPercent = (float) itemStack.getDamage() / (float) itemStack.getMaxDamage();

        return new ElytraData(itemStack, durabilityRemaining, durabilityPercent);
    }

    /**
     * Returns ItemStack of player equipped wings
     *
     * @param player
     * @return ItemStack of equipped wings; ItemStack.EMPTY if not found
     */
    private static ItemStack findWingsItemStack(PlayerEntity player)
    {
        // check the player's chest slot for elytra
        ItemStack elytraStack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (elytraStack.getItem() instanceof FabricElytraItem || elytraStack.getItem() instanceof ElytraItem)
        {
            return elytraStack;
        }
//        if (BetterFlight.isCuriousElytraLoaded)
//        {
//            elytraStack = CuriosCompat.getCurioWings(player);
//            if (elytraStack != ItemStack.EMPTY) return elytraStack;
//
//        }
//        if (BetterFlight.isBeanBackpackLoaded)
//        {
//            elytraStack = BeansCompat.getBeanWings(player);
//            if (elytraStack != ItemStack.EMPTY) return elytraStack;
//        }
        return ItemStack.EMPTY;
    }

    public static boolean checkForAir(World world, LivingEntity player)
    {
        Box boundingBox = player.getBoundingBox()
                .withMaxY(player.getBoundingBox().minY + 3.5)
                .expand(1D, 0D, 1D)
                .offset(0, -1.5D, 0);
        Stream<BlockPos> blocks = getBlockPosIfLoaded(world, boundingBox);
        Stream<BlockPos> filteredBlocks = blocks.filter(
                pos ->
                {
                    BlockState block = world.getBlockState(pos);
                    return block.isFullCube(world, pos) || block.isLiquid(); //checks if block is solid or fluid
                });
        return filteredBlocks.toList().isEmpty();
    }

    /**
     * Returns stream of BlockPos in given AABB, if the chunks are already loaded.
     *
     * @param world
     * @param boundingBox
     * @return Stream of BlockPos found
     */
    private static Stream<BlockPos> getBlockPosIfLoaded(World world, Box boundingBox)
    {
        int i = MathHelper.floor(boundingBox.minX);
        int j = MathHelper.floor(boundingBox.maxX);
        int k = MathHelper.floor(boundingBox.minY);
        int l = MathHelper.floor(boundingBox.maxY);
        int i1 = MathHelper.floor(boundingBox.minZ);
        int j1 = MathHelper.floor(boundingBox.maxZ);
        return world.isRegionLoaded(i, k, i1, j, l, j1) ? BlockPos.stream(boundingBox) : Stream.empty();
    }
}
