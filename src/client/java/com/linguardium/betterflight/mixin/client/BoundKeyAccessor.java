package com.linguardium.betterflight.mixin.client;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface BoundKeyAccessor {
    @Accessor
    InputUtil.Key getBoundKey();
    @Accessor
    void setTimesPressed(int value);
    @Accessor
    int getTimesPressed();
}
