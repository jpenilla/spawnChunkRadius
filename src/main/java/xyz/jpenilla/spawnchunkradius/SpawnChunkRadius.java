package xyz.jpenilla.spawnchunkradius;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.util.function.BiConsumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;

@Mod("spawn_chunk_radius")
public final class SpawnChunkRadius {
    public static final GameRules.Key<GameRules.IntegerValue> SPAWN_CHUNK_RADIUS = GameRules.register("spawnChunkRadius", GameRules.Category.MISC, create(2, 0, 32, (minecraftServer, integerValue) -> {
        ServerLevel serverLevel = minecraftServer.overworld();
        serverLevel.setDefaultSpawnPos(serverLevel.getSharedSpawnPos(), serverLevel.getSharedSpawnAngle());
    }));

    private static GameRules.Type<GameRules.IntegerValue> create(
        int defaultValue, int min, int max, BiConsumer<MinecraftServer, GameRules.IntegerValue> changeListener
    ) {
        return new GameRules.Type<>(
            () -> IntegerArgumentType.integer(min, max),
            type -> new GameRules.IntegerValue(type, defaultValue),
            changeListener,
            GameRules.GameRuleTypeVisitor::visitInteger
        );
    }
}
