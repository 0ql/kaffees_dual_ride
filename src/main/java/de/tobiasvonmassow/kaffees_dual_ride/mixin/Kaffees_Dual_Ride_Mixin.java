package de.tobiasvonmassow.kaffees_dual_ride.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractHorseEntity.class)
public abstract class Kaffees_Dual_Ride_Mixin extends AnimalEntity {

	@Shadow
	private float lastAngryAnimationProgress;

	@Shadow public abstract void equipHorseArmor(PlayerEntity player, ItemStack stack);

	@Shadow public abstract boolean isTame();

	@Shadow public abstract void openInventory(PlayerEntity player);

	@Shadow protected abstract void putPlayerOnBack(PlayerEntity player);

	protected Kaffees_Dual_Ride_Mixin(EntityType<? extends AbstractHorseEntity> arg, World arg2) {
		super(arg, arg2);
	}

	@Override
	public Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		int i = this.getPassengerList().indexOf(passenger);
		float offset = 0;
		if (this.getPassengerList().size() > 1) {
			offset = i == 0 ? 0.2f : -0.6f;
		}
		return super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor).add((new Vec3d(0.0, 0.15f * lastAngryAnimationProgress * scaleFactor, -0.7f * lastAngryAnimationProgress * scaleFactor + offset)).rotateY(-this.getYaw() * 0.017453292F));
	}

	@Override
	public boolean hasPassengers() {
		return false;
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() < 2;
	}

}
