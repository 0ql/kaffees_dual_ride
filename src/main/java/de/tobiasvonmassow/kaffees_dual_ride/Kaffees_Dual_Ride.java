package de.tobiasvonmassow.kaffees_dual_ride;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

import net.minecraft.util.ActionResult;

public class Kaffees_Dual_Ride implements ModInitializer {
	@Override
	public void onInitialize() {
		UseEntityCallback.EVENT.register((player, world, hand, pos, hitResult) -> {
			if (!player.isSpectator() && !player.isSneaking()) {
				final Entity target = hitResult.getEntity();
				if (target instanceof AbstractHorseEntity && target.getPassengerList().size() < 2) {
					player.startRiding(target, true);
				}
			}
			return ActionResult.PASS;
		});
	}
}
