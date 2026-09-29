package com.dashomi.preventer.mixin;

import com.dashomi.preventer.PreventerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class RenderHandItemMixin {
    @Inject(
            method = "submitArmWithItem",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void hideOffhandItem(PlayerRenderState playerRenderState, FirstPersonHandsAndItemsRenderState handsRenderState, float frameInterp, float xRot, InteractionHand hand, float attack, ItemStack stack, float inverseArmHeight, PoseStack matrices, SubmitNodeCollector submitNodeCollector, int light, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (hand != InteractionHand.OFF_HAND || stack.isEmpty() || player == null ||
                (player.getUsedItemHand().equals(InteractionHand.OFF_HAND) && player.isUsingItem())) return;

        if (PreventerClient.preventerActive()) {
            Item handItem = stack.getItem();

            if (handItem == Items.SHIELD && PreventerClient.config.hideShield) {
                ci.cancel();
            }

            if (handItem == Items.TOTEM_OF_UNDYING && PreventerClient.config.hideTotem) {
                ci.cancel();
            }
        }
    }
}
