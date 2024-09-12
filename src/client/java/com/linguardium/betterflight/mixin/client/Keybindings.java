package com.linguardium.betterflight.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.rejahtavi.betterflight.client.Keybinding;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
public class Keybindings {
    @Inject(method="onKeyPressed", at=@At("HEAD"))
    private static void handleOwnKeysOnPressed(InputUtil.Key key, CallbackInfo ci) {
        Keybinding.getBinding(key).ifPresent(binding->{
            BoundKeyAccessor accessor = ((BoundKeyAccessor)binding);
            accessor.setTimesPressed(accessor.getTimesPressed()+1);
        });
    }
    @Inject(method="setKeyPressed", at=@At("HEAD"))
    private static void handleOwnKeysSetPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        Keybinding.getBinding(key).ifPresent(binding->binding.setPressed(pressed));
    }
    @WrapOperation(method="updateKeysByCode", at=@At(value="INVOKE",target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object dontStoreOwnKeys(Map instance, Object k, Object v, Operation<Object> original) {
        if (Keybinding.isOwnKey((KeyBinding)v)) return v;
        return original.call(instance,k,v);
    }
}
