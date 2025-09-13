package net.fathommod;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class ClientDevUtils {
    public static void playMusic(Music music) {
        if (!Minecraft.getInstance().getMusicManager().isPlayingMusic(music)) {
            Minecraft.getInstance().getMusicManager().startPlaying(music);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static double calculateAttackDamage(float calculatedDamage) {
        for (AttributeModifier mod : Minecraft.getInstance().player.getAttribute(Attributes.ATTACK_DAMAGE).getModifiers().stream().filter(mod -> mod.operation() == AttributeModifier.Operation.ADD_VALUE).toList())
            if (!mod.id().equals(Item.BASE_ATTACK_DAMAGE_ID))
                calculatedDamage += (float) mod.amount();
        for (AttributeModifier mod : Minecraft.getInstance().player.getAttribute(Attributes.ATTACK_DAMAGE).getModifiers().stream().filter(mod -> mod.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE).toList())
            calculatedDamage += (float) ((mod.amount() + 1) * Minecraft.getInstance().player.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        for (AttributeModifier mod : Minecraft.getInstance().player.getAttribute(Attributes.ATTACK_DAMAGE).getModifiers().stream().filter(mod -> mod.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).toList())
            calculatedDamage *= (float) (mod.amount() + 1);
        return calculatedDamage;
    }

    @SuppressWarnings("DataFlowIssue")
    public static double calculateAttackSpeed(float calculatedAttackSpd) {
        for (AttributeModifier mod : Minecraft.getInstance().player.getAttribute(Attributes.ATTACK_SPEED).getModifiers().stream().filter(mod -> mod.operation() == AttributeModifier.Operation.ADD_VALUE).toList())
            if (!mod.id().equals(Item.BASE_ATTACK_SPEED_ID))
                calculatedAttackSpd += (float) mod.amount();
        for (AttributeModifier mod : Minecraft.getInstance().player.getAttribute(Attributes.ATTACK_SPEED).getModifiers().stream().filter(mod -> mod.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE).toList())
            calculatedAttackSpd += (float) ((mod.amount() + 1) * Minecraft.getInstance().player.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        for (AttributeModifier mod : Minecraft.getInstance().player.getAttribute(Attributes.ATTACK_SPEED).getModifiers().stream().filter(mod -> mod.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).toList())
            calculatedAttackSpd *= (float) (mod.amount() + 1);
        return calculatedAttackSpd;
    }

    public static boolean hasTrinket(Item item) {
        return DevUtils.hasTrinket(Minecraft.getInstance().player, item);
    }
}
