package net.fathommod.init;

import net.fathommod.FathommodMod;
import net.fathommod.block.BloodGrassBlock;
import net.fathommod.block.BudgetLightBlock;
import net.fathommod.block.PALTNBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FathommodModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, FathommodMod.MOD_ID);
	public static final DeferredHolder<Block, Block> PALTN = REGISTRY.register("paltn", PALTNBlock::new);
	public static final DeferredHolder<Block, Block> BLOOD_GRASS = REGISTRY.register("blood_grass", BloodGrassBlock::new);
	public static final DeferredHolder<Block, Block> BUDGET_LIGHT = REGISTRY.register("budget_light", BudgetLightBlock::new);
}
