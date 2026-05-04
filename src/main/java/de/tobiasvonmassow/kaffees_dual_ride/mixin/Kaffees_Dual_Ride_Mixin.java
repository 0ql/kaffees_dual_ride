package de.tobiasvonmassow.kaffees_dual_ride.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractHorseEntity.class)
public abstract class Kaffees_Dual_Ride_Mixin extends AnimalEntity {

	@Shadow private float lastAngryAnimationProgress;

	@Shadow protected abstract float getPassengerAttachmentY(EntityDimensions dimensions, float scaleFactor);

	protected Kaffees_Dual_Ride_Mixin(EntityType<? extends AbstractHorseEntity> arg, World arg2) {
		super((EntityType<? extends AnimalEntity>) arg, arg2);
	}

	@Override
	public Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		int i = this.getPassengerList().indexOf(passenger);
		float offset = 0;
		if (this.getPassengerList().size() > 1) {
			offset = i == 0 ? 0.2f : -0.6f;
		}
		return new Vector3f(0.0f, getPassengerAttachmentY(dimensions, scaleFactor) + 0.15f * lastAngryAnimationProgress * scaleFactor, -0.7f * lastAngryAnimationProgress * scaleFactor + offset);
	}

	@Override
	public boolean hasPassengers() {
		return false;
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() < this.getMaxPassengers();
	}

	protected int getMaxPassengers() {
		return 2;
	}

}
