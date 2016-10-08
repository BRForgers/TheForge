/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:11]
 */

package cf.brforgers.bot.utils;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;

public class Utils {
	public static boolean canTalk(GuildMessageReceivedEvent event) {
		int count;
		synchronized (Tasks.userTimeout) {
			count = Tasks.userTimeout.getOrDefault(event.getAuthor(), 0);
			Tasks.userTimeout.put(event.getAuthor(), count + 1);
		}
		return count + 1 < 5;
	}

	public static String[] splitArgs(String args, int expectedArgs) {
		String[] raw = args.split("\\s+", expectedArgs), normalized = new String[expectedArgs];

		Arrays.fill(normalized, "");
		for (int i = 0; i < normalized.length; i++) {
			if (i < raw.length && raw[i] != null && !raw[i].isEmpty()) {
				normalized[i] = raw[i];
			}
		}
		return normalized;
	}

	public static String name(User user, Guild guild) {
		return (guild == null || guild.getNicknameForUser(user) == null ? user.getUsername() : guild.getNicknameForUser(user));
	}

	public static Runnable asyncSleepThen(final int milis, final Runnable doAfter) {
		return async(() -> {
			try {
				Thread.sleep(milis);
				if (doAfter != null) doAfter.run();
			} catch (Exception ignored) {
			}
		});
	}

	public static Runnable async(Runnable doInNewThread) {
		return new Thread(doInNewThread)::start;
	}

	public static String limit(String value, int length) {
		StringBuilder buf = new StringBuilder(value);
		if (buf.length() > length) {
			buf.setLength(length - 3);
			buf.append("...");
		}

		return buf.toString();
	}

	public static String idFromRawMention(String string) {
		if (string.length() > 2 && (string.charAt(0) == '<' && string.charAt(1) == '@' && string.charAt(string.length() - 1) == '>'))
			string = string.substring(2, string.length() - 1);
		if (string.length() > 0 && string.charAt(0) == '!') string = string.substring(1);
		return string.toLowerCase();
	}
}
