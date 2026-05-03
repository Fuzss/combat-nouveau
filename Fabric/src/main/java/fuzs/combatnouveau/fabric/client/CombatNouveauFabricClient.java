package fuzs.combatnouveau.fabric.client;

import fuzs.combatnouveau.common.CombatNouveau;
import fuzs.combatnouveau.common.client.CombatNouveauClient;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class CombatNouveauFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveauClient::new);
    }
}
