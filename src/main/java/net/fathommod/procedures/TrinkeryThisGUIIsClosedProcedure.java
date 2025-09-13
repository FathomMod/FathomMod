package net.fathommod.procedures;

import net.fathommod.event.TrinketEvent;
import net.fathommod.network.FathommodModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("all")
public class TrinkeryThisGUIIsClosedProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof Player))
			return;
		Player player = (Player) entity;
		if (player.level().isClientSide())
			return;
		{
			FathommodModVariables.EntityVariables _vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
            ItemStack oldContent = _vars.trinket1.copy();
            ItemStack slotContent = (entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(0)).getItem() : ItemStack.EMPTY);
			_vars.trinket1 = slotContent;
			if (!oldContent.isEmpty() && !oldContent.is(slotContent.getItem()))
				NeoForge.EVENT_BUS.post(new TrinketEvent.TrinketRemovedEvent(player, oldContent));
			else if (!_vars.trinket1.is(oldContent.getItem()) && !_vars.trinket1.isEmpty())
				NeoForge.EVENT_BUS.post(new TrinketEvent.TrinketAddedEvent(player, _vars.trinket1));
			_vars.syncPlayerVariables(entity);
		}
		{
			FathommodModVariables.EntityVariables _vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
			ItemStack oldContent = _vars.trinket2.copy();
            oldContent.setCount(1);
			ItemStack slotContent = (entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(1)).getItem() : ItemStack.EMPTY);
			_vars.trinket2 = slotContent;
			if (!oldContent.isEmpty() && !oldContent.is(slotContent.getItem()))
				NeoForge.EVENT_BUS.post(new TrinketEvent.TrinketRemovedEvent(player, oldContent));
			else if (!_vars.trinket2.is(oldContent.getItem()) && !_vars.trinket2.isEmpty())
				NeoForge.EVENT_BUS.post(new TrinketEvent.TrinketAddedEvent(player, _vars.trinket2));
			_vars.syncPlayerVariables(entity);
		}
		{
			FathommodModVariables.EntityVariables _vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
			ItemStack oldContent = _vars.trinket3.copy();
            oldContent.setCount(1);
			ItemStack slotContent = (entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY);
			_vars.trinket3 = slotContent;
			if (!oldContent.isEmpty() && !oldContent.is(slotContent.getItem()))
				NeoForge.EVENT_BUS.post(new TrinketEvent.TrinketRemovedEvent(player, oldContent));
			else if (!_vars.trinket3.is(oldContent.getItem()) && !_vars.trinket3.isEmpty())
				NeoForge.EVENT_BUS.post(new TrinketEvent.TrinketAddedEvent(player, _vars.trinket3));
			_vars.syncPlayerVariables(entity);
		}
		{
			FathommodModVariables.EntityVariables _vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
			ItemStack oldContent = _vars.trinket4.copy();
            oldContent.setCount(1);
			ItemStack slotContent = (entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(3)).getItem() : ItemStack.EMPTY);
			_vars.trinket4 = slotContent;
			if (!oldContent.isEmpty() && !oldContent.is(slotContent.getItem()))
				NeoForge.EVENT_BUS.post(new TrinketEvent.TrinketRemovedEvent(player, oldContent));
			else if (!_vars.trinket4.is(oldContent.getItem()) && !_vars.trinket4.isEmpty())
				NeoForge.EVENT_BUS.post(new TrinketEvent.TrinketAddedEvent(player, _vars.trinket4));
			_vars.replacedOffhandItem = (entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(4)).getItem() : ItemStack.EMPTY);
			_vars.syncPlayerVariables(entity);
		}
	}
}
