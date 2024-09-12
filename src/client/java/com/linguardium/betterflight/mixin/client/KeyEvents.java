package com.linguardium.betterflight.mixin.client;

import com.rejahtavi.betterflight.client.events.ClientEvents;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyEvents {

    @Inject(method="onKey",
            at=@At(value="INVOKE",
                    target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void handleKeysOutsideScreen(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (ClientEvents.onKeyInput(key, scancode, modifiers, action)) {
            ci.cancel();
        }
    }

    @Inject(at=@At("HEAD"), method="onKey",cancellable = true)
    private void handleModKeys(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {

    }
}
