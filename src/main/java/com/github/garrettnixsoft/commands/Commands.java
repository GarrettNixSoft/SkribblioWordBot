package com.github.garrettnixsoft.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.ServerBuilder;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class Commands {

	public static void createCommands(DiscordApi api) {

		SlashCommand wordCommand = SlashCommand.with("word", "A command to add a word",
				List.of(SlashCommandOption.create(SlashCommandOptionType.STRING, "WORD", "A word to add", true)))
				.createGlobal(api).join();
		System.out.println("Created command \"word\", id: " + wordCommand.getId());

		SlashCommand saveCommand = SlashCommand.with("save", "A command to save newly added words")
				.createGlobal(api).join();
		System.out.println("Created command \"save\", id: " + saveCommand.getId());

		SlashCommand listCommand = SlashCommand.with("list", "A command to get the list of words")
				.createGlobal(api).join();
		System.out.println("Created command \"list\", id: " + listCommand.getId());

		SlashCommand removeCommand = SlashCommand.with("remove", "A command to remove a word from the list",
						List.of(SlashCommandOption.create(SlashCommandOptionType.STRING, "REMOVE", "The word to remove", true)))
				.createGlobal(api).join();
		System.out.println("Created command \"remove\", id: " + removeCommand.getId());

		SlashCommand unsavedCommand = SlashCommand.with("changes", "A command to see new words that are not yet saved")
				.createGlobal(api).join();
		System.out.println("Created command \"changes\", id: " + unsavedCommand.getId());

	}

	public static void createServerCommands(DiscordApi api) {
		Optional<Server> serverOpt = api.getServerById(957854387450028043L);
		System.out.println("Got server from ID");
		Server server = serverOpt.get();

		SlashCommand wordCommand = SlashCommand.with("word", "A command to add a word",
						List.of(SlashCommandOption.create(SlashCommandOptionType.STRING, "WORD", "A word to add", true)))
				.createForServer(server).join();
		System.out.println("Created command \"word\", id: " + wordCommand.getId());

		SlashCommand saveCommand = SlashCommand.with("save", "A command to save newly added words")
				.createForServer(server).join();
		System.out.println("Created command \"save\", id: " + saveCommand.getId());

		SlashCommand listCommand = SlashCommand.with("list", "A command to get the list of words")
				.createForServer(server).join();
		System.out.println("Created command \"list\", id: " + listCommand.getId());

	}

	private static void createNewCommandTemporary(DiscordApi api) {



	}

	public static void updateCommands(DiscordApi api) {

		new SlashCommandUpdater(982894877375356948L).setSlashCommandOptions(
						List.of(SlashCommandOption.create(SlashCommandOptionType.STRING, "REMOVE", "The word to remove", true)))
				.updateGlobal(api).join();

	}

	public static void executeCommand(SlashCommandInteraction interaction) {
		switch (interaction.getCommandName()) {
			case "word" -> executeWordCommand(interaction);
			case "save" -> executeSaveCommand(interaction);
			case "list" -> executeListCommand(interaction);
			case "remove" -> executeRemoveCommand(interaction);
			case "changes" -> executeChangesCommand(interaction);
			default -> {}
		}
	}

	private static void executeWordCommand(SlashCommandInteraction interaction) {
		Optional<String> wordOpt = interaction.getOptionStringValueByIndex(0);
		String word = wordOpt.orElse("");
		boolean success = WordFile.addWord(word);
		InteractionImmediateResponseBuilder responseBuilder = interaction.createImmediateResponder();

		if (success) {
			responseBuilder.setContent("Successfully added ```" + word + "``` to the word list.\nUse ```/save``` to commit your change(s) to the word file.");
		} else {
			if (word.length() > 30) {
				responseBuilder.setContent("Could not add ```" + word + "``` to the word list. Words must be 30 characters or less.");
			}
			else {
				responseBuilder.setContent("Could not add ```" + word + "``` to the word list. Duplicates are not allowed.");
			}
		}
		responseBuilder.respond();
	}

	private static void executeSaveCommand(SlashCommandInteraction interaction) {
		boolean success = WordFile.saveWordsToFile();
		InteractionImmediateResponseBuilder responseBuilder = interaction.createImmediateResponder();
		if (success) {
			responseBuilder.setContent("Successfully saved changes.");
		} else {
			responseBuilder.setContent("Failed to save changes. (There may not have been any new changes to save)");
		}
		responseBuilder.respond();
	}

	private static void executeListCommand(SlashCommandInteraction interaction) {
		File wordFile = WordFile.getWordList();
		interaction.createImmediateResponder().setContent("Fetching word list... (You will receive a follow up message)").respond();
		interaction.createFollowupMessageBuilder().addAttachment(wordFile).send();
	}

	public static void executeRemoveCommand(SlashCommandInteraction interaction) {
		Optional<String> wordOpt = interaction.getOptionStringValueByIndex(0);
		String word = wordOpt.orElse("");
		if (!word.isEmpty()) {
			boolean success = WordFile.removeWord(word);
			InteractionImmediateResponseBuilder responseBuilder = interaction.createImmediateResponder();
			if (success) {
				responseBuilder.setContent("Successfully removed ```" + word + "``` from the word list.\nIf this word has already been saved to the word file, use ```/save``` to commit this change.");
			}
			else {
				responseBuilder.setContent("Could not remove ```" + word + "``` from the word list; no such entry exists.");
			}
			responseBuilder.respond();
		}
	}

	public static void executeChangesCommand(SlashCommandInteraction interaction) {
		HashSet<String> newWords = WordFile.getUnsavedWords();
		HashSet<String> removedWords = WordFile.getUnsavedRemovals();
		if (newWords.isEmpty()) {
			interaction.createImmediateResponder().setContent("No unsaved changes.").respond();
			return;
		}
		StringBuilder content = new StringBuilder();
		content.append("Unsaved changes:\n```");
		for (String word : newWords) {
			content.append("+ ").append(word).append("\n");
		}
		for (String word : removedWords) {
			content.append("- ").append(word).append("\n");
		}
		content.append("```");
		interaction.createImmediateResponder().setContent(content.toString()).respond();
	}

}
