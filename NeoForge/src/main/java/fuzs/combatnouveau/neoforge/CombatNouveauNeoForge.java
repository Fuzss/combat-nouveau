package fuzs.combatnouveau.neoforge;

import fuzs.combatnouveau.common.CombatNouveau;
import fuzs.combatnouveau.common.init.ModRegistry;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.common.Mod;

@Mod(CombatNouveau.MOD_ID)
public class CombatNouveauNeoForge {

    public CombatNouveauNeoForge() {
        ModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveau::new);
        DataProviderBuilder.ofBuiltIn(CombatNouveau.WEAK_SWEEPING_EDGE_ID,
                PackType.SERVER_DATA)
                .addWorldBootstrap(Registries.ENCHANTMENT, ModRegistry::bootstrapEnchantments);
    }
}
