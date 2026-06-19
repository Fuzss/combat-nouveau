package fuzs.combatnouveau.neoforge;

import fuzs.combatnouveau.common.CombatNouveau;
import fuzs.combatnouveau.common.init.ModRegistry;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.common.Mod;

@Mod(CombatNouveau.MOD_ID)
public class CombatNouveauNeoForge {

    public CombatNouveauNeoForge() {
        ModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveau::new);
        DataProviderHelper.registerDataProviders(CombatNouveau.WEAK_SWEEPING_EDGE_ID,
                PackType.SERVER_DATA,
                ModRegistry.REGISTRY_SET_BUILDER);
    }
}
