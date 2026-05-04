package de.tobiasvonmassow.kaffees_dual_ride.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;

@Mixin(AbstractHorseEntity.class)
public abstract class Kaffees_Dual_Ride_Mixin extends AnimalEntity {

	@Shadow
	private float lastAngryAnimationProgress;

	@Shadow public abstract void equipHorseArmor(PlayerEntity player, ItemStack stack);

	@Shadow public abstract boolean hasArmorInSlot();

	@Shadow public abstract boolean isTame();

	@Shadow public abstract void openInventory(PlayerEntity player);

	@Shadow public abstract boolean hasArmorSlot();

	@Shadow public abstract boolean isHorseArmor(ItemStack item);

	@Shadow protected abstract void putPlayerOnBack(PlayerEntity player);


	protected Kaffees_Dual_Ride_Mixin(EntityType<? extends AbstractHorseEntity> arg, World arg2) {
		super((EntityType<? extends AnimalEntity>) arg, arg2);
	}

	@Invoker("getPassengerAttachmentY")
	protected abstract float invokeGetPassengerAttachmentY(EntityDimensions dimensions, float scaleFactor);

	@Inject(method = "getPassengerAttachmentPos", at = @At(value = "HEAD", target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;getPassengerAttachmentPos(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityDimensions;F)Lorg/joml/Vector3f;"), cancellable = true)
	private void injected(Entity passenger, EntityDimensions dimensions, float scaleFactor, CallbackInfoReturnable<Vector3f> cir) {
		int i = ((AbstractHorseEntity) (Object) this).getPassengerList().indexOf(passenger);
		float offset = 0;
		if (((AbstractHorseEntity) (Object) this).getPassengerList().size() > 1) {
			offset = i == 0 ? 0.2f : -0.6f;
		}
		cir.setReturnValue(new Vector3f(0.0f, invokeGetPassengerAttachmentY(dimensions, scaleFactor) + 0.15f * lastAngryAnimationProgress * scaleFactor, -0.7f * lastAngryAnimationProgress * scaleFactor + offset));
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
