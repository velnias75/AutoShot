/*
 * Copyright 2022 by Heiko Schäfer <heiko@rangun.de>
 *
 * This file is part of AutoShot.
 *
 * AutoShot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * AutoShot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with AutoShot.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.rangun.autoshot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.rangun.autoshot.AutoShotMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(MinecraftClient.class)
public final class MinecraftClientMixin {

	private long tickCounter = -1L;

	@Inject(method = "tick", at = @At("HEAD"))
	public void onTick(CallbackInfo ci) {

		@SuppressWarnings("resource")
		final ClientPlayerEntity player = MinecraftClient.getInstance().player;

		if (player == null) {
			tickCounter = -1L;
			return;
		} else {
			++tickCounter;
		}

		if (tickCounter % 200 == 0) {
			AutoShotMod.doTick(tickCounter);
		}
	}
}
