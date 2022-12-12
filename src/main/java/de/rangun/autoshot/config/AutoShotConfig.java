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

package de.rangun.autoshot.config;

import java.util.Arrays;
import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "autoshot")
@Config.Gui.Background(Config.Gui.Background.TRANSPARENT)
public final class AutoShotConfig implements ConfigData { // NOPMD by heiko on 10.12.22, 12:38

	public enum DAYTIME {

		tick(-1L), day(1000L), noon(6000L), night(13_000L), midnight(18_000L); // NO_UCD (unused code)

		public final long daytime_tick;

		DAYTIME(final long daytime_tick) {
			this.daytime_tick = daytime_tick;
		}
	}

	public boolean enabled = false; // NOPMD by heiko on 10.12.22, 12:38

	public boolean at_interval = true;

	@ConfigEntry.Gui.Tooltip
	public List<Long> tick_intervals = Arrays.asList(200L);

	@ConfigEntry.Gui.Excluded
	public int tick_interval_idx;

	public boolean at_daytime;

	@ConfigEntry.Gui.Tooltip(count = 5)
	public DAYTIME daytime = DAYTIME.noon;

	public long daytime_tick = DAYTIME.noon.daytime_tick;

	@Override
	public void validatePostLoad() throws ValidationException {

		if (daytime_tick < 0L || daytime_tick > 24_000L) {
			throw new ValidationException("daytime_tick must be between 0 and 24000");
		}
	}
}
