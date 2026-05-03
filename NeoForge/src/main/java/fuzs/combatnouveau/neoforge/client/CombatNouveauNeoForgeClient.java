package fuzs.combatnouveau.neoforge.client;

import fuzs.combatnouveau.common.CombatNouveau;
import fuzs.combatnouveau.common.client.CombatNouveauClient;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = CombatNouveau.MOD_ID, dist = Dist.CLIENT)
public class CombatNouveauNeoForgeClient {

    public CombatNouveauNeoForgeClient() {
        ClientModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveauClient::new);
    }
}
