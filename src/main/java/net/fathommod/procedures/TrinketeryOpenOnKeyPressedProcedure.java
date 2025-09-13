package net.fathommod.procedures;

import io.netty.buffer.Unpooled;
import net.fathommod.network.FathommodModVariables;
import net.fathommod.world.inventory.TrinkeryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("all")
public class TrinketeryOpenOnKeyPressedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof ServerPlayer _ent) {
			BlockPos _bpos = BlockPos.containing(x, y, z);
			_ent.openMenu(new MenuProvider() {
				@Override
				public @NotNull Component getDisplayName() {
					return Component.literal("Trinkery");
				}

				@Override
				public boolean shouldTriggerClientSideContainerClosingOnOpen() {
					return false;
				}

				@Override
				public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
					TrinkeryMenu menu = new TrinkeryMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
                    Map<Integer, Slot> slots = menu.get();
                    slots.get(0).set(player.getData(FathommodModVariables.ENTITY_VARIABLES).trinket1.copy());
                    slots.get(1).set(player.getData(FathommodModVariables.ENTITY_VARIABLES).trinket2.copy());
                    slots.get(2).set(player.getData(FathommodModVariables.ENTITY_VARIABLES).trinket3.copy());
                    slots.get(3).set(player.getData(FathommodModVariables.ENTITY_VARIABLES).trinket4.copy());
                    slots.get(4).set(player.getData(FathommodModVariables.ENTITY_VARIABLES).replacedOffhandItem.copy());
                    return menu;
				}
			}, _bpos);
		}
	}
}
