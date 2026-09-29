package fuzs.combatnouveau.common.handler;

import fuzs.combatnouveau.common.CombatNouveau;
import fuzs.combatnouveau.common.config.CommonConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;

public final class ModifyEnchantmentsHandler {

    private ModifyEnchantmentsHandler() {
        // NO-OP
    }

    public static boolean modifyEnchantment(ResourceKey<Enchantment> key, Enchantment.Builder builder, RegistryOps.RegistryInfoLookup lookup) {
        if (key == Enchantments.SWEEPING_EDGE) {
            return modifySweepingEdge(builder);
        } else {
            return false;
        }
    }

    private static boolean modifySweepingEdge(Enchantment.Builder builder) {
        if (!CombatNouveau.CONFIG.get(CommonConfig.class).halfSweepingDamage) {
            return false;
        }

        // Halve the sweeping damage to 0.5 per level instead of the vanilla 1.0 per level.
        builder.getEffectsList(EnchantmentEffectComponents.ATTRIBUTES).clear();
        builder.withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                new EnchantmentAttributeEffect(Identifier.withDefaultNamespace("enchantment.sweeping_edge"),
                        Attributes.SWEEPING_DAMAGE_RATIO,
                        new LevelBasedValue.Fraction(LevelBasedValue.perLevel(0.5F),
                                LevelBasedValue.perLevel(2.0F, 1.0F)),
                        AttributeModifier.Operation.ADD_VALUE));
        return true;
    }
}
