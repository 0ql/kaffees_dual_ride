package de.tobiasvonmassow.kaffees_dual_ride.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractHorseEntity.class)
public abstract class Kaffees_Dual_Ride_Mixin extends AnimalEntity {

	protected Kaffees_Dual_Ride_Mixin(EntityType<? extends AbstractHorseEntity> arg, World arg2) {
		super((EntityType<? extends AnimalEntity>) arg, arg2);
	}

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
