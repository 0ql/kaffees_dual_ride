package de.tobiasvonmassow.kaffees_dual_ride.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SaddleItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(AbstractHorseEntity.class)
public abstract class Kaffees_Dual_Ride_Mixin extends AnimalEntity {
	private static final UUID HORSE_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295"); //Used for attribute modifier when equipping armor
	protected Kaffees_Dual_Ride_Mixin(EntityType<? extends AbstractHorseEntity> arg, World arg2) {
		super(arg, arg2);
	}

	@Shadow
	private float lastAngryAnimationProgress;

	@Shadow public abstract void equipHorseArmor(PlayerEntity player, ItemStack stack);

	@Shadow public abstract boolean hasArmorInSlot();

	@Shadow public abstract void saddle(@Nullable SoundCategory sound);

	@Shadow public abstract boolean isSaddled();

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

	// abstract class + constructor required, so I can extend AnimalEntity to
	// perform this explicit override
	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		if(!player.getStackInHand(hand).isEmpty()) {
			if (this.getFirstPassenger() == null) {
				if (player.getStackInHand(hand).getItem() instanceof HorseArmorItem && !this.hasArmorInSlot()) {
					customEquipHorseArmor(player.getStackInHand(hand), player);
				} else if (player.getStackInHand(hand).getItem() instanceof SaddleItem && !this.isSaddled()) {
					if(!player.getAbilities().creativeMode) {
						player.getStackInHand(hand).decrement(1);
					}
					this.saddle(SoundCategory.NEUTRAL);
				}
			}
			return ActionResult.SUCCESS;
		}
		if (this.canAddPassenger(player)) {
			player.startRiding(this);
			return ActionResult.success(this.getWorld().isClient);
		}
		return super.interactMob(player, hand);
	}

	private void customEquipHorseArmor(ItemStack stack, PlayerEntity player) {
		if(stack.getItem() instanceof HorseArmorItem) {
			this.equipHorseArmor(player, stack);
			if (!this.getWorld().isClient) {
				this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(HORSE_ARMOR_BONUS_ID);
				int i = ((HorseArmorItem) stack.getItem()).getBonus();
				if (i != 0) {
					this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addTemporaryModifier(new EntityAttributeModifier(HORSE_ARMOR_BONUS_ID, "Horse armor bonus", (double) i, EntityAttributeModifier.Operation.ADDITION));
				}
			}
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