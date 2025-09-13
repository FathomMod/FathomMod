package net.fathommod;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.fathommod.init.FathommodModItems;
import net.fathommod.init.FathommodModTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@JeiPlugin
@SuppressWarnings("unused")
public class FMJEIPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "fm_jei_plugin");
    }

    private final ArrayList<ItemStack> JEI_DELISTED_ITEMS = new ArrayList<>();

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        for (DeferredHolder<Item, Item> item : FathommodModTabs.DELISTED_ITEMS) {
            try {
                JEI_DELISTED_ITEMS.add(item.get().getDefaultInstance());
            } catch (NullPointerException ignored) {}
        }
        JEI_DELISTED_ITEMS.add(FathommodModItems.GRENADE_ITEM.get().getDefaultInstance());
        JEI_DELISTED_ITEMS.add(FathommodModItems.GRENADE_LAUNCHER.get().getDefaultInstance());
        JEI_DELISTED_ITEMS.add(FathommodModItems.PHASING_TEXAS.get().getDefaultInstance());
        JEI_DELISTED_ITEMS.add(FathommodModItems.LIGHTBEARER.get().getDefaultInstance());
        JEI_DELISTED_ITEMS.add(FathommodModItems.AXEPICK.get().getDefaultInstance());
        JEI_DELISTED_ITEMS.add(FathommodModItems.WHY_THO.get().getDefaultInstance());
        JEI_DELISTED_ITEMS.add(FathommodModItems.RIOT_SHIELD.get().getDefaultInstance());
        JEI_DELISTED_ITEMS.add(FathommodModItems.ZEUS_BOOTS.get().getDefaultInstance());
        registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, JEI_DELISTED_ITEMS);
    }
}