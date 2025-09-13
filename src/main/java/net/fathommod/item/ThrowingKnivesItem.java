
package net.fathommod.item;

import net.fathommod.DamageClasses;
import net.fathommod.DevUtils;
import net.fathommod.entity.ThrowingKnivesEntity;
import net.fathommod.init.FathommodModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ThrowingKnivesItem extends Item {
	public ThrowingKnivesItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(@NotNull ItemStack itemstack, @NotNull LivingEntity entity) {
		return 72000;
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player entity, @NotNull InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = InteractionResultHolder.fail(entity.getItemInHand(hand));
		if (entity.getAbilities().instabuild || findAmmo(entity) != ItemStack.EMPTY) {
			ar = InteractionResultHolder.success(entity.getItemInHand(hand));
			entity.startUsingItem(hand);
		}
		return ar;
	}

	@Override
	public void onUseTick(Level world, @NotNull LivingEntity entity, @NotNull ItemStack itemstack, int count) {
		if (!world.isClientSide() && entity instanceof ServerPlayer player) {
			ItemStack stack = findAmmo(player);
            if (!player.getAbilities().instabuild && player.getCooldowns().isOnCooldown(stack.getItem()))
                return;
			if (player.getAbilities().instabuild || stack != ItemStack.EMPTY) {
                if (!player.hasInfiniteMaterials())
                    player.getCooldowns().addCooldown(stack.getItem(), 10);
				ThrowingKnivesEntity projectile = new ThrowingKnivesEntity(FathommodModEntities.THROWING_KNIVES_ENTITY.get(), world);
				projectile.setBaseDamage(4);
				projectile.setOwner(entity);
				projectile.setCritArrow(false);
				projectile.setSilent(true);
				projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0, 1.25f, 0);
				projectile.teleportTo(entity.getX(), entity.getEyeY(), entity.getZ());
				world.addFreshEntity(projectile);
				if (player.getAbilities().instabuild) {
					projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
				} else {
					if (stack.isDamageableItem()) {
						stack.hurtAndBreak(1,  player, EquipmentSlot.MAINHAND);
					} else {
						stack.shrink(1);
					}
				}
			}
			entity.releaseUsingItem();
		}
	}

	private ItemStack findAmmo(Player player) {
		ItemStack stack = ProjectileWeaponItem.getHeldProjectile(player, e -> e.getItem() == ThrowingKnivesEntity.PROJECTILE_ITEM.getItem());
		if (stack == ItemStack.EMPTY) {
			for (int i = 0; i < player.getInventory().items.size(); i++) {
				ItemStack teststack = player.getInventory().items.get(i);
				if (teststack.getItem() == ThrowingKnivesEntity.PROJECTILE_ITEM.getItem()) {
					stack = teststack;
					break;
				}
			}
		}
		return stack;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack p_41421_, @NotNull TooltipContext p_339594_, @NotNull List<Component> components, @NotNull TooltipFlag p_41424_) {
		components.add(Component.translatable("tooltip.fathommod.throwing_knives.first_line").setStyle(Style.EMPTY.withColor(0x58a7bf)));
		components.add((Component.translatable("tooltip.fathommod.generic_damage", 4).withColor(DevUtils.DAMAGE_TOOLTIPS_HEX).append(Component.translatable(DamageClasses.RANGED.getComponent()).withColor(DamageClasses.RANGED.getColor()))));
		super.appendHoverText(p_41421_, p_339594_, components, p_41424_);
	}
}
