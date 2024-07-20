package xyz.jpenilla.spawnchunkradius.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.jpenilla.spawnchunkradius.SpawnChunkRadius;

@Mixin(MinecraftServer.class)
abstract class MinecraftServerMixin {
    @Shadow @Final protected WorldData worldData;

    @Redirect(
        method = "loadLevel",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/progress/ChunkProgressListenerFactory;create(I)Lnet/minecraft/server/level/progress/ChunkProgressListener;")
    )
    ChunkProgressListener makeListener(ChunkProgressListenerFactory instance, int i) {
        return instance.create(this.worldData.overworldData().getGameRules().getInt(SpawnChunkRadius.SPAWN_CHUNK_RADIUS));
    }

    @Redirect(
        method = "prepareLevels",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V")
    )
    void redirectAddTicket(ServerChunkCache instance, TicketType<?> $$0, ChunkPos $$1, int $$2, Object $$3) {
        instance.level.setDefaultSpawnPos(instance.level.getSharedSpawnPos(), instance.level.getSharedSpawnAngle());
    }

    @ModifyConstant(
        method = "prepareLevels",
        constant = @Constant(intValue = 441)
    )
    int modifyChunkCount(int constant) {
        int radius = this.worldData.overworldData().getGameRules().getInt(SpawnChunkRadius.SPAWN_CHUNK_RADIUS);
        return radius > 0 ? Mth.square(spawnChunkRadius$calculateDiameter(radius)) : 0;
    }

    @Unique
    private static int spawnChunkRadius$calculateDiameter(int radius) {
        return 2 * radius + 1;
    }
}
