/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/10/16 07:53]
 */

package cf.brforgers.bot.data;

import cf.brforgers.bot.Java;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static cf.brforgers.bot.Bot.JSON;
import static cf.brforgers.bot.Bot.LOGGER;

public class Configs {
	private static Configs configs = null;
	public String token = "", ownerID = "", guildID = "";

	public static Configs get() {
		loadConfig();
		return configs;
	}

	private static Path path() {
		return DataManager.getPath("configs", "json");
	}

	private static void loadConfig() {
		if (configs != null) return;
		try {
			configs = JSON.fromJson(new String(Files.readAllBytes(path()), Charset.forName("UTF-8")), Configs.class);
			Objects.requireNonNull(configs, "Config object is null (How do you  managed to even do that?)");
			Objects.requireNonNull(configs.token, "Token doesn't exists/is null");
			Objects.requireNonNull(configs.ownerID, "OwnerID doesn't exists/is null");
			Objects.requireNonNull(configs.ownerID, "GuildID doesn't exists/is null");
		} catch (Exception e) {
			LOGGER.error("Configuration File not found or damaged. Generating one at " + path() + "...");
			configs = new Configs();
			try {
				Files.write(path(), JSON.toJson(configs).getBytes(Charset.forName("UTF-8")));
				LOGGER.error("Configuration File generated. Please fill the required configs. The App will now Exit.");
			} catch (Exception ex) {
				LOGGER.error("Configuration File could not be generated. Please fix the permissions. The App will now Exit.");
			}

			Java.stopApp();
		}
	}
}
