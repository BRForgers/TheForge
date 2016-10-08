package cf.brforgers.bot.handlers;

import cf.brforgers.bot.Bot;
import cf.brforgers.bot.data.BotData;
import cf.brforgers.bot.data.Configs;
import cf.brforgers.bot.utils.Answers;
import cf.brforgers.bot.utils.Statistics;
import cf.brforgers.bot.utils.Utils;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static cf.brforgers.bot.utils.Answers.bool;
import static cf.brforgers.bot.utils.Answers.send;
import static cf.brforgers.bot.utils.Formatter.bold;
import static cf.brforgers.bot.utils.Formatter.encase;
import static cf.brforgers.bot.utils.Utils.*;

public class SimpleCommandHandler {
	public static final List<String> prefixes = new ArrayList<>();
	public static final Map<String, BiConsumer<String, GuildMessageReceivedEvent>> commands = new HashMap<>();
	public static final Map<BiConsumer<String, GuildMessageReceivedEvent>, String> help = new HashMap<>();

	public static void init() {
		prefixes.clear();
		prefixes.add("~~");
		prefixes.add("<@!" + Bot.SELF.getId() + "> ");
		prefixes.add("<@" + Bot.SELF.getId() + "> ");

		commands.clear();
		commands.put("roles", (args, event) -> send(event, bold("Guild Roles:") + "\n" + encase(String.join(", ", Bot.GUILD.getRoles().stream().filter(role -> role != Bot.GUILD.getPublicRole()).map(Role::getName).toArray(String[]::new)))));

		help.put(commands.get("roles"), "List the Guild Roles");

		commands.put("list", (args, event) -> {
			BotData.get().validateAll();
			BotData.get().save();
			send(event, bold("Joinable Roles:") + "\n" + encase(String.join(", ", BotData.get().assignableRoles.stream()
					.filter(BotData.RoleData::validate)
					.filter(roleData -> roleData.permRequired <= getPermLevel(event.getAuthor()))
					.filter(roleData -> roleData != null)
					.map(roleData -> roleData.name).toArray(String[]::new))
				)
			);
		});

		help.put(commands.get("list"), "List the Joinable Roles");

		commands.put("nick", (args, event) -> {
			event.getGuild().getManager().setNickname(event.getAuthor(), args);
			bool(event, true);
		});

		commands.put("setnick", (arg, event) -> {
			String[] args = splitArgs(arg, 2);
			if (getPermLevel(event.getAuthor()) == 0 || args[0].isEmpty()) {
				send(event, "*Did you mean:* `~~nick " + arg + "`?");
				return;
			}

			event.getGuild().getManager().setNickname(Bot.GUILD.getUserById(Utils.idFromRawMention(args[0])), args[1]);
			bool(event, true);
		});

		help.put(commands.get("nick"), "List the Joinable Roles");


		commands.put("assign", (args, event) -> {
			if (getPermLevel(event.getAuthor()) == 0) return;
			String[] arg = splitArgs(args, 2);
			User target =
				(event.getMessage().getMentionedUsers().isEmpty() ? null : event.getMessage().getMentionedUsers().get(event.getMessage().getMentionedUsers().size() - 1));
			if (target == null || target == Bot.SELF) {
				send(event, "Please assign someone that is not me.");
				return;
			}
			BotData.RoleData role = BotData.get().assignableRoles.stream().filter(roleData -> roleData.name.equals(arg[1])).findFirst().orElse(null);
			if (role == null) {
				send(event, "Role can't be assigned.");
			} else if (!role.validate()) {
				send(event, "Role is invalid.");
			} else if (getPermLevel(event.getAuthor()) < role.permRequired) {
				send(event, "Not enough Permissions.");
			} else {
				Bot.GUILD.getManager().addRoleToUser(target, role.getRole()).update();
				bool(event, true);
			}
		});

		help.put(commands.get("assign"), "Assign Roles to someone");

		commands.put("join", (args, event) -> {
			BotData.RoleData role = BotData.get().assignableRoles.stream().filter(roleData -> roleData.name.equals(args)).findFirst().orElse(null);
			if (role == null) {
				send(event, "Role can't be assigned.");
			} else if (!role.validate()) {
				send(event, "Role is invalid.");
			} else if (getPermLevel(event.getAuthor()) < role.permRequired) {
				send(event, "Not enough Permissions.");
			} else {
				Bot.GUILD.getManager().addRoleToUser(event.getAuthor(), role.getRole()).update();
				bool(event, true);
			}
		});

		help.put(commands.get("join"), "Join a Role");

		commands.put("allow", (args, event) -> {
			if (getPermLevel(event.getAuthor()) != 2) return;
			User target =
				(event.getMessage().getMentionedUsers().isEmpty() ? null : event.getMessage().getMentionedUsers().get(event.getMessage().getMentionedUsers().size() - 1));
			if (target == null || target == Bot.SELF) {
				send(event, "Please mention someone that is not me to be allowed.");
				return;
			}
			BotData.get().allowedPeople.add(target.getId());
			BotData.get().save();
			bool(event, true);
		});

		help.put(commands.get("allow"), "Allow someone to Moderate");

		commands.put("disallow", (args, event) -> {
			if (getPermLevel(event.getAuthor()) != 2) return;
			User target =
				(event.getMessage().getMentionedUsers().isEmpty() ? null : event.getMessage().getMentionedUsers().get(event.getMessage().getMentionedUsers().size() - 1));
			if (target == null || target == Bot.SELF) {
				send(event, "Please mention someone that is not me to be disassigned.");
				return;
			}
			BotData.get().allowedPeople.remove(target.getId());
			BotData.get().save();
			bool(event, true);
		});

		help.put(commands.get("disallow"), "Disallow someone to Moderate");

		commands.put("allow-role", (args, event) -> {
			if (getPermLevel(event.getAuthor()) != 2) return;
			if (args.trim().isEmpty()) {
				send(event, "Role is invalid.");
				return;
			}

			List<Role> roles = Bot.GUILD.getRoles().stream()
				.filter(role -> role.getName().toLowerCase().replace(" ", "_").equals(args.toLowerCase()))
				.filter(role -> role != Bot.GUILD.getPublicRole())
				.collect(Collectors.toList());
			if (roles.isEmpty()) {
				send(event, "Role is invalid.");
				return;
			}

			BotData.RoleData data = new BotData.RoleData();
			data.permRequired = -1;
			data.id = roles.get(0).getId();
			data.name = roles.get(0).getName();
			BotData.get().allowRoles.add(data);
			BotData.get().save();
			bool(event, true);
		});

		help.put(commands.get("allow-role"), "Allow everyone in a role to Moderate");


		commands.put("disallow-role", (args, event) -> {
			if (getPermLevel(event.getAuthor()) != 2) return;
			if (args.trim().isEmpty()) {
				send(event, "Role is invalid.");
				return;
			}

			BotData.RoleData roleData = BotData.get().allowRoles.stream()
				.filter(BotData.RoleData::validate)
				.filter(role -> role.getRole().getName().toLowerCase().replace(" ", "_").equals(args.toLowerCase()))
				.filter(role -> role.getRole() != Bot.GUILD.getPublicRole())
				.findFirst().orElse(null);

			if (roleData == null) {
				send(event, "Role is invalid.");
				return;
			}

			BotData.get().allowRoles.remove(roleData);
			BotData.get().save();
			bool(event, true);
		});

		help.put(commands.get("disallow-role"), "Disallow everyone in a role to Moderate");

		commands.put("add", (args, event) -> {
			if (getPermLevel(event.getAuthor()) != 2) return;
			String[] arg = splitArgs(args, 3); //add NAME ROLE [PERM_REQUIRED]
			if (arg[0].trim().isEmpty() || arg[1].trim().isEmpty()) {
				send(event, "Role is invalid.");
				return;
			}

			int r = Statistics.parseInt(arg[2], 0);
			List<Role> roles = Bot.GUILD.getRoles().stream()
				.filter(role -> role.getName().toLowerCase().replace(" ", "_").equals(arg[1].toLowerCase()))
				.filter(role -> role != Bot.GUILD.getPublicRole())
				.collect(Collectors.toList());
			if (roles.isEmpty()) {
				send(event, "Role is invalid.");
				return;
			}

			BotData.RoleData data = new BotData.RoleData();
			data.permRequired = r;
			data.id = roles.get(0).getId();
			data.name = arg[0];
			BotData.get().assignableRoles.add(data);
			BotData.get().save();
			bool(event, true);
		});

		help.put(commands.get("add"), "Add a new Joinable Role");

		commands.put("allowed", (args, event) -> {
			List<User> users = new ArrayList<>();
			BotData.get().validateAll();
			BotData.get().allowedPeople.forEach(s -> users.add(Bot.API.getUserById(s)));
			BotData.get().allowRoles.forEach(roleData -> users.addAll(Bot.GUILD.getUsersWithRole(roleData.getRole())));
			List<User> filteredUsers = new ArrayList<>();
			filteredUsers.add(Bot.API.getUserById(Configs.get().ownerID));
			users.forEach(user -> {
				if (!filteredUsers.contains(user)) filteredUsers.add(user);
			});
			send(event, bold("Allowed:") + "\n" + encase(String.join(", ", filteredUsers.stream().map(user -> name(user, Bot.GUILD)).toArray(String[]::new))));
		});

		help.put(commands.get("allowed"), "List everyone allowed to moderate");

		commands.put("defaultRoles", (arg, event) -> {
			if (getPermLevel(event.getAuthor()) != 2) return;
			String[] args = splitArgs(arg, 2);
		});

		help.put(commands.get("defaultRoles"), "Control the Default Roles given to a User");

		commands.put("?", (arg, event) -> send(event, bold("Commands:") + encase(String.join("\n", commands.entrySet().stream().map(entry -> entry.getKey() + " - " + help.get(entry.getValue())).sorted().toArray(String[]::new)))));

		commands.put("help", commands.get("?"));
		commands.put("cmds", commands.get("?"));

		help.put(commands.get("?"), "Show the Commands available");

		commands.put("stop", (arg, event) -> {
			if (getPermLevel(event.getAuthor()) != 2) return;
			Bot.stopBot();
		});

		help.put(commands.get("stop"), "Stop the bot");

		commands.put("restart", (arg, event) -> {
			if (getPermLevel(event.getAuthor()) != 2) return;
			Bot.restartBot();
		});

		help.put(commands.get("restart"), "Restart the bot");

		commands.put("fixthatnpe", (arg, event) -> {
			if (getPermLevel(event.getAuthor()) == 0) return;
			BotData.get().validateAll();
			BotData.get().save();
		});

		help.put(commands.get("fixthatnpe"), "Fix THAT NPE");
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

	public static int getPermLevel(User user) {
		if (user.getId().equals(Configs.get().ownerID)) return 2;
		if (BotData.get().allowedPeople.contains(user.getId())) return 1;
		List<Role> roles = BotData.get().allowRoles.stream().filter(BotData.RoleData::validate).map(BotData.RoleData::getRole).collect(Collectors.toList());
		if (Bot.GUILD.getRolesForUser(user).stream().filter(roles::contains).count() != 0) return 1;
		return 0;
	}
}
