package net.fathommod.init;

import net.fathommod.FathommodMod;
import net.fathommod.world.inventory.TrinkeryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FathommodModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, FathommodMod.MOD_ID);
	public static final DeferredHolder<MenuType<?>, MenuType<TrinkeryMenu>> TRINKERY = REGISTRY.register("trinkery", () -> IMenuTypeExtension.create(TrinkeryMenu::new));
}
