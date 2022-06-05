package com.github.garrettnixsoft.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class WordFile {

	private static HashSet<String> words;
	private static HashSet<String> newWords;
	private static HashSet<String> removedWords;

	static Path path = Paths.get("resources/words.txt");

	public static boolean loadWordsFromFile() {

		words = new HashSet<>();
		newWords = new HashSet<>();
		removedWords = new HashSet<>();

		try  (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					line = line.replace(",", "");
					words.add(line);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean addWord(String word) {
		if (words.contains(word) || word.isEmpty() || word.length() > 30) {
			return false;
		} else {
			words.add(word);
			newWords.add(word);
			return true;
		}
	}

	public static boolean removeWord(String word) {
		if (words.contains(word)) {
			words.remove(word);
			if (newWords.contains(word)) {
				newWords.remove(word);
			}
			else {
				removedWords.add(word);
			}
			return true;
		}
		else return false;
	}

	public static HashSet<String> getUnsavedWords() {
		return newWords;
	}

	public static HashSet<String> getUnsavedRemovals() {
		return removedWords;
	}

	public static boolean saveWordsToFile() {

		if (newWords.isEmpty() && removedWords.isEmpty()) {
			return false;
		}

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

			for (String word : words) {
				writer.append(word).append(",");
				writer.newLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		newWords.clear();
		removedWords.clear();

		return true;
	}

	public static File getWordList() {

		return path.toFile();

	}

}
