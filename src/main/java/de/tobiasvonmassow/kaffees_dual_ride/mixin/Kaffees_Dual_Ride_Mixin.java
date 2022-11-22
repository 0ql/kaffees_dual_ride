package de.tobiasvonmassow.kaffees_dual_ride.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.ImmutableList;

@Mixin(AbstractHorseEntity.class)
public abstract class Kaffees_Dual_Ride_Mixin extends AnimalEntity {

	protected Kaffees_Dual_Ride_Mixin(EntityType<? extends AbstractHorseEntity> arg, World arg2) {
		super((EntityType<? extends AnimalEntity>)arg, arg2);
    }

	@Inject(at = @At(value = "TAIL"), method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;)V", locals = LocalCapture.CAPTURE_FAILHARD)
	private void injected(Entity passenger, CallbackInfo ci) {
		// don't reposition riders if there is only one rider
		if (((AbstractHorseEntity)(Object)this).getPassengerList().size() < 2) return;
		// set passengerposition to the position of the horse
		int i = ((AbstractHorseEntity)(Object)this).getPassengerList().indexOf(passenger);
		// horizontal offset
		float horizontal_offset = (float)((((AbstractHorseEntity)(Object)this).isRemoved() ? (double)0.01f : ((AbstractHorseEntity)(Object)this).getMountedHeightOffset()) + passenger.getHeightOffset());
		// first passenger is offset by 0.2, second passenger is offset by -0.6
		float x_offset = i == 0 ? 0.2f : -0.6f;
		// rotate passengers with horse
		Vec3d vec3d = new Vec3d(x_offset, 0.0, 0.0).rotateY(-((AbstractHorseEntity)(Object)this).getYaw() * ((float)Math.PI / 180) - 1.5707964f);
		passenger.setPosition(((AbstractHorseEntity)(Object)this).getX() + vec3d.x,((AbstractHorseEntity)(Object)this).getY() + (double)horizontal_offset,((AbstractHorseEntity)(Object)this).getZ() + vec3d.z);
	}
	
	// abstract class + constructor required so I can extend AnimalEntity to perform this explicit override 
	@Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getPassengerList().size() > 0 && this.canAddPassenger(player)) {
			player.startRiding(this);
			return ActionResult.success(this.world.isClient);
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