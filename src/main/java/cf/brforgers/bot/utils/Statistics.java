/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:18]
 */

package cf.brforgers.bot.utils;

import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

import java.util.Date;

import static cf.brforgers.bot.utils.Answers.send;
import static cf.brforgers.bot.utils.Formatter.boldAndItalic;
import static cf.brforgers.bot.utils.Formatter.encase;
import static cf.brforgers.bot.utils.Tasks.cpuUsage;

public class Statistics {
	public static Date startDate = null;
	public static int crashes = 0, invalidargs = 0, msgs = 0, cmds = 0, toofasts = 0;

	public static int parseInt(String s, int onCatch) {
		try {
			return Integer.parseInt(s);
		} catch (Exception ignored) {
		}
		return onCatch;
	}

	public static void printStats(GuildMessageReceivedEvent event) {
		int mb = 1024 * 1024;
		Runtime instance = Runtime.getRuntime();
		send(event,
			boldAndItalic("Statistics") + "\n" + encase(
				"- Uptime: " + "<@217747278071463937> FIX THIS"
					+ "\n - " + Statistics.msgs + " messages sent"
					+ "\n - " + Statistics.cmds + " commands executed"
					+ "\n - " + Statistics.crashes + " exceptions"
					+ "\n - " + Statistics.toofasts + " spams"
					+ "\n - " + Thread.activeCount() + " active threads"
					+ "\n - Invalid Arguments: " + Statistics.invalidargs
					+ "\n - RAM Usage(Usando/Total/Máximo): " + ((instance.totalMemory() - instance.freeMemory()) / mb) + " MB/" + (instance.totalMemory() / mb) + " MB/" + (instance.maxMemory() / mb) + " MB"
					+ "\n - CPU Usage: " + cpuUsage + "%"
			)
		);
	}
}
