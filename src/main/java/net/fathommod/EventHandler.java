package net.fathommod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.fathommod.entity.ButchersCleaveProjectile;
import net.fathommod.entity.GrenadeProjectile;
import net.fathommod.entity.TNTArrowEntity;
import net.fathommod.entity.ted.TedEntity;
import net.fathommod.entity.ted.TedSpawner;
import net.fathommod.event.TrinketEvent;
import net.fathommod.init.*;
import net.fathommod.item.SweetSpotItem;
import net.fathommod.item.types.AbstractSpearItem;
import net.fathommod.item.types.BluntWeaponItem;
import net.fathommod.item.types.ScytheItem;
import net.fathommod.network.FathommodModPackets;
import net.fathommod.network.FathommodModVariables;
import net.fathommod.trinket.*;
import net.fathommod.world.inventory.TrinkeryMenu;
import net.minecraft.advancements.critereon.PickedUpItemTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"DataFlowIssue", "unused"})
@EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = FathommodMod.MOD_ID)
public class EventHandler {
    @SubscribeEvent
    public static void applyEffects(LivingDamageEvent.Post event) {
        if (event.getNewDamage() <= 0)
            return;
        DamageSource source = event.getSource();
        LivingEntity entity = event.getEntity();
        if (source.is(FathommodModDamageTypes.TED_ROCK)) {
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, entity.onGround() ? 40 : 80, 0, false, false, false));
            entity.addEffect(new MobEffectInstance(FathommodModMobEffects.MOVEMENT_STUN, entity.onGround() ? 40 : 80, 0, false, false));
        }
        if (FathommodModVariables.MapVariables.get(event.getEntity().level()).isMasochistModeEnabled()) {
            if (source.is(FathommodModDamageTypes.TED_ROCK))
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0, false, false, true));
            else if (source.is(FathommodModDamageTypes.TED_SWIPE))
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
        }
        if (source.is(NeoForgeMod.POISON_DAMAGE)) {
            FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
            vars.takenPoisonDamage += event.getNewDamage() / 2;
            vars.syncPlayerVariables(entity);
        }
    }

    @SubscribeEvent
    public static void onEntitySpawn(FinalizeSpawnEvent event) {
        if (event.getEntity() instanceof Warden warden) {
            warden.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(22.5);
        }
        if ((BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()).getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) || BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()).getNamespace().equals(FathommodMod.MOD_ID)) && FathommodModVariables.MapVariables.get(event.getLevel()).isMasochistModeEnabled() && !(event.getEntity() instanceof Animal) && !(event.getEntity() instanceof WaterAnimal)) {
            event.getEntity().getAttribute(Attributes.MAX_HEALTH).addOrReplacePermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "masochist_mode_health_increase"), 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            event.getEntity().heal(event.getEntity().getMaxHealth());
        } else if (event.getEntity() instanceof BossEntity && FathommodModVariables.MapVariables.get(event.getLevel()).isMasochistModeEnabled()) {
            event.getEntity().getAttribute(Attributes.MAX_HEALTH).addOrReplacePermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "masochist_mode_health_increase"), .5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            event.getEntity().heal(event.getEntity().getMaxHealth());
        }
    }

    @SubscribeEvent
    public static void onEntityDeathDrop(LivingDropsEvent event) {
        if (event.getEntity().getData(FathommodModVariables.ENTITY_VARIABLES).isTedRabbit)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onTrinketAdd(TrinketEvent.TrinketAddedEvent event) {
        if (event.stack.getItem() instanceof Trinket trinket) {
            for (ItemStack s : DevUtils.getTrinkets(event.player)) {
                if (s.getItem() instanceof Trinket t && t.incompatibleTrinkets().contains(trinket))
                    return;
            }
            for (Trinket t : trinket.incompatibleTrinkets()) {
                if (DevUtils.hasTrinket(event.player, (Item) t))
                    NeoForge.EVENT_BUS.post(new TrinketEvent.TrinketRemovedEvent(event.player, DevUtils.getFirstTrinketOfType(event.player, (Item) t)));
            }
        }
        if (event.stack.getItem() instanceof OneTimeEffectTrinket trinket && !event.isFromJoining)
            trinket.applyEffect(event.player, event.stack);
        if (event.stack.getItem() instanceof AttributeTrinket trinket)
            event.player.getAttribute(trinket.getAttribute()).addOrUpdateTransientModifier(trinket.getModifier());
        if (event.stack.getItem() instanceof MultiAttributeTrinket trinket) {
            if (trinket.getModifiers().size() != trinket.getAttributes().size())
                throw new RuntimeException("Trinket " + event.stack.getItem().getClass().getSimpleName() + " has conflicting modifier and attribute arrays");
            else {
                for (int i = 0; i < trinket.getModifiers().size(); i++)
                    event.player.getAttribute(trinket.getAttributes().get(i)).addOrUpdateTransientModifier(trinket.getModifiers().get(i));
            }
        }
    }

    @SubscribeEvent
    public static void onTrinketRemove(TrinketEvent.TrinketRemovedEvent event) {
        Item item = event.stack.getItem();

        if (item instanceof OneTimeEffectTrinket trinket) {
            trinket.removeEffect(event.player, event.stack);
        }

        if (item instanceof AttributeTrinket trinket) {
            event.player.getAttribute(trinket.getAttribute()).removeModifier(trinket.getModifier().id());
        }

        if (item instanceof MultiAttributeTrinket trinket) {
            if (trinket.getModifiers().size() != trinket.getAttributes().size()) {
                throw new RuntimeException("Trinket " + item.getClass().getSimpleName() + " has conflicting modifier and attribute arrays");
            } else {
                for (int i = 0; i < trinket.getModifiers().size(); i++) {
                    event.player.getAttribute(trinket.getAttributes().get(i)).removeModifier(trinket.getModifiers().get(i).id());
                }
            }
        }
        if (event.stack.getItem() instanceof Trinket t) {
            for (Trinket tr : t.incompatibleTrinkets())
                if (DevUtils.hasTrinket(event.player, (Item) tr))
                    NeoForge.EVENT_BUS.post(new TrinketEvent.TrinketAddedEvent(event.player, DevUtils.getFirstTrinketOfType(event.player, (Item) tr)));
        }
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        FathommodModVariables.EntityVariables vars = event.getEntity().getData(FathommodModVariables.ENTITY_VARIABLES);
        PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new FathommodModPackets.UpdateMasochistMode(FathommodModVariables.MapVariables.get(event.getEntity().level()).isMasochistModeEnabled()));
        for (ItemStack s : DevUtils.getTrinkets(event.getEntity())) {
            TrinketEvent.TrinketAddedEvent event1 = new TrinketEvent.TrinketAddedEvent(event.getEntity(), s);
            event1.isFromJoining = true;
            NeoForge.EVENT_BUS.post(event1);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        FathommodModVariables.EntityVariables vars = event.getEntity().getData(FathommodModVariables.ENTITY_VARIABLES);
        for (ItemStack s : DevUtils.getTrinkets(event.getEntity())) {
            NeoForge.EVENT_BUS.post(new TrinketEvent.TrinketAddedEvent(event.getEntity(), s));
        }
    }

    @SubscribeEvent
    public static void onItemModifier(ItemAttributeModifierEvent event) {
        ArmorItem item;
        if (event.getItemStack().getItem() instanceof ArmorItem)
            item = (ArmorItem) event.getItemStack().getItem();
        else
            return;
        Holder<ArmorMaterial> material = item.getMaterial();
        ArmorItem.Type type = item.getType();

        if (material == ArmorMaterials.LEATHER || material == ArmorMaterials.GOLD) {
            event.addModifier(FathommodModAttributes.ARMOR_DEFENSE.getDelegate(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "leather_armor_" + type.getName()), .25, AttributeModifier.Operation.ADD_VALUE), switch (type) {
                case HELMET -> EquipmentSlotGroup.HEAD;
                case CHESTPLATE -> EquipmentSlotGroup.CHEST;
                case LEGGINGS -> EquipmentSlotGroup.LEGS;
                case BOOTS -> EquipmentSlotGroup.FEET;
                default -> EquipmentSlotGroup.ANY;
            });
        } else if (material == ArmorMaterials.IRON || material == ArmorMaterials.CHAIN)
            event.addModifier(FathommodModAttributes.ARMOR_DEFENSE.getDelegate(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "iron_armor_" + type.getName()), .5, AttributeModifier.Operation.ADD_VALUE), switch (type) {
                case HELMET -> EquipmentSlotGroup.HEAD;
                case CHESTPLATE -> EquipmentSlotGroup.CHEST;
                case LEGGINGS -> EquipmentSlotGroup.LEGS;
                case BOOTS -> EquipmentSlotGroup.FEET;
                default -> EquipmentSlotGroup.ANY;
            });
        else if (material == ArmorMaterials.DIAMOND)
            event.addModifier(FathommodModAttributes.ARMOR_DEFENSE.getDelegate(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "diamond_armor_" + type.getName()), 1, AttributeModifier.Operation.ADD_VALUE), switch (type) {
                case HELMET -> EquipmentSlotGroup.HEAD;
                case CHESTPLATE -> EquipmentSlotGroup.CHEST;
                case LEGGINGS -> EquipmentSlotGroup.LEGS;
                case BOOTS -> EquipmentSlotGroup.FEET;
                default -> EquipmentSlotGroup.ANY;
            });
        else if (material == ArmorMaterials.NETHERITE)
            event.addModifier(FathommodModAttributes.ARMOR_DEFENSE.getDelegate(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "netherite_armor_" + type.getName()), 2, AttributeModifier.Operation.ADD_VALUE), switch (type) {
                case HELMET -> EquipmentSlotGroup.HEAD;
                case CHESTPLATE -> EquipmentSlotGroup.CHEST;
                case LEGGINGS -> EquipmentSlotGroup.LEGS;
                case BOOTS -> EquipmentSlotGroup.FEET;
                default -> EquipmentSlotGroup.ANY;
            });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void armorReworkHandler(LivingDamageEvent.Pre event) {
        if (!ServerTempVars.shouldUseFMDamageFormula)
            return;
        event.setNewDamage(calculateNewDamage(event.getSource(), damageContainers.get(event.getContainer()), event.getEntity()));
    }

    public static float calculateNewDamage(DamageSource source, float originalDamage, LivingEntity entity) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) // if the type bypasses armor then there's no point in calculating as enchants only increase the effectiveness of AP, and AD is bypassed
            return originalDamage;
        if (source.is(FathommodModDamageTypes.TED_INSTA_KILL) && originalDamage >= 100) {
            return Float.MAX_VALUE;
        }
        if (source.is(DamageTypeTags.BYPASSES_ARMOR))
            return originalDamage;
        int resistanceAmplifier = 0;
        try {
            resistanceAmplifier = entity.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1;
        } catch (NullPointerException ignored) {} // entity doesnt have resistance
        float armorPiercing = 0f;
        // apply any armor piercings here for them to be calculated BEFORE armor defense
        armorPiercing += .0625f * (source.getEntity() != null ? DevUtils.getEnchantLevel(source.getEntity().getWeaponItem(), Enchantments.BREACH, entity.level()) : 0); // apply breach
        armorPiercing += (source.getEntity() instanceof LivingEntity attacker ? (float) attacker.getAttribute(FathommodModAttributes.ARMOR_DEFENSE_PIERCING_PERCENT).getValue() / 100 : 0); // apply armor defense piercing attribute
        armorPiercing += source.getEntity() instanceof LivingEntity attacker && attacker.getMainHandItem().getItem() instanceof BluntWeaponItem ? .15f : 0;

        float armorDefense = (float) entity.getAttribute(FathommodModAttributes.ARMOR_DEFENSE.getDelegate()).getValue()
                * Math.max(0, 1 - armorPiercing);
        // apply any armor piercings here for them to be calculated AFTER armor defense, but before other stuff
        if (entity instanceof Player) {
            if (!FathommodModVariables.MapVariables.get(entity.level()).isMasochistModeEnabled() && (!entity.level().getDifficulty().equals(Difficulty.HARD) || source.getEntity() instanceof BossEntity)) {
                if (entity.level().getDifficulty().equals(Difficulty.HARD) && !(source.getEntity() instanceof BossEntity))
                    armorDefense *= .75f;
                else
                    armorDefense *= .5f;
            }
        }

        float damage = Math.max(Math.max(originalDamage * (1 - (0.2f * resistanceAmplifier)), 0) // apply vanilla resistance
                - armorDefense, Math.min(1, Math.max(originalDamage * (1 - (0.2f * resistanceAmplifier)), 0) // apply vanilla resistance
                - armorDefense));
        if (source.is(FathommodModDamageTypes.TED_INSTA_KILL))
            return originalDamage;
        float armorPointsFromArmor = 0;
        EquipmentSlot[] armorSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (EquipmentSlot s : armorSlots) {
            Optional<ItemAttributeModifiers.Entry> entry = entity.getItemBySlot(s).getAttributeModifiers().modifiers().stream().filter(_entry -> _entry.attribute() == Attributes.ARMOR).findFirst();
            armorPointsFromArmor += entry.map(value -> (float) value.modifier().amount()).orElse(0F);
        }
        float naturalArmorPoints = (float) entity.getAttribute(Attributes.ARMOR).getValue() - armorPointsFromArmor;
        float protectionFromNaturalAP = .04f * naturalArmorPoints;
        float protectionFromArmorAP = .04f * armorPointsFromArmor;
        if (entity instanceof Player) {
            if (!FathommodModVariables.MapVariables.get(entity.level()).isMasochistModeEnabled() && (!entity.level().getDifficulty().equals(Difficulty.HARD) || source.getEntity() instanceof BossEntity)) {
                if (entity.level().getDifficulty().equals(Difficulty.HARD) && !(source.getEntity() instanceof BossEntity))
                    protectionFromArmorAP *= .875f;
                else
                    protectionFromArmorAP *= .75f;
            }
        }
        float enchantProt = 1f;
        float specialEnchantProt = 0f;
        if (!source.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
            for (EquipmentSlot s : armorSlots) {
                if (entity.getItemBySlot(s).isEmpty())
                    continue;
                short protLevel = DevUtils.getEnchantLevel(entity.getItemBySlot(s), Enchantments.PROTECTION, entity.level());
                short fireProtLevel = DevUtils.getEnchantLevel(entity.getItemBySlot(s), Enchantments.FIRE_PROTECTION, entity.level());
                short projProtLevel = DevUtils.getEnchantLevel(entity.getItemBySlot(s), Enchantments.PROJECTILE_PROTECTION, entity.level());
                short blastProtLevel = DevUtils.getEnchantLevel(entity.getItemBySlot(s), Enchantments.BLAST_PROTECTION, entity.level());
                if (protLevel >= 1)
                    enchantProt += (float) protLevel / 100f;
                else if (source.is(DamageTypeTags.IS_FIRE) && fireProtLevel >= 1)
                    specialEnchantProt += (.06f + ((fireProtLevel - 1) / 100f) * 3);
                else if (source.is(DamageTypeTags.IS_EXPLOSION) && blastProtLevel >= 1)
                    specialEnchantProt += (.06f + ((blastProtLevel - 1) / 100f) * 3);
                else if (source.is(DamageTypeTags.IS_PROJECTILE) && projProtLevel >= 1)
                    specialEnchantProt += (.06f + ((projProtLevel - 1) / 100f) * 3);
            }
        }
        protectionFromArmorAP *= enchantProt;
        protectionFromArmorAP *= Math.max(0, 1 - armorPiercing);
        protectionFromNaturalAP *= Math.max(0, 1 - armorPiercing);
        if (entity.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue() <= 0) {
            if (originalDamage > entity.getAttribute(Attributes.ARMOR).getValue() / 2)
                protectionFromArmorAP *= .75f;
            else if (originalDamage > entity.getAttribute(Attributes.ARMOR).getValue() / 1.5)
                protectionFromArmorAP *= .5f;
        } else if (originalDamage > 2 * entity.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue())
            protectionFromArmorAP *= .6f;
        else if (originalDamage > 1.5 * entity.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue())
            protectionFromArmorAP *= .8f;
        final float protection = protectionFromArmorAP + protectionFromNaturalAP;
        return damage * Math.clamp(1 - (protection + specialEnchantProt), .04f, 1);
    }
    
    @SubscribeEvent
    public static void onEntitySetTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel))
            return;
        if (event.getNewAboutToBeSetTarget() != null && event.getNewAboutToBeSetTarget().getData(FathommodModVariables.ENTITY_VARIABLES).isGodMode)
            event.setNewAboutToBeSetTarget(null);
        if (event.getNewAboutToBeSetTarget() != null && event.getEntity().getData(FathommodModVariables.ENTITY_VARIABLES).isSummon && Objects.equals(event.getEntity().getData(FathommodModVariables.ENTITY_VARIABLES).summonOwner, event.getNewAboutToBeSetTarget().getUUID())) {
            event.setNewAboutToBeSetTarget(null);
        }
        LivingEntity entity = event.getEntity();
        if (event.getNewAboutToBeSetTarget() == null && event.getEntity().getData(FathommodModVariables.ENTITY_VARIABLES).isSummon) {
            for (LivingEntity entityiterator : event.getEntity().level().getEntitiesOfClass(Mob.class, new AABB(entity.getX() - 25, entity.getY() - 25, entity.getZ() - 25, entity.getX() + 25,  entity.getY() + 25 ,entity.getZ() + 25))) {
                if (entityiterator != ((ServerLevel) entity.level()).getEntity(entity.getData(FathommodModVariables.ENTITY_VARIABLES).summonOwner) && (entityiterator instanceof Mob mob && mob.getTarget() == ((ServerLevel) entity.level()).getEntity(entity.getData(FathommodModVariables.ENTITY_VARIABLES).summonOwner)) && entity.canAttack(entityiterator)) {
                    event.setNewAboutToBeSetTarget(entityiterator);
                }
            }
        }
    }

    public static List<ResourceLocation> getModRecipeItemKeys(MinecraftServer server, RecipeHolder<?> holder) {
        RecipeManager recipeManager = server.getRecipeManager();
        String modNamespace = FathommodMod.MOD_ID;

        List<Ingredient> ingredients = switch (holder.value()) {
            case CraftingRecipe crafting -> crafting.getIngredients();
            case AbstractCookingRecipe cooking -> cooking.getIngredients();
            default -> Collections.emptyList();
        };

        List<ResourceLocation> ingredients_to_return = new ArrayList<>();

        for (Ingredient ingredient : ingredients) {
            Arrays.stream(ingredient.getItems()).forEach(stack -> ingredients_to_return.add(BuiltInRegistries.ITEM.getKey(stack.getItem())));
        }

        return ingredients_to_return;
    }

    public static List<RecipeHolder<?>> getModRecipes(MinecraftServer server) {
        RecipeManager recipeManager = server.getRecipeManager();
        String modNamespace = FathommodMod.MOD_ID;

        List<RecipeHolder<?>> modRecipes = new ArrayList<>();

        recipeManager.getRecipes().stream().filter(holder -> holder.id().getNamespace().equals(FathommodMod.MOD_ID)).forEach(modRecipes::add);

        return modRecipes;
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        ItemStack stack = event.getItemEntity().getItem();
        Item pickedUpItem = event.getItemEntity().getItem().getItem();
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(pickedUpItem);
        List<RecipeHolder<?>> recipes = getModRecipes(player.getServer());
        List<RecipeHolder<?>> recipesToAward = new ArrayList<>();
        recipes.forEach(holder -> {
            List<ResourceLocation> ingredients = getModRecipeItemKeys(player.getServer(), holder);
            for (ResourceLocation location : ingredients)
                if (itemKey == location) {
                    recipesToAward.add(holder);
                    break;
                }
        });
        player.awardRecipes(recipesToAward);
        if (event.getItemEntity().getItem().get(DataComponents.CUSTOM_DATA) != null && event.getItemEntity().getItem().get(DataComponents.CUSTOM_DATA).copyTag().getBoolean("__fathommod__unbreakable_by_trinket") && !DevUtils.hasTrinket(player, Trinkets.UNBREAKABILITY)) {
            CustomData.update(DataComponents.CUSTOM_DATA, event.getItemEntity().getItem(), tag -> tag.putBoolean("__fathommod__unbreakable_by_trinket", false));
            event.getItemEntity().getItem().remove(DataComponents.UNBREAKABLE);
        }
    }

    @SubscribeEvent
    public static void spearSweep(LivingDamageEvent.Post event) {
        Player player;
        if (event.getSource().getEntity() instanceof Player p)
            player = p;
        else return;
        double charge = player.getAttackStrengthScale(0);
        Item item = player.getMainHandItem().getItem();
        if (item instanceof AbstractSpearItem && charge >= 0.85) {
            LivingEntity target = event.getEntity();
            if (target.getData(FathommodModVariables.ENTITY_VARIABLES).hasBeenHitByCustomSweep) {
                return;
            }
            FMHitbox spearHitbox = AbstractSpearItem.getSweepHitbox(player);
            spearHitbox.queueForRenderingOnClient((ServerLevel) player.level(), 2000);
            for (LivingEntity entity : spearHitbox.getEntitiesOfClass(LivingEntity.class, (ServerLevel) player.level(), entity -> !entity.getStringUUID().equals(player.getStringUUID()))) {
                FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
                vars.hasBeenHitByCustomSweep = true;
                entity.hurt(new DamageSource(player.level().holderOrThrow(DamageTypes.PLAYER_ATTACK), player), event.getOriginalDamage());
            }
        }
    }

    @SubscribeEvent
    public static void onArmorChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot().getType() != EquipmentSlot.Type.HUMANOID_ARMOR || !(event.getEntity() instanceof ServerPlayer player))
            return;
        ItemStack stack = event.getTo();
        if (!DevUtils.hasTrinket(player, Trinkets.UNBREAKABILITY)) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (tryOrDefault(Boolean.class, NullPointerException.class, false, () -> data.copyTag().getBoolean("__fathommod__unbreakable_by_trinket"))) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean("__fathommod__unbreakable_by_trinket", false));
                stack.remove(DataComponents.UNBREAKABLE);
            }
        } else {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data == null) {
                CompoundTag tag = new CompoundTag();
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
            if (!stack.has(DataComponents.UNBREAKABLE)) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean("__fathommod__unbreakable_by_trinket", true));
                stack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
            }
        }
        stack = event.getFrom();
        if (!stack.isEmpty()) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data == null) {
                CompoundTag tag = new CompoundTag();
                data = CustomData.of(tag);
                stack.set(DataComponents.CUSTOM_DATA, data);
            }
            if (data.copyTag().getBoolean("__fathommod__unbreakable_by_trinket")) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean("__fathommod__unbreakable_by_trinket", false));
                stack.remove(DataComponents.UNBREAKABLE);
            }
        }
    }

    @SubscribeEvent
    private static void onMainHandItemChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.MAINHAND || !(event.getEntity() instanceof Player player))
            return;
        ItemStack newStack = event.getTo();
        ItemStack oldStack = event.getFrom();
        if (newStack.getItem() instanceof TwoHandedItem item) {
            ItemStack originalOffhandItem = player.getOffhandItem();
            FathommodModVariables.EntityVariables vars = player.getData(FathommodModVariables.ENTITY_VARIABLES);
            if (!vars.replacedOffhandItem.isEmpty() && !tryOrDefault(CompoundTag.class, NullPointerException.class, new CompoundTag(), () -> originalOffhandItem.get(DataComponents.CUSTOM_DATA).copyTag()).getBoolean("__fathommod__copy_of_two_handed_item") && !(oldStack.getItem() instanceof TwoHandedItem))
                player.addItem(vars.replacedOffhandItem);
            if (!tryOrDefault(CompoundTag.class, NullPointerException.class, new CompoundTag(), () -> originalOffhandItem.get(DataComponents.CUSTOM_DATA).copyTag()).getBoolean("__fathommod__copy_of_two_handed_item") && !originalOffhandItem.isEmpty())
                vars.replacedOffhandItem = originalOffhandItem;
            if (item.shouldDisplayItemInOffhand()) {
                ItemStack newOffhandItem = newStack.copy();
                CustomData.update(DataComponents.CUSTOM_DATA, newOffhandItem, tag -> tag.putBoolean("__fathommod__copy_of_two_handed_item", true));
                player.setItemSlot(EquipmentSlot.OFFHAND, newOffhandItem);
            } else {
                player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
            vars.syncPlayerVariables(player);
        } else if (oldStack.getItem() instanceof TwoHandedItem item) {
            FathommodModVariables.EntityVariables vars = player.getData(FathommodModVariables.ENTITY_VARIABLES);
            ItemStack oldOffhandStack = player.getOffhandItem();
            player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            if (vars.replacedOffhandItem.isEmpty() || (!tryOrDefault(CompoundTag.class, NullPointerException.class, new CompoundTag(), () -> oldOffhandStack.get(DataComponents.CUSTOM_DATA).copyTag()).getBoolean("__fathommod__copy_of_two_handed_item") && !oldOffhandStack.isEmpty()))
                return;
            player.setItemSlot(EquipmentSlot.OFFHAND, vars.replacedOffhandItem);
            vars.replacedOffhandItem = ItemStack.EMPTY;
        }
    }

    public static <T, E extends Throwable> T tryOrDefault(@SuppressWarnings("all") Class<T> type, @SuppressWarnings("all") Class<E> errorType, T defaultValue, Callable<T> func) {
        try {
            return func.call();
        } catch (Exception e) {
            try {
                //noinspection unchecked
                E e1 = (E) e;
            } catch (NullPointerException ex) {
                throw new RuntimeException(e);
            }
            return defaultValue;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void scytheSweep(LivingDamageEvent.Post event) {
        Player player;
        if (event.getNewDamage() <= 0)
            return;
        if (event.getSource().getEntity() instanceof Player p)
            player = p;
        else return;
        double charge = player.getAttackStrengthScale(0);
        Item item = player.getMainHandItem().getItem();
        if (item instanceof ScytheItem && charge >= 0.85) {
            LivingEntity target = event.getEntity();
            if (target.getData(FathommodModVariables.ENTITY_VARIABLES).hasBeenHitByCustomSweep) {
                return;
            }
            @Nullable
            Tuple<Entity, Vec3> raycastHit = DevUtils.performPreciseRaycast(player, player.level(), player.getEyePosition(), player.getLookAngle().normalize(), player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).getValue() + .5);
            FMHitbox scytheHitbox = ScytheItem.getSweepHitbox(target, raycastHit, player);
            scytheHitbox.queueForRenderingOnClient((ServerLevel) player.level(), 2000);
            for (LivingEntity entity : scytheHitbox.getEntitiesOfClass(LivingEntity.class, (ServerLevel) player.level(), entity -> entity != player)) {
                FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
                vars.hasBeenHitByCustomSweep = true;
                entity.hurt(new DamageSource(player.level().holderOrThrow(DamageTypes.PLAYER_ATTACK), player), event.getOriginalDamage());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity().getData(FathommodModVariables.ENTITY_VARIABLES).isGodMode || event.getSource().getEntity() instanceof GrenadeProjectile)
            event.setCanceled(true);
    }

    private static ArrayList<int[]> generateSpawnLocationsArray(double originX, double originZ) {
        int A = (int) originX;
        int B = (int) originZ;

        ArrayList<int[]> points = new ArrayList<>();

        for (int x = A - 30; x <= A + 30; x++) {
            for (int z = B - 30; z <= B + 30; z++) {
                if (Math.abs(x - A) > 15 || Math.abs(z - B) > 15) {
                    points.add(new int[]{x, z});
                }
            }
        }

        return points;
    }

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemstack = event.getItemStack();
        Player entity = event.getEntity();
        Level world = event.getLevel();
        if (!(world instanceof ServerLevel))
            return;
        if (itemstack.getItem() == Items.FIREWORK_ROCKET && itemstack.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.INFINITY)) > 0 && !entity.hasInfiniteMaterials()) {
            itemstack.grow(1);
        }
        if (itemstack.is(FathommodModItems.BUTCHERS_CLEAVE.get()) && !entity.getCooldowns().isOnCooldown(FathommodModItems.BUTCHERS_CLEAVE.get())) {
            ButchersCleaveProjectile projectile = new ButchersCleaveProjectile(world);
            projectile.damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            projectile.setOwner(entity);
            projectile.setPos(entity.getEyePosition());
            projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0, 1, 0);
            entity.getCooldowns().addCooldown(FathommodModItems.BUTCHERS_CLEAVE.get(), 100);
            world.addFreshEntity(projectile);
        }
        if (itemstack.getItem() == FathommodModItems.TED_SPAWNER.get() && !world.isClientSide() && world.getEntitiesOfClass(TedEntity.class, new AABB(entity.getX() - 200, entity.getY() - 200, entity.getZ() - 200, entity.getX() + 200, entity.getY() + 200, entity.getZ() + 200)).isEmpty() && world.getEntitiesOfClass(TedSpawner.class, new AABB(entity.getX() - 200, entity.getY() - 200, entity.getZ() - 200, entity.getX() + 200, entity.getY() + 200, entity.getZ() + 200)).isEmpty()) {
            ArrayList<int[]> list = generateSpawnLocationsArray(entity.getX(), entity.getZ());
            Collections.shuffle(list);
            for (int[] point : list) {
                ChunkAccess chunk = world.getChunk(point[0] >> 4, point[1] >> 4);
                if (chunk instanceof LevelChunk lvlChunk) {
                    if (entity.level().clip(
                            new ClipContext(
                                    new Vec3(point[0], entity.getY() + 6, point[1]),
                                    new Vec3(point[0], entity.getY() - 5, point[1]),
                                    ClipContext.Block.COLLIDER,
                                    ClipContext.Fluid.NONE,
                                    entity
                            )
                    ) instanceof BlockHitResult result && result.getType() != HitResult.Type.MISS) {
                        TedSpawner ted = new TedSpawner(FathommodModEntities.TED_SPAWNER.get(), world);
                        ted.teleportTo(result.getBlockPos().getX(), result.getBlockPos().getY() + 1, result.getBlockPos().getZ());
                        world.addFreshEntity(ted);
                        break;
                    }
                }
            }
        }
    }

    static HashMap<UUID, List<ItemStack>> previousInventoryMap = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player entity = event.getEntity();
        FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
        if (entity.getCooldowns().isOnCooldown(entity.getUseItem().getItem()))
            entity.stopUsingItem();
        if (DevUtils.hasTrinket(entity, Trinkets.UNBREAKABILITY))
            return;
        UUID uuid = entity.getUUID();

        List<ItemStack> current = getFullInventorySnapshot(entity);
        List<ItemStack> previous = previousInventoryMap.get(uuid);

        List<ItemStack> diffs = getDiffs(previous, current);
        if (!diffs.isEmpty()) {
            for (ItemStack stack : diffs) {
                CustomData data = stack.get(DataComponents.CUSTOM_DATA);
                if (tryOrDefault(Boolean.class, NullPointerException.class, false, () -> data.copyTag().getBoolean("__fathommod__unbreakable_by_trinket"))) {
                    CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean("__fathommod__unbreakable_by_trinket", false));
                    stack.remove(DataComponents.UNBREAKABLE);
                }
            }
        }

        previousInventoryMap.put(uuid, current);
    }

    private static List<ItemStack> getFullInventorySnapshot(Player player) {
        List<ItemStack> snapshot = new ArrayList<>();
        snapshot.addAll(player.getInventory().items);
        snapshot.addAll(player.getInventory().armor);
        snapshot.add(player.getInventory().offhand.getFirst());
        return snapshot;
    }

    private static ArrayList<ItemStack> getDiffs(List<ItemStack> a, List<ItemStack> b) {
        if (a == null || b == null || a.size() != b.size()) return new ArrayList<>();
        ArrayList<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            if (!ItemStack.matches(a.get(i), b.get(i)))
                list.add(b.get(i));
        }
        return list;
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Applicable event) {
        MobEffect effect = event.getEffectInstance().getEffect().value();
        if (event.getEntity() instanceof TedEntity)
            event.setResult(effect.isBeneficial() || effect.getClass().getName().contains("ComboHitEffect") ? MobEffectEvent.Applicable.Result.APPLY : MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        event.getEntity().getData(FathommodModVariables.ENTITY_VARIABLES).hasBeenHitByCustomSweep = false;
        if (ServerTempVars.serverTickAge % 20 != 0) {
            return;
        }
        if (!(event.getEntity().level() instanceof ServerLevel))
            return;
        if (event.getEntity() instanceof LivingEntity entity && entity.level() instanceof ServerLevel world) {
            FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
            if (vars.isSummon && (world.getEntity(vars.summonOwner) != null || vars.summonTimeLeft-- <= 0)) {
                entity.discard();
            }
            try {
                if (vars.isSummon && (entity instanceof Mob mob) && mob.getTarget() == null) {
                    mob.getNavigation().moveTo(world.getEntity(entity.getData(FathommodModVariables.ENTITY_VARIABLES).summonOwner), 0.75d);
                }
            } catch (NullPointerException ignored) {}
            vars.syncPlayerVariables(entity);

            if (entity instanceof Mob mob && mob.getData(FathommodModVariables.ENTITY_VARIABLES).isTedRabbit) {
                for (Entity entityiterator : event.getEntity().level().getEntities(entity, new AABB(entity.getX() - 50, entity.getY() - 50, entity.getZ() - 50, entity.getX() + 50, entity.getY() + 50,entity.getZ() + 50))) {
                    if (entityiterator instanceof Player player && mob.getTarget() == null && mob.canAttack(player)) {
                        mob.setTarget(player);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        ServerTempVars.serverTickAge++;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player entity = event.getEntity();

        FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);

        if (!(entity instanceof ServerPlayer))
            return;

        for (ItemStack s : DevUtils.getTrinkets(entity)) {
            if (s.getItem() instanceof TickTrinket trinket) {
                trinket.tick(entity);
            }
        }

        if (entity.onGround()) {
            vars.doubleJumpCooldown = false;
            vars.secondDoubleJumpUsed = false;
        }

        if (entity.onGround() && vars.windBurstCooldown <= 0) {
            vars.usedWindBurstCharges = 0;
        }
        vars.windBurstCooldown = Math.max(vars.windBurstCooldown - 1, 0);
        vars.syncPlayerVariables(entity);
    }

    @SubscribeEvent
    private static void sweetSpotHandler(LivingIncomingDamageEvent event) {
        try {
            if (event.getSource().getEntity() instanceof LivingEntity livingEntity && livingEntity.getMainHandItem().getItem() instanceof SweetSpotItem item && item.getSweetSpotRange() != null) {
                if (item.getSweetSpotRange().isInRange(Math.sqrt(livingEntity.distanceToSqr(event.getEntity())), livingEntity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE).getValue())) {
                    event.setAmount(event.getAmount() * item.getSweetSpotRange().damageMultiplier);
                }
            }
        } catch (NullPointerException ignored) {}
    }

    @SubscribeEvent
    public static void afterEntityDamage(LivingDamageEvent.Post event) {
        if (event.getEntity().level().isClientSide())
            return;
        if (event.getSource().getEntity() instanceof ServerPlayer player && player.getItemBySlot(EquipmentSlot.MAINHAND).getItem() == FathommodModItems.METAL_BAT.get()) {
            player.level().playSound(null, player.blockPosition(), FathommodModSounds.BONK.get(), SoundSource.PLAYERS, 2.75f, 1f);
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide())
            return;
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        Entity sourceentity = source.getEntity();

        if (source.is(FathommodModDamageTypes.FAKE_EXPLOSION) && entity == sourceentity)
            event.setCanceled(true);

        if (sourceentity instanceof BossEntity && entity.invulnerableTime > 0) {
            entity.invulnerableTime = 0;
            entity.hurt(source, event.getOriginalAmount());
        }

        if (sourceentity instanceof Rabbit rabbit && rabbit.getData(FathommodModVariables.ENTITY_VARIABLES).isTedRabbit) {
            source.type = entity.level().holderOrThrow(FathommodModDamageTypes.SKILL_ISSUE);
        }

        if (!source.is(DamageTypes.THORNS) && sourceentity != null && DevUtils.sumArmorEnchantmentLevels(entity, Enchantments.THORNS) > 0)
            sourceentity.hurt(new DamageSource(sourceentity.level().holderOrThrow(DamageTypes.THORNS), entity), DevUtils.sumArmorEnchantmentLevels(entity, Enchantments.THORNS));
    }

    @SubscribeEvent
    private static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        AbstractContainerMenu menu = player.containerMenu;

        if (menu instanceof TrinkeryMenu customMenu) {
            FathommodModVariables.EntityVariables variables = event.getEntity().getData(FathommodModVariables.ENTITY_VARIABLES);
            variables.replacedOffhandItem = customMenu.getSlot(4).getItem();
        }
    }

    public static final IdentityHashMap<DamageContainer, Float> damageContainers = new IdentityHashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingShieldBlock(LivingShieldBlockEvent event) {
        if (!event.getBlocked())
            return;
        if (event.getDamageSource().getEntity() instanceof TedEntity ted && event.getEntity() instanceof Player player) {
            Item item = player.getUseItem().getItem();
            ItemStack stack = player.getUseItem();
            event.setShieldDamage(0);
            DamageSource source = event.getDamageSource();
            if (source.is(FathommodModDamageTypes.TED_ROCK))
                event.getEntity().getUseItem().hurtAndBreak(75, player, EquipmentSlot.MAINHAND);
            else if (source.is(FathommodModDamageTypes.TED_SWIPE))
                event.getEntity().getUseItem().hurtAndBreak(150, player, EquipmentSlot.MAINHAND);
            else if (source.is(FathommodModDamageTypes.TED_INSTA_KILL))
                event.getEntity().getUseItem().hurtAndBreak(event.getEntity().getUseItem().getMaxDamage(), player, EquipmentSlot.MAINHAND);
            player.getCooldowns().addCooldown(item, event.getDamageSource().is(FathommodModDamageTypes.TED_ROCK) ? 300 : (event.getDamageSource().is(FathommodModDamageTypes.TED_SWIPE) ? 500 : (event.getDamageSource().is(FathommodModDamageTypes.TED_INSTA_KILL) ? 1800 : 0)));
            if (event.getDamageSource().is(FathommodModDamageTypes.TED_INSTA_KILL)) {
                event.setBlockedDamage(event.getOriginalBlockedDamage() - ((TedEntity) event.getDamageSource().getEntity()).swipeDamage);
                event.setShieldDamage(stack.getMaxDamage());
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, ted.isEnraged ? 1 : 0));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, ted.isEnraged ? 1 : 0));
                return;
            }
        }
        if (event.getBlockedDamage() >= 20 && event.getEntity() instanceof Player) {
            event.setBlockedDamage(event.getBlockedDamage() - (event.getBlockedDamage() / 2));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    private static void onHeal(LivingHealEvent event) {
        if (event.getEntity().hasEffect(FathommodModMobEffects.BLEED.getDelegate())) {
            event.setAmount(event.getAmount() * .25f);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void applyPoisonHealCap(LivingHealEvent event) {
        FathommodModVariables.EntityVariables vars = event.getEntity().getData(FathommodModVariables.ENTITY_VARIABLES);
        if (vars.takenPoisonDamage == 0)
            return;
        if (event.getAmount() + event.getEntity().getHealth() >= vars.getPosionAffectedCap(event.getEntity().getMaxHealth())) {
            event.setAmount(vars.getPosionAffectedCap(event.getEntity().getMaxHealth()) - event.getEntity().getHealth());
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void maceDamageCap(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof BossEntity && event.getSource().is(DamageTypes.MOB_ATTACK))
            event.setCanceled(true);
        if (event.getSource().getEntity() != null && event.getSource().getEntity().getWeaponItem() != null && event.getSource().getEntity().getWeaponItem().getItem() == Items.MACE && event.getAmount() > 50 + (5 * DevUtils.getEnchantLevel(event.getSource().getEntity().getWeaponItem(), Enchantments.DENSITY, event.getEntity().level()))) {
            event.setAmount(Math.min(50 + (5 * DevUtils.getEnchantLevel(event.getSource().getEntity().getWeaponItem(), Enchantments.DENSITY, event.getEntity().level())) * (event.getEntity() instanceof BossEntity ? 1 : 2), event.getAmount()));
        }
    }

    @SubscribeEvent
    public static void onEntityAttacked(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide())
            return;
        Entity sourceentity = event.getSource().getEntity();
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (arrowSharpenerHandler(source) && sourceentity != null) {
            event.setNewDamage(event.getNewDamage() * 1.2f);
        }
        if (source.getEntity() instanceof Player player && DevUtils.hasTrinket(player, Trinkets.RING_OF_POWER) && (player.getAttackStrengthScale(0) >= 1 || !source.is(DamageTypes.PLAYER_ATTACK)))
            event.setNewDamage(event.getNewDamage() + 2);
        if (sourceentity instanceof LivingEntity livingEntity && livingEntity.getItemBySlot(EquipmentSlot.MAINHAND).getItem() == FathommodModItems.TED_CLAWS.get() && !source.is(FathommodModDamageTypes.TED_WEAPON_COMBO)) {
            FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
            vars.syncPlayerVariables(livingEntity);
            entity.invulnerableTime = 0;
            FathommodMod.queueServerWork(7, () -> {
                float multiHitDamage = event.getOriginalDamage();
                entity.invulnerableTime = 0;
                entity.hurt(new DamageSource(entity.level().holderOrThrow(FathommodModDamageTypes.TED_WEAPON_COMBO), event.getSource().getEntity()), multiHitDamage);
                entity.invulnerableTime = 0;
                FathommodMod.queueServerWork(7, () -> {
                    entity.invulnerableTime = 0;
                    entity.hurt(new DamageSource(entity.level().holderOrThrow(FathommodModDamageTypes.TED_WEAPON_COMBO), event.getSource().getEntity()), multiHitDamage);
                }, event.getEntity().level());
            }, event.getEntity().level());
        }
    }

    public static boolean arrowSharpenerHandler(DamageSource source) {
        Entity _sourceentity = source.getEntity();
        if (_sourceentity instanceof LivingEntity sourceentity && (source.getDirectEntity() != null || source.is(DamageTypeTags.IS_PROJECTILE)))
            return (DevUtils.hasTrinket(sourceentity, Trinkets.ARROW_SHARPENER));
        else
            return false;
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ItemStack oldOffhandStack = event.getEntity().getOffhandItem();
        if (tryOrDefault(CompoundTag.class, NullPointerException.class, new CompoundTag(), () -> oldOffhandStack.get(DataComponents.CUSTOM_DATA).copyTag()).getBoolean("__fathommod__copy_of_two_handed_item"))
            event.getEntity().setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        Player player = event.getEntity();
        ItemStack mainHandStack = player.getMainHandItem();
        if (mainHandStack.getItem() instanceof TwoHandedItem item) {
            ItemStack originalOffhandItem = player.getOffhandItem();
            FathommodModVariables.EntityVariables vars = player.getData(FathommodModVariables.ENTITY_VARIABLES);
            if (!vars.replacedOffhandItem.isEmpty() && !tryOrDefault(CompoundTag.class, NullPointerException.class, new CompoundTag(), () -> originalOffhandItem.get(DataComponents.CUSTOM_DATA).copyTag()).getBoolean("__fathommod__copy_of_two_handed_item"))
                player.addItem(vars.replacedOffhandItem);
            if (!tryOrDefault(CompoundTag.class, NullPointerException.class, new CompoundTag(), () -> originalOffhandItem.get(DataComponents.CUSTOM_DATA).copyTag()).getBoolean("__fathommod__copy_of_two_handed_item"))
                vars.replacedOffhandItem = originalOffhandItem;
            if (item.shouldDisplayItemInOffhand()) {
                ItemStack newOffhandItem = mainHandStack.copy();
                CustomData.update(DataComponents.CUSTOM_DATA, newOffhandItem, tag -> tag.putBoolean("__fathommod__copy_of_two_handed_item", true));
                player.setItemSlot(EquipmentSlot.OFFHAND, newOffhandItem);
            } else {
                player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
            vars.syncPlayerVariables(player);
        }
        if (event.getEntity() instanceof ServerPlayer _player)
            PacketDistributor.sendToPlayer(_player, new FathommodModPackets.UpdateLightingPerms(DevUtils.hasTrinket(player, Trinkets.LIGHT)));
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        onEffectLost(event.getEffect(), event.getEntity());
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        onEffectLost(event.getEffectInstance().getEffect(), event.getEntity());
    }

    private static void onEffectLost(Holder<MobEffect> effect, LivingEntity entity) {
        if (effect.is(ResourceKey.create(Registries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "fatal_poison")))) {
            FathommodModVariables.EntityVariables vars = entity.getData(FathommodModVariables.ENTITY_VARIABLES);
            vars.isPaltnPoisoned = false;
            vars.syncPlayerVariables(entity);
        }
    }

    @SubscribeEvent
    private static void shootAmmo(ArrowLooseEvent event) {
        if (event.getEntity().getProjectile(event.getBow()).is(FathommodModItems.TNT_ARROW_ITEM.get()) && event.getBow().is(Items.BOW)) {
            event.setCanceled(true);
            Player player = event.getEntity();
            if (player.hasInfiniteMaterials() || event.hasAmmo()) {
                Projectile projectile = new TNTArrowEntity(FathommodModEntities.TNT_ARROW.get(), player.level(), 0);
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, BowItem.getPowerForTime(event.getCharge()), 0);
                projectile.teleportTo(player.getX(), player.getEyeY(), player.getZ());
                projectile.setOwner(player);
                player.level().addFreshEntity(projectile);
                player.getCooldowns().addCooldown(event.getBow().getItem(), 40);
                if (!player.hasInfiniteMaterials())
                    event.getEntity().getProjectile(event.getBow()).shrink(1);
            }
        }
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    private static class ModEvents {
        private static class FMRecipeProvider extends RecipeProvider {
            public FMRecipeProvider(PackOutput p_248933_, CompletableFuture<HolderLookup.Provider> p_323846_) {
                super(p_248933_, p_323846_);
            }

            @Override
            protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FathommodModItems.BALLOON.get())
                        .define('a', FathommodModItems.FABRIC.get())
                        .define('b', Items.STRING)
                        .pattern(" aa")
                        .pattern(" aa")
                        .pattern("bb ")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FathommodModItems.WOOD_BAT.get())
                        .define('a', ItemTags.PLANKS)
                        .define('c', Items.IRON_INGOT)
                        .pattern("a")
                        .pattern("a")
                        .pattern("c")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FathommodModItems.BLACK_SMITHS_WILL.get())
                        .define('a', Items.IRON_BLOCK)
                        .define('b', Items.STICK)
                        .pattern("aaa")
                        .pattern(" b ")
                        .pattern(" b ")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FathommodModItems.BOXING_GLOVES.get())
                        .define('a', FathommodModItems.FABRIC.get())
                        .define('b', Items.WHITE_WOOL)
                        .pattern("aaa")
                        .pattern("bbb")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Trinkets.CHAINED_HANDLE)
                        .define('a', Items.CHAIN)
                        .define('b', Trinkets.HANDLE_EXTENSION)
                        .define('c', Items.NETHERITE_INGOT)
                        .pattern("aba")
                        .pattern("bcb")
                        .pattern("aba")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Trinkets.DIG_FASTER)
                        .define('a', Items.CALCITE)
                        .define('b', Items.DIAMOND_PICKAXE)
                        .define('c', Items.STONE)
                        .define('d', Items.CRYING_OBSIDIAN)
                        .define('e', Items.MOSS_BLOCK)
                        .define('f', Items.SCULK_CATALYST)
                        .pattern("ada")
                        .pattern("ebd")
                        .pattern("cfa")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, FathommodModItems.FABRIC.get())
                        .requires(Items.LEATHER)
                        .requires(Items.LEATHER)
                        .requires(Items.LEATHER)
                        .requires(Items.STRING)
                        .requires(Items.STRING)
                        .requires(Items.STRING)
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Trinkets.DOUBLE_DOUBLE_JUMP)
                        .define('a', Items.MANGROVE_PROPAGULE)
                        .define('b', FathommodModItems.FROG_LEG.get())
                        .define('c', FathommodModItems.FROG_SOUL.get())
                        .pattern("a a")
                        .pattern("bcb")
                        .pattern("a a")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Trinkets.HANDLE_EXTENSION)
                        .define('a', Items.STICK)
                        .define('b', Items.DIAMOND_SWORD)
                        .pattern("aaa")
                        .pattern("aba")
                        .pattern("aaa")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Trinkets.CRACK)
                        .define('a', FathommodModItems.FABRIC.get())
                        .define('b', Items.SUGAR)
                        .define('c', Items.LEATHER_BOOTS)
                        .pattern("aaa")
                        .pattern("bcb")
                        .pattern("aaa")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Trinkets.CRACK_BUT_FOR_WATER)
                        .define('a', Items.PRISMARINE_SHARD)
                        .define('b', Items.HEART_OF_THE_SEA)
                        .define('d', Items.LEATHER_BOOTS)
                        .define('c', Items.NAUTILUS_SHELL)
                        .pattern("aba")
                        .pattern("cdc")
                        .pattern("aca")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Trinkets.LEG_RING)
                        .define('a', Items.SUGAR)
                        .define('b', Items.IRON_INGOT)
                        .pattern(" a ")
                        .pattern("aba")
                        .pattern(" a ")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Trinkets.LIGHT)
                        .define('a', Items.TORCH)
                        .define('b', Items.REDSTONE_TORCH)
                        .define('c', Items.SOUL_TORCH)
                        .define('d', TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_spawner_accepted_items")))
                        .pattern("aba")
                        .pattern("cdc")
                        .pattern("aba")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FathommodModItems.METAL_BAT.get())
                        .define('a', Items.IRON_BLOCK)
                        .define('b', Items.NETHERITE_INGOT)
                        .define('c', FathommodModItems.WOOD_BAT.get())
                        .pattern("a")
                        .pattern("b")
                        .pattern("c")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FathommodModItems.PALTN_SCYTHE.get())
                        .define('a', FathommodModItems.PALTN.get())
                        .define('b', Items.NETHERITE_SCRAP)
                        .define('c', Items.STICK)
                        .define('d', Items.IRON_INGOT)
                        .pattern("ab ")
                        .pattern("c d")
                        .pattern("c  ")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FathommodModItems.BASIC_SPEAR.get())
                        .define('a', Items.NETHERITE_SCRAP)
                        .define('b', Items.IRON_INGOT)
                        .define('c', Items.STICK)
                        .pattern(" ab")
                        .pattern(" ca")
                        .pattern("c  ")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FathommodModItems.TED_SPAWNER.get())
                        .define('a', Items.CARROT)
                        .define('b', TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_spawner_accepted_items")))
                        .pattern("aaa")
                        .pattern("aba")
                        .pattern("aaa")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ItemStack throwingKnives = FathommodModItems.THROWING_KNIVES.get().getDefaultInstance();
                throwingKnives.setCount(16);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, throwingKnives)
                        .define('a', Items.CARROT)
                        .define('b', TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "ted_spawner_accepted_items")))
                        .pattern("a")
                        .pattern("b")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, FathommodModItems.TNT_ARROW_ITEM.get())
                        .define('a', Items.ARROW)
                        .define('b', Items.TNT)
                        .pattern("aaa")
                        .pattern("aba")
                        .pattern("aaa")
                        .unlockedBy("dummy_condition", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.empty()))
                        .save(recipeOutput);
            }
        }

        private static class FMModelProvider extends ItemModelProvider {
            public FMModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
                super(output, modid, existingFileHelper);
            }

            static final Set<Item> TWO_DIMENSIONAL_ITEMS = Set.of(FathommodModItems.FABRIC.get(), FathommodModItems.PALTN.get(), FathommodModItems.WHY_THO.get(), FathommodModItems.TED_SPAWNER.get());

            @Override
            protected void registerModels() {
                for (ResourceLocation location : BuiltInRegistries.ITEM.keySet().stream().filter(location -> location.getNamespace().equals(FathommodMod.MOD_ID)).toList()) {
                    try {
                        if (TWO_DIMENSIONAL_ITEMS.contains(BuiltInRegistries.ITEM.get(location))) {
                            this.generatedModels.put(location, basicItem(location));
                            continue;
                        }
                        if (BuiltInRegistries.ITEM.get(location) instanceof BlockItem) {
                            ItemModelBuilder builder = new ItemModelBuilder(location, this.existingFileHelper);
                            builder.parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "block/" + location.getPath())));
                            generatedModels.put(location, builder);
                            continue;
                        }
                        if (BuiltInRegistries.ITEM.get(location) instanceof GeoItem) {
                            ItemModelBuilder builder = new ItemModelBuilder(location, this.existingFileHelper);
                            builder.parent(new ModelFile.ExistingModelFile(ResourceLocation.withDefaultNamespace("builtin/entity"), this.existingFileHelper));
                            generatedModels.put(location, builder);
                            AtomicReference<String> displaySettingsString = new AtomicReference<>("");
                            try {
                                this.existingFileHelper.getResource(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "models/displaysettings/" + location.getPath() + ".json"), PackType.CLIENT_RESOURCES).openAsReader().lines().forEach(line -> displaySettingsString.set(displaySettingsString.get() + line));
                            } catch (IOException e) {
                                FathommodMod.LOGGER.warn("Item {} doesn't have display settings!", location);
                            }
                            var transforms = builder.transforms();
                            try {
                                JsonObject object = JsonParser.parseString(displaySettingsString.get()).getAsJsonObject().getAsJsonObject("display");
                                for (String key : object.asMap().keySet()) {
                                    JsonObject displayTransforms = object.getAsJsonObject(key);

                                    if (displayTransforms.has("rotation")) {
                                        transforms.transform(
                                                Arrays.stream(ItemDisplayContext.values()).filter(context -> context.getSerializedName().equals(key)).findFirst().orElseThrow(() -> new RuntimeException("Unknown display context: " + key))
                                        ).rotation(
                                                displayTransforms.getAsJsonArray("rotation").get(0).getAsFloat(),
                                                displayTransforms.getAsJsonArray("rotation").get(1).getAsFloat(),
                                                displayTransforms.getAsJsonArray("rotation").get(2).getAsFloat()
                                        );
                                    }
                                    if (displayTransforms.has("translation")) {
                                        transforms.transform(
                                                Arrays.stream(ItemDisplayContext.values()).filter(context -> context.getSerializedName().equals(key)).findFirst().orElseThrow(() -> new RuntimeException("Unknown display context: " + key))
                                        ).translation(
                                                displayTransforms.getAsJsonArray("translation").get(0).getAsFloat(),
                                                displayTransforms.getAsJsonArray("translation").get(1).getAsFloat(),
                                                displayTransforms.getAsJsonArray("translation").get(2).getAsFloat()
                                        );
                                    }
                                    if (displayTransforms.has("scale")) {
                                        transforms.transform(
                                                Arrays.stream(ItemDisplayContext.values()).filter(context -> context.getSerializedName().equals(key)).findFirst().orElseThrow(() -> new RuntimeException("Unknown display context: " + key))
                                        ).scale(
                                                displayTransforms.getAsJsonArray("scale").get(0).getAsFloat(),
                                                displayTransforms.getAsJsonArray("scale").get(1).getAsFloat(),
                                                displayTransforms.getAsJsonArray("scale").get(2).getAsFloat()
                                        );
                                    }
                                }
                            } catch (JsonSyntaxException e) {
                                FathommodMod.LOGGER.fatal("Failed to parse display settings for {}", location);
                                FathommodMod.LOGGER.fatal(e);
                                throw e;
                            }
                            FathommodMod.LOGGER.info("Created model for geo item {}", location);
                            FathommodMod.LOGGER.info("Generated display settings for {}!", location);
                            continue;
                        }
                        ItemModelBuilder builder = new ItemModelBuilder(location, this.existingFileHelper);
                        builder.parent(new ModelFile.UncheckedModelFile("fathommod:custom/" + location.getPath()));
                        builder.texture("0", ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "item/" + location.getPath()))
                                .texture("particle", ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "item/" + location.getPath()));
                        var transforms = builder.transforms();
                        AtomicReference<String> displaySettingsString = new AtomicReference<>("");
                        boolean displaySettingsExist = true;
                        try {
                            this.existingFileHelper.getResource(ResourceLocation.fromNamespaceAndPath(FathommodMod.MOD_ID, "models/displaysettings/" + location.getPath() + ".json"), PackType.CLIENT_RESOURCES).openAsReader().lines().forEach(line -> displaySettingsString.set(displaySettingsString.get() + line));
                        } catch (IOException e) {
                            displaySettingsExist = false;
                            FathommodMod.LOGGER.warn("Item {} doesn't have display settings!", location);
                        }
                        if (displaySettingsExist) {
                            try {
                                JsonObject object = JsonParser.parseString(displaySettingsString.get()).getAsJsonObject().getAsJsonObject("display");
                                for (String key : object.asMap().keySet()) {
                                    JsonObject displayTransforms = object.getAsJsonObject(key);

                                    if (displayTransforms.has("rotation")) {
                                        transforms.transform(
                                                Arrays.stream(ItemDisplayContext.values()).filter(context -> context.getSerializedName().equals(key)).findFirst().orElseThrow(() -> new RuntimeException("Unknown display context: " + key))
                                        ).rotation(
                                                displayTransforms.getAsJsonArray("rotation").get(0).getAsFloat(),
                                                displayTransforms.getAsJsonArray("rotation").get(1).getAsFloat(),
                                                displayTransforms.getAsJsonArray("rotation").get(2).getAsFloat()
                                        );
                                    }
                                    if (displayTransforms.has("translation")) {
                                        transforms.transform(
                                                Arrays.stream(ItemDisplayContext.values()).filter(context -> context.getSerializedName().equals(key)).findFirst().orElseThrow(() -> new RuntimeException("Unknown display context: " + key))
                                        ).translation(
                                                displayTransforms.getAsJsonArray("translation").get(0).getAsFloat(),
                                                displayTransforms.getAsJsonArray("translation").get(1).getAsFloat(),
                                                displayTransforms.getAsJsonArray("translation").get(2).getAsFloat()
                                        );
                                    }
                                    if (displayTransforms.has("scale")) {
                                        transforms.transform(
                                                Arrays.stream(ItemDisplayContext.values()).filter(context -> context.getSerializedName().equals(key)).findFirst().orElseThrow(() -> new RuntimeException("Unknown display context: " + key))
                                        ).scale(
                                                displayTransforms.getAsJsonArray("scale").get(0).getAsFloat(),
                                                displayTransforms.getAsJsonArray("scale").get(1).getAsFloat(),
                                                displayTransforms.getAsJsonArray("scale").get(2).getAsFloat()
                                        );
                                    }
                                    builder = transforms.end();
                                }
                            } catch (JsonSyntaxException e) {
                                FathommodMod.LOGGER.fatal("Failed to parse display settings for {}", location);
                                FathommodMod.LOGGER.fatal(e);
                                throw e;
                            }
                            FathommodMod.LOGGER.info("Generated display settings for {}!", location);
                        }
                        this.generatedModels.put(location, builder);
                        FathommodMod.LOGGER.info("Put model for {}!", location);
                    } catch (Exception e) {
                        FathommodMod.LOGGER.fatal("Failed to generate model for {}", location);
                        throw e;
                    }
                }
            }
        }

        @SubscribeEvent
        private static void dataGen(GatherDataEvent event) {
            DataGenerator generator = event.getGenerator();
            PackOutput output = generator.getPackOutput();
            ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
            CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

            generator.addProvider(
                    true,
                    new FMModelProvider(output, FathommodMod.MOD_ID, existingFileHelper)
            );

            generator.addProvider(
                    true,
                    new FMRecipeProvider(output, lookupProvider)
            );
        }
    }
}
