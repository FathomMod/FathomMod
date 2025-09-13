package net.fathommod.item;

import net.minecraft.world.item.Item;

public class KillersPawItem extends Item {

    public KillersPawItem() {
        super(new Item.Properties().stacksTo(1));
    }

//    @Override
//    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
//        return (player.getCooldowns().isOnCooldown(FathommodModItems.KILLERS_PAW.get())) ? InteractionResultHolder.success(player.getItemBySlot(EquipmentSlot.MAINHAND)) : InteractionResultHolder.fail(player.getItemBySlot(EquipmentSlot.MAINHAND));
//    }
}
