package net.fathommod.item.types;

import com.mojang.math.Axis;
import net.fathommod.DevUtils;
import net.fathommod.FMHitbox;
import net.fathommod.item.SweetSpotItem;
import net.fathommod.item.SweetSpotRange;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class ScytheItem extends FMMeleeWeapon implements SweetSpotItem {
    public ScytheItem(Properties properties) {
        super(properties);
    }

    public static FMHitbox getSweepHitbox(LivingEntity target, Tuple<Entity, Vec3> raycastHit, Player player) {
        return new FMHitbox(raycastHit != null ? (new Vec3(target.position().x, raycastHit.getB().y, target.position().z)) : target.getEyePosition(), new Vec3(1.5, 0.25, 0.5), Axis.YP.rotationDegrees(-player.getYRot()));
    }

    @Override
    public @NotNull Component aoeType() {
        return Component.translatable("tooltip.fathommod.sweep_type_tooltip", "Scythe").withColor(DevUtils.INFO_TOOLTIPS_HEX);
    }

    @Override
    public SweetSpotRange getSweetSpotRange() {
        return new SweetSpotRange(0.8, 1, 1.2);
    }
}
