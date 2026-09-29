package com.dashomi.preventer.mixin;

import com.dashomi.preventer.PreventerClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.dashomi.preventer.utils.ActionPreventedMessage.sendActionPreventedMessage;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Inject(method = "aiStep", at = @At(value = "HEAD"))
    private void onTickMovement(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (!PreventerClient.preventerActive() && !player.isSpectator()) {
            Level world = player.level();
            BlockPos pos = player.blockPosition();

            if (PreventerClient.config.preventFarmlandJumping) {
                if (world.getBlockState(pos).is(Blocks.FARMLAND)) {
                    sendActionPreventedMessage(player, Component.translatable("preventer.plants.prevented.preventFarmlandJumping"));
                    PreventerClient.LOGGER.info(String.valueOf(player.getDeltaMovement().y));
                    player.setOnGround(false);
                }
            }
        }
    }

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;setSprinting(Z)V"))
    private void wrapSetSprinting(LocalPlayer instance, boolean value, Operation<Void> original) {
        if (!PreventerClient.preventerActive() && value && instance.isUnderWater() && !instance.isSpectator()) {
            if (PreventerClient.config.preventSwimming) {
                sendActionPreventedMessage(instance, Component.translatable("preventer.miscellaneous.prevented.preventSwimming"));
                return;
            }
        }
        original.call(instance, value);
    }
}
