/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [22/09/16 18:35]
 */

package cf.brforgers.bot;

import cf.adriantodt.bot.data.Guilds;
import cf.adriantodt.bot.hardimpl.I18nHardImpl;
import cf.brforgers.bot.data.Configs;
import cf.brforgers.bot.handlers.BotGreeter;
import cf.brforgers.bot.handlers.BotIntercommns;
import cf.brforgers.bot.handlers.CommandHandler;
import cf.brforgers.bot.handlers.ReadyBuilder;
import cf.brforgers.bot.utils.Statistics;
import cf.brforgers.bot.utils.Tasks;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.hooks.AnnotatedEventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Bot {
	public static final Random RAND = new Random();
	public static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
	public static final Gson JSON_INTERNAL = new GsonBuilder().create();
	public static Logger LOGGER = LogManager.getLogger("Bot");
	public static JDA API = null;
	public static User SELF = null;
	public static boolean LOADED = false;
	public static List<Runnable> onLoaded = new ArrayList<>();

	static {
		onLoaded.add(() -> {
			User user = API.getUserById(Configs.getConfigs().ownerID);
			if (user == null) {
				LOGGER.warn("Owner not regognized. This WILL cause issues (specially PermSystem)");
			} else {
				LOGGER.info("Owner recognized: " + user.getUsername() + "#" + user.getDiscriminator() + " (ID: " + user.getId() + ")");
			}
		});
	}

	public static void init() throws Exception {
		Tasks.startAsyncTasks();
		new JDABuilder()
			.setBotToken(Configs.getConfigs().token)
			.setBulkDeleteSplittingEnabled(false)
			.setAudioEnabled(false)
			.setEventManager(new AnnotatedEventManager())
			.addListener(
				//In order:
				// ReadyBuilder (Unregisters itself after);
				// Commands -> BotIntercommns -> BotGreeter -> Guilds
				new ReadyBuilder()
					.add(event -> API = event.getJDA())
					.add(event -> SELF = event.getJDA().getSelfInfo())
					.add(event -> event.getJDA().getAccountManager().setGame("made of stone"))
					.add(event -> I18nHardImpl.impl())
					.add(event -> Statistics.startDate = new Date()),
				CommandHandler.class, BotIntercommns.class, BotGreeter.class, Guilds.class
			).buildBlocking();
		LOADED = true;
		onLoaded.forEach(Runnable::run);
		onLoaded = null;
		LOGGER = LogManager.getLogger(SELF.getUsername());
		LOGGER.info("Bot: " + SELF.getUsername() + " (#" + SELF.getId() + ")");
	}

	public static void stopBot() {
		API.getAccountManager().setIdle(true);
		API.getAccountManager().update();
		LOGGER.info("Bot exiting...");
		try {
			Thread.sleep(2 * 1000);
		} catch (Exception ignored) {
		}
		Java.stopApp();
	}

	public static void restartBot() {
		API.getAccountManager().setIdle(true);
		API.getAccountManager().update();
		LOGGER.info("Bot restarting...");
		try {
			Thread.sleep(2 * 1000);
		} catch (Exception ignored) {
		}
		Java.restartApp();
	}
}
