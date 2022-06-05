package com.github.garrettnixsoft;

import com.github.garrettnixsoft.commands.Commands;
import com.github.garrettnixsoft.commands.WordFile;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.Scanner;

public class RunWordsBot {

	public static void main(String[] args) {

		// LOAD WORDS
		boolean wordsLoaded = WordFile.loadWordsFromFile();
		if (!wordsLoaded) {
			System.out.println("Word loading failed!");
			System.exit(-1);
		}

		// LOG IN BOT
		String token = "OTgyODY4OTU5NzA5NzY1NjU0.G4NipU._7DEdYz4mz_CVZyDoYGEs3NgiGBfsZbbMz0_78";
		DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

		// CREATE/UPDATE COMMANDS?
		Scanner input = new Scanner(System.in);

		System.out.println("Create commands?");
		String response = input.nextLine();

		if (Boolean.parseBoolean(response)) {
			Commands.createCommands(api);
		}

		System.out.println("Update commands?");
		response = input.nextLine();

		if (Boolean.parseBoolean(response)) {
			Commands.updateCommands(api);
		}

		input.close();

		// CONNECT TO COMMAND API
		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
			System.out.println("Command detected, id = " + slashCommandInteraction.getCommandId());
			Commands.executeCommand(slashCommandInteraction);
		});

		// GET BOT LINK
		System.out.println("Invite link for bot: " + api.createBotInvite());

		User me = api.getUserById(183629243454980096L).join();
		Server server = api.getServerById(584093301658091553L).get();
		me.updateNickname(server, "Bots are fun").join();


	}

}
