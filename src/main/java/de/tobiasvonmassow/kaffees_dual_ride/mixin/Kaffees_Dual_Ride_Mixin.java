package de.tobiasvonmassow.kaffees_dual_ride.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractHorse.class)
public abstract class Kaffees_Dual_Ride_Mixin extends Animal {

	@Shadow
	private float standAnimO;

	@Shadow public abstract void equipBodyArmor(Player player, ItemStack stack);

	@Shadow public abstract boolean isTamed();

	@Shadow public abstract void openCustomInventoryScreen(Player player);

	@Shadow protected abstract void doPlayerRide(Player player);

	protected Kaffees_Dual_Ride_Mixin(EntityType<? extends AbstractHorse> arg, Level arg2) {
		super(arg, arg2);
	}

	@Override
	public Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		int i = this.getPassengers().indexOf(passenger);
		float offset = 0;
		if (this.getPassengers().size() > 1) {
			offset = i == 0 ? 0.2f : -0.6f;
		}
		return super.getPassengerAttachmentPoint(passenger, dimensions, scaleFactor).add((new Vec3(0.0, 0.15f * standAnimO * scaleFactor, -0.7f * standAnimO * scaleFactor + offset)).yRot(-this.getYRot() * 0.017453292F));
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (this.isBaby()) {
			return super.mobInteract(player, hand);
		} else if (this.isTamed() && player.isSecondaryUseActive()) {
			this.openCustomInventoryScreen(player);
			return InteractionResult.SUCCESS;
		} else {
			ItemStack itemStack = player.getItemInHand(hand);
			if (!itemStack.isEmpty()) {
				InteractionResult actionResult = itemStack.interactLivingEntity(player, this, hand);
				if (actionResult.consumesAction()) {
					return actionResult;
				}

				if (this.isEquippableInSlot(itemStack, EquipmentSlot.BODY) && !this.isWearingBodyArmor()) {
					this.equipBodyArmor(player, itemStack);
					return InteractionResult.SUCCESS;
				}
			}

			this.doPlayerRide(player);
			return InteractionResult.SUCCESS;
		}
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengers().size() < 2;
	}

}
