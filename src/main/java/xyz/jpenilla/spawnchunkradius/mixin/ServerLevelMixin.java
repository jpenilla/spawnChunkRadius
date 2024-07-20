package xyz.jpenilla.spawnchunkradius.mixin;

import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.jpenilla.spawnchunkradius.SpawnChunkRadius;

@Mixin(ServerLevel.class)
abstract class ServerLevelMixin extends Level {
    @Unique
    private int spawnChunkRadius$lastSpawnChunkRadius;

    protected ServerLevelMixin(WritableLevelData arg, ResourceKey<Level> arg2, RegistryAccess arg3, Holder<DimensionType> arg4, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(arg, arg2, arg3, arg4, supplier, bl, bl2, l, i);
    }

    @Redirect(
        method = "setDefaultSpawnPos",
        at = @At(
            target = "Lnet/minecraft/server/level/ServerChunkCache;removeRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V",
            value = "INVOKE"
        )
    )
    void injectRemoveTicket(ServerChunkCache instance, TicketType<?> type, ChunkPos pos, int radius, Object value) {
        if (this.spawnChunkRadius$lastSpawnChunkRadius > 1) {
            instance.removeRegionTicket(TicketType.START, pos, this.spawnChunkRadius$lastSpawnChunkRadius, Unit.INSTANCE);
        }

        int i = this.getGameRules().getInt(SpawnChunkRadius.SPAWN_CHUNK_RADIUS) + 1;
        if (i > 1) {
            instance.addRegionTicket(TicketType.START, pos, i, Unit.INSTANCE);
        }

        this.spawnChunkRadius$lastSpawnChunkRadius = i;
    }

    @Redirect(
        method = "setDefaultSpawnPos",
        at = @At(
            target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V",
            value = "INVOKE"
        )
    )
    void injectAddTicket(ServerChunkCache instance, TicketType<?> type, ChunkPos pos, int radius, Object value) {
        // no-op, handled above
    }
}
