package fuzs.combatnouveau.common.client;

import fuzs.combatnouveau.common.CombatNouveau;
import fuzs.combatnouveau.common.client.handler.AutoAttackHandler;
import fuzs.combatnouveau.common.client.handler.RenderOffhandItemHandler;
import fuzs.combatnouveau.common.client.handler.ShieldIndicatorHandler;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.common.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.common.api.client.event.v1.entity.player.InteractionInputEvents;
import fuzs.puzzleslib.common.api.client.event.v1.gui.RenderGuiEvents;
import fuzs.puzzleslib.common.api.client.event.v1.renderer.RenderHandEvents;

public class CombatNouveauClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        RenderGuiEvents.BEFORE.register(ShieldIndicatorHandler::onBeforeRenderGui);
        RenderGuiEvents.AFTER.register(ShieldIndicatorHandler::onAfterRenderGui);
        InteractionInputEvents.ATTACK.register(AutoAttackHandler::onAttackInteraction);
        ClientTickEvents.START.register(AutoAttackHandler::onStartTick);
        RenderHandEvents.OFF_HAND.register(RenderOffhandItemHandler::onRenderOffHand);
    }

    @Override
    public void onRegisterGuiLayers(GuiLayersContext context) {
        context.registerGuiLayer(GuiLayersContext.CROSSHAIR,
                CombatNouveau.id("crosshair_blocking_indicator"),
                ShieldIndicatorHandler::renderCrosshairBlockingIndicator);
        context.registerGuiLayer(GuiLayersContext.HOTBAR,
                CombatNouveau.id("hotbar_blocking_indicator"),
                ShieldIndicatorHandler::renderHotbarBlockingIndicator);
    }
}
