package de.tobiasvonmassow.kaffees_dual_ride.mixin;

import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class DisableSecondRiderFromInterruptingHorseMixin {
    
    @Shadow
    public ServerPlayerEntity player;

    @Redirect(method = "onClientCommand(Lnet/minecraft/network/packet/c2s/play/ClientCommandC2SPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/JumpingMount;startJumping(I)V"))
    private void startJumping(JumpingMount jm, int i) {
        if (((AbstractHorseEntity)jm).getPassengerList().indexOf(this.player) == 0) {
            jm.startJumping(i);
        }
    }

    @Redirect(method = "onClientCommand(Lnet/minecraft/network/packet/c2s/play/ClientCommandC2SPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/JumpingMount;stopJumping()V"))
    private void stopJumping(JumpingMount jm) {
        if (((AbstractHorseEntity)jm).getPassengerList().indexOf(this.player) == 0) {
            jm.stopJumping();
        }
    }
}
