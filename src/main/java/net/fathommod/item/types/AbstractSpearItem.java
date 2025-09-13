package net.fathommod.item.types;

import net.fathommod.DevUtils;
import net.fathommod.FMHitbox;
import net.fathommod.item.SweetSpotItem;
import net.fathommod.item.SweetSpotRange;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSpearItem extends FMMeleeWeapon implements SweetSpotItem {
    public AbstractSpearItem(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("all")
    public static FMHitbox getSweepHitbox(Player player) {
        return new FMHitbox(player.getEyePosition().add(player.getLookAngle().normalize().scale(player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).getValue() / 2)), new Vec3(0.25, 0.25, player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).getValue() / 2), DevUtils.getPlayerRotationQuaternion(player));
    }

    @Override
    public @NotNull Component aoeType() {
        return Component.translatable("tooltip.fathommod.sweep_type_tooltip", "Spear").withColor(DevUtils.INFO_TOOLTIPS_HEX);
    }

    @Override
    public SweetSpotRange getSweetSpotRange() {
        return new SweetSpotRange(.8, 1, 1.3);
    }
}
