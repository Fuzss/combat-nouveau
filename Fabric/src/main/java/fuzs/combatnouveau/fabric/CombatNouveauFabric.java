package fuzs.combatnouveau.fabric;

import fuzs.combatnouveau.common.CombatNouveau;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class CombatNouveauFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveau::new);
    }
}
