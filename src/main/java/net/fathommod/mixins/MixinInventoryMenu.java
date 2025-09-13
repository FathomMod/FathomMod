package net.fathommod.mixins;

import com.mojang.datafixers.util.Pair;
import net.fathommod.EventHandler;
import net.fathommod.TwoHandedItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InventoryMenu.class)
public class MixinInventoryMenu {
    @Shadow @Final private Player owner;

    @ModifyArg(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/InventoryMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"
    ))
    private Slot modifySlot(Slot original) {
        return original.getContainerSlot() == 40 ? new Slot(original.container, 40, 77, 62) {
            @Override
            public void setByPlayer(@NotNull ItemStack p_270969_, @NotNull ItemStack p_299918_) {
                owner.onEquipItem(EquipmentSlot.OFFHAND, p_299918_, p_270969_);
                super.setByPlayer(p_270969_, p_299918_);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }

            @Override
            public boolean mayPickup(@NotNull Player player) {
                return canPickupFromOffhand(player, this.getItem()) && super.mayPickup(player);
            }

            @Override
            public boolean mayPlace(@NotNull ItemStack p_40231_) {
                return !(owner.getMainHandItem().getItem() instanceof TwoHandedItem) && super.mayPlace(p_40231_);
            }
        } : original;
    }

    @Unique
    @SuppressWarnings("all")
    private boolean canPickupFromOffhand(Player player, ItemStack stack) {
        return !EventHandler.tryOrDefault(CompoundTag.class, NullPointerException.class, new CompoundTag(), () -> stack.get(DataComponents.CUSTOM_DATA).copyTag()).getBoolean("__fathommod__copy_of_two_handed_item");
    }
}