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

	public static String calculate(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			return "<Null Input>";
		}

		//milliseconds
		long different = endDate.getTime() - startDate.getTime();

		if (different <= 0) {
			return "<Bad time>";
		}

		different = different / 1000;
		long minutesInMilli = 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different;

		return String.format(
			"%d days, %d hours, %d minutes, %d seconds",
			elapsedDays,
			elapsedHours, elapsedMinutes, elapsedSeconds);

	}

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
			boldAndItalic("Session Statistics:") + "\n" + encase(
				"Uptime: " + calculate(startDate, new Date())
					+ "\n - " + Statistics.msgs + " messages sent"
					+ "\n - " + Statistics.cmds + " commands executed"
					+ "\n - " + Statistics.crashes + " exceptions"
					+ "\n - " + Statistics.toofasts + " spams"
					+ "\n - " + Thread.activeCount() + " active threads"
					+ "\n - Invalid Arguments: " + Statistics.invalidargs
					+ "\n - RAM Usage(Using/Total/Max): " + ((instance.totalMemory() - instance.freeMemory()) / mb) + " MB/" + (instance.totalMemory() / mb) + " MB/" + (instance.maxMemory() / mb) + " MB"
					+ "\n - CPU Usage: " + cpuUsage + "%"
			)
		);
	}
}
