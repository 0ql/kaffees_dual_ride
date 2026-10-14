package de.tobiasvonmassow.kaffees_dual_ride.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class DisableSecondRiderFromInterruptingHorseMixin {

	@Shadow
	public ServerPlayer player;

	@Redirect(method = "handlePlayerCommand(Lnet/minecraft/network/protocol/game/ServerboundPlayerCommandPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PlayerRideableJumping;handleStartJump(I)V"))
	private void handleStartJump(PlayerRideableJumping jm, int i) {
		if (((Entity) jm).getPassengers().indexOf(this.player) == 0) {
			jm.handleStartJump(i);
		}
	}

	@Redirect(method = "handlePlayerCommand(Lnet/minecraft/network/protocol/game/ServerboundPlayerCommandPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PlayerRideableJumping;handleStopJump()V"))
	private void handleStopJump(PlayerRideableJumping jm) {
		if (((Entity) jm).getPassengers().indexOf(this.player) == 0) {
			jm.handleStopJump();
		}
	}
}
