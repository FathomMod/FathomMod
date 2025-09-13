package net.fathommod.particle;

import net.fathommod.ClientVars;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.jetbrains.annotations.NotNull;

public class DubGrenadeParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    public DubGrenadeParticle(ClientLevel world, double x, double y, double z, double dx, double dy, double dz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.lifetime = 180;
        this.gravity = -.15f;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.scale(ClientVars.particleScale);

        this.hasPhysics = false;
        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    private int texture = 0;
    private static final int frameTime = 4;

    @Override
    public void tick() {
        super.tick();
        if (this.age % frameTime == 0)
            texture++;
        if (texture > 8) {
            this.remove();
        }
        if (!this.removed && this.spriteSet instanceof ParticleEngine.MutableSpriteSet _spriteSet) {
            this.setSprite(_spriteSet.sprites.get(texture));
        }
    }
}
