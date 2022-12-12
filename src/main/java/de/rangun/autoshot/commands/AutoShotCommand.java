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

package de.rangun.autoshot.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.rangun.autoshot.config.AutoShotConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;

public final class AutoShotCommand implements Command<FabricClientCommandSource> {

	private final boolean withArg;

	public AutoShotCommand(final boolean withArg) {
		this.withArg = withArg;
	}

	@SuppressWarnings("resource")
	@Override
	public int run(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {

		final ConfigHolder<AutoShotConfig> configHolder = AutoConfig.getConfigHolder(AutoShotConfig.class);
		final AutoShotConfig config = configHolder.getConfig();

		if (withArg) {

			final Long ticks = context.getArgument("tick_interval", Long.class);
			final int idx = config.tick_intervals.indexOf(ticks);

			if (idx == -1) {

				if (config.tick_intervals.add(ticks)) {

					config.tick_interval_idx = config.tick_intervals.indexOf(ticks);
					configHolder.save();

					context.getSource().getClient().inGameHud.getChatHud()
							.addMessage(new LiteralText("Added AutoShot interval " + ticks + " as new preset."));
				}

			} else {
				config.tick_interval_idx = idx;
				configHolder.save();
			}
		}

		sendCurrentInterval(context, config.tick_intervals.get(config.tick_interval_idx));

		return Command.SINGLE_SUCCESS;
	}

	@SuppressWarnings("resource")
	private void sendCurrentInterval(final CommandContext<FabricClientCommandSource> context, final Long ticks) {
		context.getSource().getClient().inGameHud.getChatHud()
				.addMessage(new LiteralText("Current AutoShot interval in ticks: " + ticks));
	}
}
