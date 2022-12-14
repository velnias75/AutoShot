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

package de.rangun.autoshot; // NOPMD by heiko on 12.12.22, 08:23

import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.DISPATCHER;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFW;

import de.rangun.autoshot.commands.AutoShotCommand;
import de.rangun.autoshot.commands.TickIntervalsSuggestionProvider;
import de.rangun.autoshot.config.AutoShotConfig;
import de.rangun.autoshot.config.AutoShotConfig.DAYTIME;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.world.GameRules;

@Environment(EnvType.CLIENT)
public final class AutoShotMod implements ClientModInitializer { // NOPMD by heiko on 09.12.22, 14:55

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss", Locale.US);
	private static final String AUTOSHOT_STATE = "AutoShot is now ";

	private ConfigHolder<AutoShotConfig> configHolder;

	private static KeyBinding kbSettings;
	private static KeyBinding kbEnable;

	@SuppressWarnings("resource")
	@Override
	public void onInitializeClient() {

		AutoConfig.register(AutoShotConfig.class, GsonConfigSerializer::new);

		configHolder = AutoConfig.getConfigHolder(AutoShotConfig.class);

		kbSettings = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.autoshot.settings", InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_U, "category.autoshot.keys"));

		kbEnable = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.autoshot.enable", InputUtil.Type.KEYSYM,
				InputUtil.UNKNOWN_KEY.getCode(), "category.autoshot.keys"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			while (kbSettings.wasPressed()) {
				client.openScreen(AutoConfig.getConfigScreen(AutoShotConfig.class, null).get());
			}

			while (kbEnable.wasPressed()) {

				configHolder.getConfig().enabled = !configHolder.getConfig().enabled;
				configHolder.save();

				if (configHolder.getConfig().enabled) {
					MinecraftClient.getInstance().inGameHud.setOverlayMessage(
							new LiteralText(AUTOSHOT_STATE).append(new LiteralText("on").formatted(Formatting.GREEN)), // NOPMD
																														// by
																														// heiko
																														// on
																														// 10.12.22,
																														// 12:52
							false);
				} else {
					MinecraftClient.getInstance().inGameHud.setOverlayMessage(
							new LiteralText(AUTOSHOT_STATE).append(new LiteralText("off").formatted(Formatting.RED)), // NOPMD
																														// by
																														// heiko
																														// on
																														// 10.12.22,
																														// 12:52
							false);
				}
			}

			// Fixes #1
			if (configHolder.getConfig().enabled && configHolder.getConfig().at_daytime
					&& MinecraftClient.getInstance().world != null
					&& MinecraftClient.getInstance().world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).get()
					&& MinecraftClient.getInstance().world
							.getTimeOfDay() == (DAYTIME.tick.equals(configHolder.getConfig().daytime)
									? configHolder.getConfig().daytime_tick
									: configHolder.getConfig().daytime.daytime_tick)) {
				saveScreenShot();
			}

		});

		DISPATCHER.register(literal("autoshot").then(argument("tick_interval", longArg())
				.suggests(new TickIntervalsSuggestionProvider()).executes(new AutoShotCommand(true)))
				.executes(new AutoShotCommand(false)));
	}

	@SuppressWarnings("resource")
	public static void saveScreenShot() {

		final NativeImage nativeImage = ScreenshotUtils.takeScreenshot(0, 0,
				MinecraftClient.getInstance().getFramebuffer());

		@SuppressWarnings("resource")
		final File file = new File(MinecraftClient.getInstance().runDirectory, "auto-shots");
		file.mkdir();

		final File file2 = getScreenshotFilename(file);

		Util.getIoWorkerExecutor().execute(() -> {

			try { // NOPMD by heiko on 09.12.22, 15:15

				nativeImage.writeFile(file2);

				final MutableText text = new LiteralText(file2.getName()).formatted(Formatting.UNDERLINE)
						.styled(style -> style
								.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath())));

				MinecraftClient.getInstance().inGameHud.getChatHud()
						.addMessage(new LiteralText("Took auto-shot: ").append(text));

			} catch (IOException ex) {

				LogManager.getLogger().warn("Couldn't save auto-screenshot", ex);
				MinecraftClient.getInstance().inGameHud.getChatHud()
						.addMessage(new LiteralText("Couldn't save auto-screenshot..."));

			} finally {
				nativeImage.close();
			}

		});
	}

	private static File getScreenshotFilename(final File directory) {

		File file;

		synchronized (DATE_FORMAT) {

			final String sdf = DATE_FORMAT.format(new Date());

			int i = 1; // NOPMD by heiko on 09.12.22, 14:44

			while ((file = new File(directory, sdf + (i == 1 ? "" : "_" + i) + ".png")).exists()) { // NOPMD by heiko
																									// on
																									// 09.12.22,
																									// 14:50
				++i;
			}
		}

		return file;
	}
}
