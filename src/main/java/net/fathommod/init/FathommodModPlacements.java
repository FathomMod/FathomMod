package net.fathommod.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fathommod.FathommodMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class FathommodModPlacements {
    public static class PaltnPlacement extends PlacementModifier {
        public static final MapCodec<PaltnPlacement> CODEC = MapCodec.assumeMapUnsafe(Codec.unit(PaltnPlacement::new));

        public PaltnPlacement() {}

        @Override
        public @NotNull Stream<BlockPos> getPositions(PlacementContext context, @NotNull RandomSource random, BlockPos origin) {
            int x = origin.getX();
            int z = origin.getZ();
            int y = context.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            if (y < 100) return Stream.empty();

            BlockState below = context.getLevel().getBlockState(new BlockPos(x, y - 1, z));
            if (!(below.getBlock() == Blocks.STONE || below.getBlock() == Blocks.GRASS_BLOCK)) return Stream.empty();

            return Stream.of(new BlockPos(x, y, z));
        }

        @Override
        public @NotNull PlacementModifierType<?> type() {
            return PALTN_PLACEMENT.get();
        }
    }

    public static final DeferredRegister<PlacementModifierType<?>> REGISTRY = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, FathommodMod.MOD_ID);
    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<PaltnPlacement>> PALTN_PLACEMENT = REGISTRY.register("paltn_placement", () -> () -> PaltnPlacement.CODEC);
}