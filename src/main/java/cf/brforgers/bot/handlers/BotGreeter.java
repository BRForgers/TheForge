/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:07]
 */

package cf.brforgers.bot.handlers;

import cf.brforgers.bot.Bot;
import cf.brforgers.bot.data.Configs;
import cf.brforgers.bot.utils.Utils;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.SubscribeEvent;

public class BotGreeter {
	public static void greet(GuildMessageReceivedEvent event) {
		event.getChannel().sendMessage("Hello, I'm " + Utils.name(Bot.SELF, event.getGuild()) + ", the guardian of this place. Please type `~~join en` or `~~join pt` to select your language");
	}

	@SubscribeEvent
	public static void onGuildJoin(GuildJoinEvent event) {
		if (!event.getGuild().getId().equals(Configs.getConfigs().guildID)) {
			event.getGuild().getManager().leave();
		}
	}

	@SubscribeEvent
	public static void onMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getMessage().getRawContent().trim().matches("<@!?" + event.getJDA().getSelfInfo().getId() + ">")) {
			greet(event);
		}
	}
}
