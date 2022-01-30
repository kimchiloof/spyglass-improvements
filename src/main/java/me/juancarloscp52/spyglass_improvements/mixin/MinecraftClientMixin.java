package me.juancarloscp52.spyglass_improvements.mixin;

import me.juancarloscp52.spyglass_improvements.client.SpyglassImprovementsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 2))
    public boolean handleInput(KeyBinding instance){
        return instance.isPressed() || SpyglassImprovementsClient.useSpyglass.isPressed();
    }
    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;stopUsingItem(Lnet/minecraft/entity/player/PlayerEntity;)V"))
    public void stopUsing(ClientPlayerInteractionManager instance, PlayerEntity player){
        instance.stopUsingItem(player);
        MinecraftClient client = MinecraftClient.getInstance();

        // When stop using, reset spyglass position if it was changed.
        if(SpyglassImprovementsClient.useSpyglass.wasPressed()){
            ((KeyBindingInvoker) SpyglassImprovementsClient.useSpyglass).invokeReset();
            int slot = SpyglassImprovementsClient.slot;
            if(player.getOffHandStack().getItem().equals(Items.SPYGLASS)){
                if(slot > 8) {
                    client.interactionManager.clickSlot(0, slot, 40, SlotActionType.SWAP, client.player);
                    SpyglassImprovementsClient.slot = -1;
                }
            }else if(slot >= 0 && slot <=8) {
                player.getInventory().selectedSlot = slot;
            }
        }
    }
}
