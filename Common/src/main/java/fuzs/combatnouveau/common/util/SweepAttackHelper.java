package fuzs.combatnouveau.common.util;

import fuzs.combatnouveau.common.services.CommonAbstractions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;

public class SweepAttackHelper {

    /**
     * @see Player#attack(Entity)
     */
    public static void doSweepAttack(Player player) {
        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        ItemStack attackingItemStack = player.getWeaponItem();
        DamageSource damageSource = player.createAttackSource(attackingItemStack);
        float attackStrengthScale = player.getAttackStrengthScale(0.5F);
        baseDamage *= player.baseDamageScaleFactor();
        if (baseDamage > 0.0F) {
            boolean fullStrengthAttack = attackStrengthScale > 0.9F;
            boolean knockbackAttack = player.isSprinting() && fullStrengthAttack;
            boolean criticalAttack = fullStrengthAttack && player.canCriticalAttack(player);
            if (player.isSweepAttack(fullStrengthAttack, criticalAttack, knockbackAttack)) {
                AABB aabb = getSweepAttackAABB(player);
                doSweepAttack(player, baseDamage, damageSource, attackStrengthScale, aabb);
                // This also resets the attack ticker.
                player.swing(InteractionHand.MAIN_HAND);
                player.causeFoodExhaustion(0.1F);
            }
        }
    }

    private static AABB getSweepAttackAABB(Player player) {
        double moveX = -Mth.sin(player.getYRot() * Mth.DEG_TO_RAD) * 2.0;
        double moveZ = Mth.cos(player.getYRot() * Mth.DEG_TO_RAD) * 2.0;
        return CommonAbstractions.INSTANCE.getSweepHitBox(player, player).move(moveX, 0.0, moveZ);
    }

    /**
     * @see Player#doSweepAttack(Entity, float, DamageSource, float)
     */
    private static void doSweepAttack(Player player, float baseDamage, DamageSource damageSource, float attackStrengthScale, AABB aabb) {
        player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP);
        if (player.level() instanceof ServerLevel serverLevel) {
            float sweepingDamage =
                    1.0F + (float) player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * baseDamage;
            for (LivingEntity nearby : serverLevel.getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (nearby != player && !player.isAlliedTo(nearby)) {
                    if (nearby instanceof ArmorStand armorStand) {
                        if (armorStand.isMarker()) {
                            continue;
                        }
                    }

                    if (player.distanceToSqr(nearby) < 9.0) {
                        float enchantedDamage =
                                player.getEnchantedDamage(nearby, sweepingDamage, damageSource) * attackStrengthScale;
                        if (nearby.hurtServer(serverLevel, damageSource, enchantedDamage)) {
                            nearby.knockback(0.4F,
                                    Mth.sin(player.getYRot() * Mth.DEG_TO_RAD),
                                    -Mth.cos(player.getYRot() * Mth.DEG_TO_RAD),
                                    damageSource,
                                    enchantedDamage);
                            EnchantmentHelper.doPostAttackEffects(serverLevel, nearby, damageSource);
                        }
                    }
                }
            }

            double dx = -Mth.sin(player.getYRot() * Mth.DEG_TO_RAD);
            double dz = Mth.cos(player.getYRot() * Mth.DEG_TO_RAD);
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    player.getX() + dx,
                    player.getY(0.5F),
                    player.getZ() + dz,
                    0,
                    dx,
                    0.0F,
                    dz,
                    0.0F);
        }
    }
}
