package fuzs.combatnouveau.neoforge;

import fuzs.combatnouveau.common.CombatNouveau;
import fuzs.combatnouveau.common.handler.ModifyEnchantmentsHandler;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.fml.common.Mod;

import java.util.List;

@Mod(CombatNouveau.MOD_ID)
public class CombatNouveauNeoForge {

    public CombatNouveauNeoForge() {
        ModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveau::new);
    }

    @SuppressWarnings("unchecked")
    public static Enchantment modifyEnchantment(ResourceKey<Enchantment> key, Enchantment enchantment, RegistryOps.RegistryInfoLookup lookup) {
        Enchantment.Builder builder = Enchantment.enchantment(enchantment.definition());
        builder.exclusiveWith(enchantment.exclusiveSet());
        // copy the original effects so the builder starts out as a copy of the enchantment being modified
        builder.effectMapBuilder.addAll(enchantment.effects());
        enchantment.effects().forEach((TypedDataComponent<?> component) -> {
            if (component.value() instanceof List<?> valueList) {
                builder.getEffectsList((DataComponentType<List<Object>>) component.type()).addAll(valueList);
            }
        });
        if (ModifyEnchantmentsHandler.modifyEnchantment(key, builder, lookup)) {
            // keep the original description instead of deriving it from the resource key
            return new Enchantment(enchantment.description(),
                    enchantment.definition(),
                    enchantment.exclusiveSet(),
                    builder.effectMapBuilder.build());
        } else {
            return null;
        }
    }
}
