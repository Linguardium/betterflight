package com.rejahtavi.betterflight.client;

import com.linguardium.betterflight.BetterFlight;
import com.linguardium.betterflight.mixin.client.BoundKeyAccessor;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Keybinding
{
    public static final String KEY_CATEGORY_BETTERFLIGHT = BetterFlight.MODID;
    public static final String KEY_TOGGLE_FLIGHT = "Toggle Flight";
    public static final String KEY_FLAP = "Flap";
    public static final String KEY_FLARE = "Flare";
    public static final String KEY_WIDGET_POS = "Toggle Widget Position";
    private static List<net.minecraft.client.option.KeyBinding> OWN_KEYBINDINGS = new ArrayList<>();
    // key mappings
    public static final KeyBinding toggleKey = createKey(KEY_TOGGLE_FLIGHT, GLFW.GLFW_KEY_F8);
    public static final KeyBinding flapKey = createKey(KEY_FLAP, GLFW.GLFW_KEY_SPACE);
    public static final KeyBinding flareKey = createKey(KEY_FLARE, GLFW.GLFW_KEY_LEFT_SHIFT);
    public static final KeyBinding widgetPosKey = createKey(KEY_WIDGET_POS, GLFW.GLFW_KEY_F10);

    public static boolean isOwnKey(InputUtil.Key key) {
        return getBinding(key).isPresent();
    }
    public static boolean isOwnKey(KeyBinding key) {
        return OWN_KEYBINDINGS.contains(key);
    }

    public static Optional<KeyBinding> getBinding(InputUtil.Key key) {
        Optional<KeyBinding> found = OWN_KEYBINDINGS.stream().filter(b->((BoundKeyAccessor)b).getBoundKey().equals(key)).findFirst();
        return found;
    }
    private static KeyBinding createKey(String id, int defaultKey) {
        KeyBinding binding = new KeyBinding(id,
                InputUtil.Type.KEYSYM, defaultKey, KEY_CATEGORY_BETTERFLIGHT);
        OWN_KEYBINDINGS.add(binding);
        return binding;
    }
}
