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

package com.falsepattern.chunk.internal.mixin.mixins.client.vanilla;

import com.falsepattern.chunk.internal.Common;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.ChunkCache;

@Mixin(ChunkCache.class)
public abstract class ChunkCacheMixin {
    @ModifyConstant(method = "getSkyBlockTypeBrightness",
                    constant = @Constant(intValue = 256),
                    require = 1)
    private static int modifyChunkHeight_getSkyBlockTypeBrightness(int constant) {
        return Common.CHUNK_HEIGHT;
    }

    @ModifyConstant(method = "getSkyBlockTypeBrightness",
                    constant = @Constant(intValue = 255),
                    require = 1)
    private static int modifyChunkHeightM1_getSkyBlockTypeBrightness(int constant) {
        return Common.CHUNK_HEIGHT - 1;
    }

    @ModifyConstant(method = "getSpecialBlockBrightness",
                    constant = @Constant(intValue = 256),
                    require = 1)
    private static int modifyChunkHeight_getSpecialBlockBrightness(int constant) {
        return Common.CHUNK_HEIGHT;
    }

    @ModifyConstant(method = "getSpecialBlockBrightness",
                    constant = @Constant(intValue = 255),
                    require = 1)
    private static int modifyChunkHeightM1_getSpecialBlockBrightness(int constant) {
        return Common.CHUNK_HEIGHT - 1;
    }

    @ModifyConstant(method = "getHeight",
                    constant = @Constant(intValue = 256),
                    require = 1)
    private static int modifyChunkHeight_getHeight(int constant) {
        return Common.CHUNK_HEIGHT;
    }
}
