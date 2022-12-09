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
import de.rangun.autoshot.config.AutoShotConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(MinecraftClient.class)
public final class MinecraftClientMixin { // NOPMD by heiko on 09.12.22, 14:59

	private long tickCounter = -1L;

	@Inject(method = "tick", at = @At("HEAD"))
	public void onTick(final CallbackInfo callbackInfo) {

		@SuppressWarnings("resource")
		final ClientPlayerEntity player = MinecraftClient.getInstance().player;
		final AutoShotConfig config = AutoConfig.getConfigHolder(AutoShotConfig.class).getConfig();

		if (player == null) {
			tickCounter = -1L;
			return;
		} else {
			++tickCounter;
		}

		if (config.enabled && tickCounter > 0 && tickCounter % config.tick_interval == 0) {
			AutoShotMod.saveScreenShot();
		}
	}
}
