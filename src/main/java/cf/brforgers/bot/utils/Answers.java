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

import cf.brforgers.bot.Bot;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;


public class Answers {
	public static void exception(GuildMessageReceivedEvent event, Exception e) {
		dear(event, "uma exceção ocorreu durante a execução do comando:");
		sendCased(event, Utils.limit(e.toString(), 500), "java");
		Bot.LOGGER.error("Exception occurred during command \"" + event.getMessage().getContent() + "\": ", e);
		Statistics.crashes++;
	}

	public static void toofast(GuildMessageReceivedEvent event) {
		send(event, Formatter.italic(Utils.name(Bot.SELF, event.getGuild()) + " is ignoring you due to your spam. Try again later."));
	}

	public static void send(GuildMessageReceivedEvent event, String message) {
		event.getChannel().sendMessageAsync(message, null);
		Statistics.msgs++;
	}

	public static void sendCased(GuildMessageReceivedEvent event, String message) {
		sendCased(event, message, "");
	}

	public static void sendCased(GuildMessageReceivedEvent event, String message, String format) {
		send(event, Formatter.encase(message, format));
	}

	public static void announce(GuildMessageReceivedEvent event, String message) {
		send(event, Formatter.boldAndItalic(message));
	}

	public static void bool(GuildMessageReceivedEvent event, boolean v) {
		send(event, (v ? ":white_check_mark:" : ":negative_squared_cross_mark:"));
	}

	public static void dear(GuildMessageReceivedEvent event, String answer) {
		send(event, Formatter.italic("Dear " + event.getAuthor().getUsername() + ", " + answer));
	}
}
