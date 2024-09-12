package com.rejahtavi.betterflight.util;

import com.rejahtavi.betterflight.common.BetterFlightCommonConfig;
import com.rejahtavi.betterflight.common.FlightActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class FlightHandler
{

    // These static methods implement the actual flight behaviors.
    // They are used by both client and server.
    //
    // The client calls them directly in response to player input,
    // then immediately sends a CFlightUpdatePacket to the server.
    //
    // The server responds by running the exact same method on
    // to keep server state in sync with the client's requests.

    public static void handleClassicTakeoff(PlayerEntity player)
    {
        // take offs need no forward component, due to the player already sprinting.
        // they do need additional vertical thrust to reliably get the player
        // enough time to flap away before hitting the ground again.
        Vec3d upwards = new Vec3d(0.0D, BetterFlightCommonConfig.TAKE_OFF_THRUST, 0.0D).multiply(getCeilingFactor(player));
        player.startFallFlying();
        player.addVelocity(upwards.x, upwards.y, upwards.z);

    }

    /**
     * grant a small amount of forward thrust along with each vertical boost
     *
     * @param player
     */
    public static void handleClassicFlap(PlayerEntity player)
    {
        double ceilingFactor = getCeilingFactor(player);
        Vec3d upwards = new Vec3d(0.0D, BetterFlightCommonConfig.CLASSIC_FLAP_THRUST, 0.0D).multiply(getCeilingFactor(player));
        Vec3d forwards = player.getVelocity().normalize().multiply(BetterFlightCommonConfig.CLASSIC_FLAP_THRUST * 0.25).multiply(ceilingFactor);
        Vec3d impulse = forwards.add(upwards);
        player.addVelocity(impulse.x, impulse.y, impulse.z);
    }

    /**
     * simplified drag equation = (a bunch of constants) * velocity squared
     * ignore all the constants and just use a single coefficient from config
     *
     * @param player
     * @side client
     */
    public static void handleFlare(PlayerEntity player)
    {
        Vec3d dragDirection = player.getVelocity().normalize().negate();
        double velocitySquared = player.getVelocity().lengthSquared();
        Vec3d dragThrust = dragDirection.multiply(velocitySquared * BetterFlightCommonConfig.FLARE_DRAG);

        double fallingSpeed = player.getVelocity().getY();
        if (fallingSpeed < 0)
        {
            player.addVelocity(dragThrust.x, dragThrust.y - (fallingSpeed * .10), dragThrust.z);
        } else
        {
            player.addVelocity(dragThrust.x, dragThrust.y, dragThrust.z);
        }
    }

    /**
     * converts food into flight stamina by adding exhaustion to the player
     *
     * @param player
     * @side server
     */
    public static void handleFlightStaminaExhaustion(PlayerEntity player)
    {
        player.addExhaustion((float) BetterFlightCommonConfig.exhaustionPerChargePoint);
    }

    /**
     * determines flight power when reaching soft and hard altitude limits
     *
     * @param player
     * @return 1.0d-0.0d based on distance between hard limit and player
     */
    private static double getCeilingFactor(PlayerEntity player)
    {
        double altitude = player.getY();
        // flying low, full power
        if (altitude < BetterFlightCommonConfig.softCeiling)
        {
            return 1.0D;
        }
        // flying too high, no power
        if (altitude > BetterFlightCommonConfig.hardCeiling)
        {
            return 0.0D;
        }
        // flying in between, scale power accordingly
        return (altitude - BetterFlightCommonConfig.softCeiling) / BetterFlightCommonConfig.ceilingRange;
    }

    /**
     * Pushes player in looking vector weakly, mimicking wings. Tells server to play sound at player position
     *
     * @param player
     * @side client
     */
    public static void handleModernFlap(PlayerEntity player)
    {
        double d0 = 0.1; //delta coefficient. Influenced by difference between d0 and current delta
        double d1 = 0.55; //boost coefficient
        Vec3d looking = player.getRotationVector();
        Vec3d delta = player.getVelocity();

        Vec3d impulse = (delta.add(
                looking.x * d1 + (looking.x * d0 - delta.x) * 1.5,
                looking.y * d1 + (looking.y * d0 - delta.y) * 1.5,
                looking.z * d1 + (looking.z * d0 - delta.z) * 1.5))
                .multiply(getCeilingFactor(player))                //scale to ceiling limit
                .add(getUpVector(player).multiply(0.25));  //add slight up vector
        player.addVelocity(impulse.x, impulse.y, impulse.z);
    }

    /**
     * Pushes player in looking vector strongly. Tells server to play sound at player position
     *
     * @param player
     * @side client
     */
    public static void handleModernBoost(PlayerEntity player)
    {
        double d0 = 0.1; //delta coefficient. Influenced by difference between d0 and current delta
        double d1 = 1.0; //boost coefficient
        Vec3d looking = player.getRotationVector();
        Vec3d delta = player.getVelocity();

        Vec3d impulse = (delta.add(
                looking.x * d1 + (looking.x * d0 - delta.x) * 1.5,
                looking.y * d1 + (looking.y * d0 - delta.y) * 1.5,
                looking.z * d1 + (looking.z * d0 - delta.z) * 1.5))
                .multiply(getCeilingFactor(player))                //scale to ceiling limit
                .add(getUpVector(player).multiply(0.25));  //add slight up vector

        player.addVelocity(impulse.x, impulse.y, impulse.z);
    }

    /**
     * Returns a unit vector normal to the player's looking vector.
     *
     * @param player
     * @return Vec3 normal
     */
    private static Vec3d getUpVector(PlayerEntity player)
    {
        float yaw = player.getYaw() % 360;
        double rads = yaw * (Math.PI / 180);
        Vec3d left = new Vec3d(Math.cos(rads), 0, Math.sin(rads));
        Vec3d up = player.getRotationVector().crossProduct(left);
        return up;
    }

}
