package net.fathommod.mixins;

import net.fathommod.DevUtils;
import net.fathommod.FathommodMod;
import net.fathommod.Trinkets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer {
    /**
     * @author IveTouchedGrass
     * @reason Redirect annotation being stubborn
     */
    @org.spongepowered.asm.mixin.Overwrite
    @SuppressWarnings("DataFlowIssue")
    public float getFieldOfViewModifier() {
        AbstractClientPlayer instance = (AbstractClientPlayer) (Object) this;
        float f = 1.0F;
        if (instance.getAbilities().flying) {
            f *= 1.1F;
        }

        double movementSpeed = instance.getAttribute(Attributes.MOVEMENT_SPEED).calculateValue();
        if (DevUtils.hasTrinket(instance, Trinkets.CRACK_ON_CRACK) && !instance.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "crack")))
            movementSpeed = instance.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() * 2;
        else if (DevUtils.hasTrinket(instance, Trinkets.CRACK) && !instance.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "crack")))
            movementSpeed = instance.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() * 3;

        f *= ((float) movementSpeed / instance.getAbilities().getWalkingSpeed() + 1.0F) / 2.0F;
        if (instance.getAbilities().getWalkingSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
            f = 1.0F;
        }

        ItemStack itemstack = instance.getUseItem();
        if (instance.isUsingItem()) {
            if (itemstack.is(Items.BOW)) {
                int i = instance.getTicksUsingItem();
                float f1 = (float)i / 20.0F;
                if (f1 > 1.0F) {
                    f1 = 1.0F;
                } else {
                    f1 *= f1;
                }

                f *= 1.0F - f1 * 0.15F;
            } else if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && instance.isScoping()) {
                return 0.1F;
            }
        }
        return net.neoforged.neoforge.client.ClientHooks.getFieldOfViewModifier(instance, f);
    }
}
