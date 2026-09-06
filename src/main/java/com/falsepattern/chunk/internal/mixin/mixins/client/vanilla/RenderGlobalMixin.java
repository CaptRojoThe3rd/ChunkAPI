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

import net.minecraft.client.renderer.RenderGlobal;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {
    @ModifyConstant(method = "<init>(Lnet/minecraft/client/Minecraft;)V",
                    constant = @Constant(intValue = 16),
                    require = 1)
    private static int modifySubchunkCount_init(int constant) {
        return Common.SUBCHUNK_COUNT;
    }

    @ModifyConstant(method = "loadRenderers",
                    constant = @Constant(intValue = 16),
                    require = 1)
    private static int modifySubchunkCount_loadRenderers(int constant) {
        return Common.SUBCHUNK_COUNT;
    }
}
