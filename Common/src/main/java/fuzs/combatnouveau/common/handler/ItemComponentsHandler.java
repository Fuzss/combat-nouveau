package fuzs.combatnouveau.common.handler;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import fuzs.combatnouveau.common.CombatNouveau;
import fuzs.combatnouveau.common.config.CommonConfig;
import fuzs.puzzleslib.common.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.common.api.core.v1.context.ItemComponentsContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.equipment.ArmorType;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class ItemComponentsHandler {
    public static final Identifier BASE_ENTITY_INTERACTION_RANGE_ID = Identifier.withDefaultNamespace(
            "base_entity_interaction_range");
    public static final Set<Identifier> BASE_ATTRIBUTE_MODIFIER_IDS = Stream.concat(Stream.of(
                    BASE_ENTITY_INTERACTION_RANGE_ID),
            Arrays.stream(ArmorType.values())
                    .map(ArmorType::getName)
                    .map((String nameValue) -> "armor." + nameValue)
                    .map(Identifier::withDefaultNamespace)).collect(ImmutableSet.toImmutableSet());

    public static void onRegisterItemComponentPatches(ItemComponentsContext context) {
        if (!CombatNouveau.CONFIG.getHolder(CommonConfig.class).isAvailable()) {
            return;
        }

        if (CombatNouveau.CONFIG.get(CommonConfig.class).increaseStackSize) {
            context.registerItemComponentsPatch(ItemComponentsHandler::isThrowableItem,
                    (DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider registries, Item item) -> {
                        builder.set(DataComponents.MAX_STACK_SIZE, 64);
                    });
            context.registerItemComponentsPatch(ItemComponentsHandler::isPotion,
                    (DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider registries, Item item) -> {
                        builder.set(DataComponents.MAX_STACK_SIZE, 16);
                        if (isThrowablePotion(item)) {
                            builder.set(DataComponents.USE_COOLDOWN,
                                    new UseCooldown(0.35F, Optional.of(CombatNouveau.id("potions"))));
                        }
                    });
        }

        if (CombatNouveau.CONFIG.get(CommonConfig.class).throwablesDelay) {
            context.registerItemComponentsPatch(ItemComponentsHandler::isThrowableItem,
                    (DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider registries, Item item) -> {
                        builder.set(DataComponents.USE_COOLDOWN,
                                new UseCooldown(0.2F, Optional.of(CombatNouveau.id("throwables"))));
                    });
        }

        if (CombatNouveau.CONFIG.get(CommonConfig.class).noItemDurabilityPenalty) {
            context.registerItemComponentsPatch(Predicates.alwaysTrue(),
                    (DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider registries, Item item) -> {
                        Weapon weapon = modifyWeaponComponent(components.get(DataComponents.WEAPON));
                        if (weapon != null) {
                            builder.set(DataComponents.WEAPON, weapon);
                        } else {
                            Tool tool = modifyToolComponent(components.get(DataComponents.TOOL));
                            if (tool != null) {
                                builder.set(DataComponents.TOOL, tool);
                            }
                        }
                    });
        }

        if (CombatNouveau.CONFIG.get(CommonConfig.class).fastDrinking) {
            context.registerItemComponentsPatch(Predicates.alwaysTrue(),
                    (DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider registries, Item item) -> {
                        Consumable consumable = modifyConsumableComponent(components.get(DataComponents.CONSUMABLE));
                        if (consumable != null) {
                            builder.set(DataComponents.CONSUMABLE, consumable);
                        }
                    });
        }

        context.registerItemComponentsPatch(Items.SHIELD,
                (DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider registries, Item item) -> {
                    BlocksAttacks blocksAttacks = modifyBlocksAttacksComponent(components.get(DataComponents.BLOCKS_ATTACKS));
                    if (blocksAttacks != null) {
                        builder.set(DataComponents.BLOCKS_ATTACKS, blocksAttacks);
                    }
                });
        context.registerItemComponentsPatch(Predicates.alwaysTrue(),
                (DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider registries, Item item) -> {
                    ItemAttributeModifiers itemAttributeModifiers = modifyItemAttributeModifiersComponent(item,
                            components);
                    if (itemAttributeModifiers != null) {
                        builder.set(DataComponents.ATTRIBUTE_MODIFIERS, itemAttributeModifiers);
                    }
                });
    }

    private static boolean isThrowableItem(Item item) {
        return item == Items.SNOWBALL || isEgg(item);
    }

    private static boolean isEgg(Item item) {
        return item == Items.EGG || item == Items.BROWN_EGG || item == Items.BLUE_EGG;
    }

    private static boolean isPotion(Item item) {
        return item == Items.POTION || isThrowablePotion(item);
    }

    private static boolean isThrowablePotion(Item item) {
        return item == Items.SPLASH_POTION || item == Items.LINGERING_POTION;
    }

    private static @Nullable Weapon modifyWeaponComponent(Weapon weapon) {
        if (weapon != null && weapon.itemDamagePerAttack() == 2) {
            return new Weapon(1, weapon.disableBlockingForSeconds());
        } else {
            return null;
        }
    }

    private static @Nullable Tool modifyToolComponent(Tool tool) {
        if (tool != null && tool.damagePerBlock() == 2) {
            return new Tool(tool.rules(), tool.defaultMiningSpeed(), 1, tool.canDestroyBlocksInCreative());
        } else {
            return null;
        }
    }

    private static @Nullable Consumable modifyConsumableComponent(@Nullable Consumable consumable) {
        if (consumable != null && consumable.animation() == ItemUseAnimation.DRINK) {
            return new Consumable(1.0F,
                    consumable.animation(),
                    consumable.sound(),
                    consumable.hasConsumeParticles(),
                    consumable.onConsumeEffects());
        } else {
            return null;
        }
    }

    private static @Nullable BlocksAttacks modifyBlocksAttacksComponent(@Nullable BlocksAttacks blocksAttacks) {
        if (blocksAttacks != null) {
            return new BlocksAttacks(CombatNouveau.CONFIG.get(CommonConfig.class).removeShieldDelay ? 0.0F :
                    blocksAttacks.blockDelaySeconds(),
                    blocksAttacks.disableCooldownScale(),
                    ImmutableList.copyOf(Lists.transform(blocksAttacks.damageReductions(),
                            (BlocksAttacks.DamageReduction damageReduction) -> {
                                return new BlocksAttacks.DamageReduction((float) CombatNouveau.CONFIG.get(CommonConfig.class).horizontalBlockingAngle,
                                        damageReduction.type(),
                                        damageReduction.base(),
                                        damageReduction.factor());
                            })),
                    blocksAttacks.itemDamage(),
                    blocksAttacks.bypassedBy(),
                    blocksAttacks.blockSound(),
                    blocksAttacks.disableSound());
        } else {
            return null;
        }
    }

    private static @Nullable ItemAttributeModifiers modifyItemAttributeModifiersComponent(Item item, DataComponentGetter components) {
        List<ItemAttributeModifiers.Entry> itemAttributeModifiers = components.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.EMPTY).modifiers();
        itemAttributeModifiers = setAttributeValue(item,
                itemAttributeModifiers,
                Attributes.ATTACK_DAMAGE,
                Item.BASE_ATTACK_DAMAGE_ID,
                CombatNouveau.CONFIG.get(CommonConfig.class).attackDamageOverrides);
        itemAttributeModifiers = setAttributeValue(item,
                itemAttributeModifiers,
                Attributes.ATTACK_SPEED,
                Item.BASE_ATTACK_SPEED_ID,
                CombatNouveau.CONFIG.get(CommonConfig.class).attackSpeedOverrides);
        List<ItemAttributeModifiers.Entry> itemAttributeModifiers2 = setAttributeValue(item,
                itemAttributeModifiers,
                Attributes.ENTITY_INTERACTION_RANGE,
                BASE_ENTITY_INTERACTION_RANGE_ID,
                CombatNouveau.CONFIG.get(CommonConfig.class).entityInteractionRangeOverrides);
        if (itemAttributeModifiers == itemAttributeModifiers2) {
            if (CombatNouveau.CONFIG.get(CommonConfig.class).additionalEntityInteractionRange) {
                OptionalDouble attackRangeBonus = getAttackRangeBonus(item, components);
                if (attackRangeBonus.isPresent()) {
                    itemAttributeModifiers = setAttributeValue(itemAttributeModifiers,
                            Attributes.ENTITY_INTERACTION_RANGE,
                            BASE_ENTITY_INTERACTION_RANGE_ID,
                            attackRangeBonus.getAsDouble());
                }
            }
        } else {
            itemAttributeModifiers = itemAttributeModifiers2;
        }

        if (components.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()
                != itemAttributeModifiers) {
            return new ItemAttributeModifiers(ImmutableList.copyOf(itemAttributeModifiers));
        } else {
            return null;
        }
    }

    private static List<ItemAttributeModifiers.Entry> setAttributeValue(Item item, List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, Identifier id, ConfigDataSet<Item> attackDamageOverrides) {
        if (attackDamageOverrides.contains(item)) {
            double newValue = attackDamageOverrides.<Double>getOptional(item, 0).orElseThrow();
            return setAttributeValue(itemAttributeModifiers, attribute, id, newValue);
        } else {
            return itemAttributeModifiers;
        }
    }

    private static List<ItemAttributeModifiers.Entry> setAttributeValue(List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, Identifier id, double newValue) {
        itemAttributeModifiers = new ArrayList<>(itemAttributeModifiers);
        AttributeModifier attributeModifier = new AttributeModifier(id,
                newValue,
                AttributeModifier.Operation.ADD_VALUE);
        ItemAttributeModifiers.Entry newEntry = new ItemAttributeModifiers.Entry(attribute,
                attributeModifier,
                EquipmentSlotGroup.MAINHAND);
        ListIterator<ItemAttributeModifiers.Entry> iterator = itemAttributeModifiers.listIterator();
        while (iterator.hasNext()) {
            ItemAttributeModifiers.Entry entry = iterator.next();
            if (entry.slot() == EquipmentSlotGroup.MAINHAND && entry.matches(attribute, id)) {
                iterator.set(newEntry);
                return itemAttributeModifiers;
            }
        }
        itemAttributeModifiers.add(newEntry);
        return itemAttributeModifiers;
    }

    private static OptionalDouble getAttackRangeBonus(Item item, DataComponentGetter dataComponents) {
        if (item == Items.TRIDENT || ToolComponentsHelper.hasComponentsForBlocks(dataComponents,
                BlockTags.MINEABLE_WITH_HOE)) {
            return OptionalDouble.of(1.0);
        } else if (ToolComponentsHelper.isWeapon(dataComponents)) {
            return OptionalDouble.of(0.5);
        } else if (ToolComponentsHelper.isTool(dataComponents)) {
            return OptionalDouble.of(0.0);
        } else {
            return OptionalDouble.empty();
        }
    }
}
