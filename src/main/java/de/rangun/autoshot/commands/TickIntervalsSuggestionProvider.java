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

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import de.rangun.autoshot.config.AutoShotConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@Environment(EnvType.CLIENT)
public final class TickIntervalsSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> { // NOPMD
																												// by
																												// heiko
																												// on
																												// 12.12.22,
																												// 08:24

	@Override
	public CompletableFuture<Suggestions> getSuggestions(final CommandContext<FabricClientCommandSource> context,
			final SuggestionsBuilder builder) throws CommandSyntaxException {

		final AutoShotConfig config = AutoConfig.getConfigHolder(AutoShotConfig.class).getConfig();

		for (final Long ticks : config.tick_intervals) {
			builder.suggest(ticks.toString());
		}

		return builder.buildFuture();
	}

}
