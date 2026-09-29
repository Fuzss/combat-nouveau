package fuzs.combatnouveau.neoforge.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fuzs.combatnouveau.common.handler.ItemComponentsHandler;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttributeUtil.class)
abstract class AttributeUtilNeoForgeMixin {

    @ModifyExpressionValue(method = "applyTextFor",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/world/entity/ai/attributes/Attribute;getBaseId()Lnet/minecraft/resources/Identifier;"))
    private static Identifier applyTextFor(Identifier baseId, @Local AttributeModifier modifier) {
        // NeoForge replaces the vanilla attribute tooltip handling, so the display logic is replicated here
        // by pretending the affected modifiers are base modifiers (which get the entity base value added).
        return ItemComponentsHandler.isSpecialBaseAttributeModifier(modifier, modifier.amount()) ? modifier.id() : baseId;
    }
}
