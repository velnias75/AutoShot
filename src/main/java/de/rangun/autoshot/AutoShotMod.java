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

package de.rangun.autoshot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

@Environment(EnvType.CLIENT)
public final class AutoShotMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
	}

	@SuppressWarnings("resource")
	public static void doTick(final long tick) {
		MinecraftClient.getInstance().inGameHud.getChatHud()
				.addMessage(new LiteralText("tick tock Urubar: " + (tick / 200L)));
	}
}
