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
import net.dv8tion.jda.events.guild.GenericGuildEvent;
import net.dv8tion.jda.events.user.GenericUserEvent;
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
	public void updateG(GenericGuildEvent event) {
		run();
	}

	@SubscribeEvent
	public void updateU(GenericUserEvent event) {
		run();
	}

	@SuppressWarnings("unchecked")
	public void run() {
		Vector<String> vector = new Vector<>();
		if (Bot.API == null || Bot.API.getStatus() == JDA.Status.INITIALIZING) {
			vector.add("<Bot being Loaded>");
		} else {
			Bot.API.getGuildById(Configs.get().guildID).getUsers().stream()
				.filter(user -> user.getOnlineStatus() != OnlineStatus.OFFLINE)
				.map(user -> name(user, Bot.API.getGuildById(Configs.get().guildID)) + (user.isBot() ? " [BOT]" : ""))
				.sorted()
				.forEach(vector::add);
		}
		this.setListData(vector);
	}
}