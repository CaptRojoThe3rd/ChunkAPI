/*
 * ChunkAPI
 *
 * Copyright (C) 2023-2025 FalsePattern, The MEGA Team, LegacyModdingMC contributors
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.chunk.internal.mixin.mixins.common.base;

import com.falsepattern.chunk.internal.Common;
import com.falsepattern.chunk.internal.impl.CustomPacketMultiBlockChange;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;

/**
 * The `@Overwrite`s in this mixin are used to replace `short[] locationOfBlockChange` with
 * `int[] chunkapi$locationOfBlockChange`.
 * <br><br>
 * In vanilla, `locationOfBlockChange` is an array of shorts that contains positions of blocks
 * to be updated. Each position is formatted as `XXXXZZZZ YYYYYYYY`. This restricts the build height
 * limit to 256 blocks.
 * <br><br>
 * The overwritten methods instead refer to `chunkapi$locationOfBlockChange`, which is an array of
 * ints. Each position is formatted as `00000000 XXXXZZZZ YYYYYYYY YYYYYYYY`. Giving Y 16 bits is
 * pretty overkill, but I think it looks cleaner.
 */

@Mixin(PlayerManager.PlayerInstance.class)
public abstract class PlayerInstanceMixin {
    /** PlayerManager.this */
    @Shadow
    @Final
    PlayerManager this$0;

    @Shadow
    private int numberOfTilesToUpdate;
    @Shadow
    private int flagsYAreasToUpdate;
    @Shadow
    @Final
    private ChunkCoordIntPair chunkLocation;

    @Unique
    private int[] chunkapi$locationOfBlockChange = new int[64];


    @Shadow
    public abstract void sendToAllPlayersWatchingChunk(Packet thePacket);

    @Shadow
    public abstract void sendTileToAllPlayersWatchingChunk(TileEntity theTileEntity);

    /**
     * @author CaptRojoThe3rd
     * @reason Extending build height limit
     */
    @SuppressWarnings("unchecked")
    @Overwrite
    public void flagChunkForUpdate(int x, int y, int z) {
        if (this.numberOfTilesToUpdate == 0) {
            this.this$0.chunkWatcherWithPlayers.add(this);
        }

        this.flagsYAreasToUpdate |= 1 << (y >> 4);

        {
            int i1 = (x << 20 | z << 16 | y);

            for (int l = 0; l < this.numberOfTilesToUpdate; ++l) {
                if (this.chunkapi$locationOfBlockChange[l] == i1) {
                    return;
                }
            }

            if (numberOfTilesToUpdate == chunkapi$locationOfBlockChange.length) {
                chunkapi$locationOfBlockChange = java.util.Arrays.copyOf(chunkapi$locationOfBlockChange, chunkapi$locationOfBlockChange.length << 1);
            }
            this.chunkapi$locationOfBlockChange[this.numberOfTilesToUpdate++] = i1;
        }
    }

    // @formatter:off

    /**
     * @author CaptRojoThe3rd
     * @reason Extending build height limit
     */
    @Overwrite
    public void sendChunkUpdate() {
        if (this.numberOfTilesToUpdate != 0) {
            int x;
            int y;
            int z;

            if (this.numberOfTilesToUpdate == 1) {
                x = this.chunkLocation.chunkXPos * 16 + (this.chunkapi$locationOfBlockChange[0] >> 20 & 15);
                y = this.chunkapi$locationOfBlockChange[0] & Common.CHUNK_HEIGHT_MASK;
                z = this.chunkLocation.chunkZPos * 16 + (this.chunkapi$locationOfBlockChange[0] >> 16 & 15);
                this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(x, y, z, this.this$0.theWorldServer));

                if (this.this$0.theWorldServer.getBlock(x, y, z).hasTileEntity(this.this$0.theWorldServer.getBlockMetadata(x, y, z))) {
                    this.sendTileToAllPlayersWatchingChunk(this.this$0.theWorldServer.getTileEntity(x, y, z));
                }
            } else {
                int l;

                if (this.numberOfTilesToUpdate >= net.minecraftforge.common.ForgeModContainer.clumpingThreshold) {
                    S21PacketChunkData packet = new S21PacketChunkData(
                            this.this$0.theWorldServer.getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos),
                            false,
                            this.flagsYAreasToUpdate
                    );
                    this.sendToAllPlayersWatchingChunk(packet);
                } else {
                    S22PacketMultiBlockChange packet = new S22PacketMultiBlockChange();
                    ((CustomPacketMultiBlockChange) (Object) packet).chunkapi$init(
                        this.numberOfTilesToUpdate,
                        this.chunkapi$locationOfBlockChange,
                        this.this$0.theWorldServer.getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos)
                    );
                    this.sendToAllPlayersWatchingChunk(packet);
                }

                {
                    WorldServer world = this.this$0.theWorldServer;
                    for (l = 0; l < this.numberOfTilesToUpdate; ++l) {
                        x = this.chunkLocation.chunkXPos * 16 + (this.chunkapi$locationOfBlockChange[l] >> 20 & 15);
                        y = this.chunkapi$locationOfBlockChange[l] & Common.CHUNK_HEIGHT_MASK;
                        z = this.chunkLocation.chunkZPos * 16 + (this.chunkapi$locationOfBlockChange[l] >> 16 & 15);

                        if (world.getBlock(x, y, z).hasTileEntity(world.getBlockMetadata(x, y, z))) {
                            this.sendTileToAllPlayersWatchingChunk(this.this$0.theWorldServer.getTileEntity(x, y, z));
                        }
                    }
                }
            }

            this.numberOfTilesToUpdate = 0;
            this.flagsYAreasToUpdate = 0;
        }
    }
    // @formatter:on
}
