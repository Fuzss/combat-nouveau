package fuzs.combatnouveau.common.network.client;

import fuzs.combatnouveau.common.util.SweepAttackHelper;
import fuzs.puzzleslib.common.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.common.api.network.v4.message.play.ServerboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

/**
 * Mimic the same packet which is used in Combat Test snapshots.
 *
 * @see net.minecraft.network.protocol.game.ServerboundInteractPacket
 */
public record ServerboundSweepAttackMessage(boolean isUsingSecondaryAction) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundSweepAttackMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ServerboundSweepAttackMessage::isUsingSecondaryAction,
            ServerboundSweepAttackMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                ServerPlayer player = context.player();
                player.setShiftKeyDown(ServerboundSweepAttackMessage.this.isUsingSecondaryAction);
                if (player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                    SweepAttackHelper.doSweepAttack(player);
                }
            }
        };
    }
}
