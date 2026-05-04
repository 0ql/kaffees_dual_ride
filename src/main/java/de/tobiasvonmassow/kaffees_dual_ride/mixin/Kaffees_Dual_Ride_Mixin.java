package de.tobiasvonmassow.kaffees_dual_ride.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractHorseEntity.class)
public abstract class Kaffees_Dual_Ride_Mixin extends AnimalEntity {

	protected Kaffees_Dual_Ride_Mixin(EntityType<? extends AbstractHorseEntity> arg, World arg2) {
		super(arg, arg2);
	}

	@Shadow public abstract boolean isTame();

	@Shadow public abstract void openInventory(PlayerEntity player);

	@Shadow public abstract boolean hasArmorSlot();

	@Shadow protected abstract void putPlayerOnBack(PlayerEntity player);

	@Shadow public abstract void equipHorseArmor(PlayerEntity player, ItemStack itemStack);

	@Shadow public abstract boolean hasArmorInSlot();

	@Shadow public abstract boolean isHorseArmor(ItemStack itemStack);

	@Shadow
	private float lastAngryAnimationProgress;

	@Override
	public void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
		int n = this.getPassengerList().indexOf(passenger);
		float offset = 0;
		if (this.getPassengerList().size() > 1) {
			offset = n == 0 ? 0.2f : -0.6f;
		}
		Vec3d vec3d = (new Vec3d(offset, 0.0, 0.0)).rotateY(-this.getYaw() * 0.017453292F - 1.5707964F);
		float f = MathHelper.sin(this.bodyYaw * 0.017453292F);
		float g = MathHelper.cos(this.bodyYaw * 0.017453292F);
		float h = 0.7F * this.lastAngryAnimationProgress;
		float i = 0.15F * this.lastAngryAnimationProgress;
		positionUpdater.accept(passenger, this.getX() + vec3d.x + (double) (h * f), this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset() + (double) i, this.getZ() + vec3d.z - (double) (h * g));
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		if (this.isBaby()) {
			return super.interactMob(player, hand);
		} else if (this.isTame() && player.shouldCancelInteraction()) {
			this.openInventory(player);
			return ActionResult.success(this.getWorld().isClient);
		} else {
			ItemStack itemStack = player.getStackInHand(hand);
			if (!itemStack.isEmpty()) {
				ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
				if (actionResult.isAccepted()) {
					return actionResult;
				}

				if (this.hasArmorSlot() && this.isHorseArmor(itemStack) && !this.hasArmorInSlot()) {
					this.equipHorseArmor(player, itemStack);
					return ActionResult.success(this.getWorld().isClient);
				}
			}

			this.putPlayerOnBack(player);
			return ActionResult.success(this.getWorld().isClient);
		}
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() < this.getMaxPassengers();
	}

	protected int getMaxPassengers() {
		return 2;
	}

}
