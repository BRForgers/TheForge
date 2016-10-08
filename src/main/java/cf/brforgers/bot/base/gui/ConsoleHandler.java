/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/10/16 17:21]
 */

package cf.brforgers.bot.base.gui;

import cf.brforgers.bot.Bot;
import cf.brforgers.bot.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConsoleHandler {
	public static final Map<String, BiConsumer<String, Consumer<String>>> CMDS = new HashMap<>();
	public static final Map<BiConsumer<String, Consumer<String>>, String> HELP = new HashMap<>();

	static {
		CMDS.put("?", (s, in) -> CMDS.entrySet().stream().map(entry -> entry.getKey() + " - " + HELP.get(entry.getValue())).sorted().forEach(in));
		CMDS.put("help", CMDS.get("?"));
		CMDS.put("cmds", CMDS.get("?"));
		HELP.put(CMDS.get("?"), "Help");

		CMDS.put("stop", (s, in) -> Bot.stopBot());
		HELP.put(CMDS.get("stop"), "Stop the Bot");

		CMDS.put("restart", (s, in) -> Bot.restartBot());
		HELP.put(CMDS.get("restart"), "Restart the Bot");
	}

	public static void handle(String command, Consumer<String> out) {
		String[] parts = Utils.splitArgs(command, 2);
		BiConsumer<String, Consumer<String>> cmd = CMDS.getOrDefault(parts[0].toLowerCase(), CMDS.get("?"));
		cmd.accept(parts[1], in -> out.accept("<" + parts[0] + "> " + in));
	}

	public static Consumer<String> wrap(Consumer<String> c) {
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
		return s -> c.accept(
			"[" + f.format(new Date()) + "] [Console]: " + s
		);
	}
}
