package fuzs.combatnouveau.common.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.combatnouveau.common.CombatNouveau;
import fuzs.combatnouveau.common.config.ClientConfig;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class RenderOffhandItemHandler {

    public static EventResult onRenderOffHand(ItemInHandRenderer itemInHandRenderer, InteractionHand interactionHand, AbstractClientPlayer player, HumanoidArm humanoidArm, ItemStack itemStack, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int combinedLight, float partialTick, float interpolatedPitch, float swingProgress, float equipProgress) {
        if (!itemStack.isEmpty()
                && CombatNouveau.CONFIG.get(ClientConfig.class).hiddenOffhandItems.contains(itemStack.getItem())) {
            if (!player.isUsingItem() || player.getUsedItemHand() != InteractionHand.OFF_HAND
                    || itemStack.getItem() instanceof ShieldItem
                    && CombatNouveau.CONFIG.get(ClientConfig.class).shieldIndicator) {
                return EventResult.INTERRUPT;
            }
        }

        return EventResult.PASS;
    }
}
