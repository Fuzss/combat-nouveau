package fuzs.combatnouveau.common.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.combatnouveau.common.CombatNouveau;
import fuzs.combatnouveau.common.config.ClientConfig;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class RenderOffhandItemHandler {

    public static EventResult onSubmitArmWithItem(FirstPersonHandsAndItemsRenderer firstPersonHandsAndItemsRenderer, PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords) {
        if (hand != InteractionHand.OFF_HAND) {
            return EventResult.PASS;
        }

        if (!itemStack.isEmpty()
                && CombatNouveau.CONFIG.get(ClientConfig.class).hiddenOffhandItems.contains(itemStack.getItem())) {
            AvatarRenderState avatarRenderState = playerState.avatarRenderState;
            if (avatarRenderState == null
                    || !avatarRenderState.isUsingItem
                    || avatarRenderState.useItemHand != InteractionHand.OFF_HAND
                    || itemStack.getItem() instanceof ShieldItem
                    && CombatNouveau.CONFIG.get(ClientConfig.class).shieldIndicator) {
                return EventResult.INTERRUPT;
            }
        }

        return EventResult.PASS;
    }
}
