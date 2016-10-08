package cf.brforgers.bot.data;

import cf.brforgers.bot.Bot;
import net.dv8tion.jda.entities.Role;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static cf.brforgers.bot.Bot.JSON;

public class BotData {
	private static BotData data = null;
	public List<RoleData> automaticBotRole = new ArrayList<>();
	public List<RoleData> automaticUserRole = new ArrayList<>();
	public List<RoleData> assignableRoles = new ArrayList<>();
	public List<RoleData> allowRoles = new ArrayList<>();
	public List<String> allowedPeople = new ArrayList<>();

	public static BotData get() {
		if (data == null) load();
		return data;
	}

	private static Path path() {
		return DataManager.getPath(Bot.SELF.getUsername().replace(" ", "_").replace(":", ""), "json");
	}

	private static void load() {
		try {
			data = JSON.fromJson(new String(Files.readAllBytes(path()), Charset.forName("UTF-8")), BotData.class);
		} catch (Exception e) {
			data = new BotData();
			data.save();
		}
	}

	public void validateAll() {
		new ArrayList<>(automaticBotRole).forEach(RoleData::validate);
		new ArrayList<>(automaticUserRole).forEach(RoleData::validate);
		new ArrayList<>(assignableRoles).forEach(RoleData::validate);
		new ArrayList<>(allowRoles).forEach(RoleData::validate);
		new ArrayList<>(allowedPeople).forEach(id -> {
			if (Bot.API.getUserById(id) == null) allowedPeople.remove(id);
		});
	}

	public void save() {
		try {
			Files.write(path(), JSON.toJson(this).getBytes(Charset.forName("UTF-8")));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static class RoleData {
		public String id = "";
		public String name = "";
		public int permRequired = 0;

		public boolean validate() {
			boolean r = getRole() != null;
			if (!r) {
				BotData.get().automaticBotRole.remove(this);
				BotData.get().automaticUserRole.remove(this);
				BotData.get().assignableRoles.remove(this);
				BotData.get().allowRoles.remove(this);
			}
			return r;
		}

		public Role getRole() {
			return Bot.API.getGuildById(Configs.get().guildID).getRoleById(id);
		}
		//0 = ALL
		//1 = ALLOWED
		//2 = OWNER
		//Sometimes can be ignored due to be automatic roles
	}
}