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

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "autoshot")
@Config.Gui.Background(Config.Gui.Background.TRANSPARENT)
public final class AutoShotConfig implements ConfigData { // NOPMD by heiko on 10.12.22, 12:38

	public boolean enabled = false; // NOPMD by heiko on 10.12.22, 12:38

	public int tick_interval = 200;

	@Override
	public void validatePostLoad() throws ValidationException {

		if (tick_interval <= 0) {
			throw new ValidationException("Interval must be greater than 0");
		}
	}
}
