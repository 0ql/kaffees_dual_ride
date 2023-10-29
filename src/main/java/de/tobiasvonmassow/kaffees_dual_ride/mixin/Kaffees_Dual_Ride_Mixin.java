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

@Mixin(AbstractHorseEntity.class)
public abstract class Kaffees_Dual_Ride_Mixin extends AnimalEntity {

	@Shadow
	private float lastAngryAnimationProgress;

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

	// abstract class + constructor required, so I can extend AnimalEntity to
	// perform this explicit override
	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		if (this.canAddPassenger(player)) {
			player.startRiding(this);
			return ActionResult.success(this.getWorld().isClient);
		}
		return super.interactMob(player, hand);
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() < this.getMaxPassengers();
	}

	protected int getMaxPassengers() {
		return 2;
	}

}
