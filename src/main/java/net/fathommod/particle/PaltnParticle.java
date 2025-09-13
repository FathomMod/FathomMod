package net.fathommod.particle;

import net.fathommod.ClientVars;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public class PaltnParticle extends TextureSheetParticle {

    public PaltnParticle(ClientLevel world, double x, double y, double z, double dx, double dy, double dz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.setSize(0.2f, 0.2f);
        this.lifetime = world.getRandom().nextIntBetweenInclusive(15, 45);
        this.gravity = world.getRandom().nextFloat() * 0.02f + 0.01f;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.scale(ClientVars.particleScale);

        this.hasPhysics = false;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.getBlockState(BlockPos.containing(this.getPos())).isEmpty())
            this.remove();
    }
}
