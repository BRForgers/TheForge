package cf.brforgers.bot.handlers;

import cf.brforgers.bot.Bot;
import cf.brforgers.bot.utils.Answers;
import cf.brforgers.bot.utils.Statistics;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static cf.brforgers.bot.utils.Utils.asyncSleepThen;
import static cf.brforgers.bot.utils.Utils.splitArgs;

public class SimpleCommandHandler {
	public static final List<String> prefixes = new ArrayList<>();
	public static final Map<String, BiConsumer<String, GuildMessageReceivedEvent>> commands = new HashMap<>();

	public static void init() {
		prefixes.clear();
		prefixes.add("~~");
		prefixes.add("<@!" + Bot.SELF.getId() + "> ");
		prefixes.add("<@" + Bot.SELF.getId() + "> ");

		commands.clear();
		commands.put("?", (args, event) -> {

		});
	}

	@SubscribeEvent
	public static void onMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getGuild().getPublicChannel() == event.getChannel()) {
			if (event.getAuthor() == Bot.SELF) { //Safer
				asyncSleepThen(15 * 1000, () -> event.getMessage().deleteMessage()).run();
				return;
			} else {
				asyncSleepThen(2 * 1000, () -> event.getMessage().deleteMessage()).run();
			}
		}

		String cmd = event.getMessage().getRawContent();

		boolean isCmd = false;
		for (String prefix : prefixes) {
			if (cmd.startsWith(prefix)) {
				cmd = cmd.substring(prefix.length());
				isCmd = true;
				break;
			}
		}

		//boolean exec = false;
		if (isCmd) { //Is Command
			String[] args = splitArgs(cmd, 2);
			BiConsumer<String, GuildMessageReceivedEvent> command = commands.get(args[0]);
			if (command != null) {
				Statistics.cmds++;
				try {
					command.accept(args[1], event);
				} catch (Exception e) {
					Answers.exception(event, e);
				}
			}
		}
	}
}
