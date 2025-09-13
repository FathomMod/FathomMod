package net.fathommod.init;

import net.fathommod.FathommodMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class FathommodModTabs {
	public static final ArrayList<DeferredHolder<Item, Item>> DELISTED_ITEMS = new ArrayList<>(List.of(FathommodModItems.WHY_THO));
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FathommodMod.MOD_ID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FATHOM_MOD_TAB = REGISTRY.register("fathom_mod",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.fathommod.fathom_mod")).icon(() -> new ItemStack(FathommodModItems.WHY_THO.get())).displayItems((parameters, output) -> {
				Class<?> clazz = FathommodModItems.class;
				for (Field field : clazz.getDeclaredFields()) {
					try {
						if (Modifier.isStatic(field.getModifiers())) {
							field.setAccessible(true);
							Object value = field.get(null);
							if (value instanceof DeferredHolder item && !DELISTED_ITEMS.contains(item)) {
								//noinspection unchecked
								output.accept(((DeferredHolder<Item, Item>) item).get().getDefaultInstance());
							}
						}
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}).withSearchBar().build());
}
