package fuzs.combatnouveau.mixin;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DiggerItem.class)
abstract class DiggerItemMixin extends TieredItem {

    public DiggerItemMixin(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Inject(method = "postHurtEnemy", at = @At("HEAD"), cancellable = true)
    public void postHurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker, CallbackInfo callback) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).noAxeAttackPenalty) return;
        ItemHelper.hurtAndBreak(itemStack, 1, attacker, EquipmentSlot.MAINHAND);
        callback.cancel();
    }
}
