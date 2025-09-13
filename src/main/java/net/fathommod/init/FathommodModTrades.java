package net.fathommod.init;

import net.fathommod.FathommodMod;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@EventBusSubscriber(modid = FathommodMod.MOD_ID)
public class FathommodModTrades {
    @SubscribeEvent
    public static void registerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.ARMORER) {
            FathommodMod.LOGGER.info("Added trades!");
            event.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.EMERALD, 40), new ItemStack(Items.DIAMOND_CHESTPLATE), new ItemStack(FathommodModItems.BLACK_SMITHS_WILL.get()), 1, 10, 0));
        }
    }
}