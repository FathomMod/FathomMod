package net.fathommod.init;

import net.fathommod.FathommodMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class FathommodModAttributes {
    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, FathommodMod.MOD_ID);
    public static final DeferredHolder<Attribute, Attribute> ARMOR_DEFENSE = REGISTRY.register("armor_defense", () -> new RangedAttribute("attribute.fathommod.armor_defense", 0, 0, Double.MAX_VALUE));
    public static final DeferredHolder<Attribute, Attribute> ARMOR_DEFENSE_PIERCING_PERCENT = REGISTRY.register("armor_defense_piercing_percent", () -> new RangedAttribute("attribute.fathommod.armor_defense_piercing_percent", 0, 0, 100));
    public static final DeferredHolder<Attribute, Attribute> STEP_DOWN_HEIGHT = REGISTRY.register("step_down_height", () -> new RangedAttribute("attribute.fathommod.step_down_height", 0, 0, 100).setSyncable(true));
    
    @SubscribeEvent
    public static void addAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entity -> event.add(entity, ARMOR_DEFENSE));
        event.getTypes().forEach(entity -> event.add(entity, ARMOR_DEFENSE_PIERCING_PERCENT));
        event.add(EntityType.PLAYER, STEP_DOWN_HEIGHT);
    }
}
