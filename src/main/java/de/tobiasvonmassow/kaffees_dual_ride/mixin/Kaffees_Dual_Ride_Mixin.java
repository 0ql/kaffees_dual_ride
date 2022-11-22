package de.tobiasvonmassow.kaffees_dual_ride.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractHorseEntity.class)
public class Kaffees_Dual_Ride_Mixin {
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
}