/*
 * This class wasn't created by <AdrianTodt>.
 * It's a modification of Minecraft's Server
 * Management GUI. It have been modificated
 * to fit Java 8 and the Bot instead.
 */

package cf.brforgers.bot.base.gui;

import cf.brforgers.bot.Bot;
import cf.brforgers.bot.data.Configs;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.hooks.SubscribeEvent;

import javax.swing.*;
import java.util.Vector;

import static cf.brforgers.bot.utils.Utils.name;

public class UsersListComponent extends JList implements Runnable {
	public UsersListComponent() {
		run();
		Bot.onLoaded.add(this);
		Bot.onLoaded.add(() -> Bot.API.addEventListener(this));
	}

	@SubscribeEvent
	public void onStatusUpdate(UserOnlineStatusUpdateEvent event) {
		run();
	}

	@SuppressWarnings("unchecked")
	public void run() {
		Vector<String> vector = new Vector<>();
		if (Bot.API == null || Bot.API.getStatus() == JDA.Status.INITIALIZING) {
			vector.add("<Bot being Loaded>");
		} else {
			Bot.API.getGuildById(Configs.getConfigs().guildID).getUsers().stream()
				.filter(user -> user.getOnlineStatus() != OnlineStatus.OFFLINE)
				.map(user -> name(user, Bot.API.getGuildById(Configs.getConfigs().guildID)) + (user.isBot() ? " [BOT]" : ""))
				.forEach(vector::add);
		}
		this.setListData(vector);
	}
}