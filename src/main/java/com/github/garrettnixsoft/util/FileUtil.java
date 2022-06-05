package com.github.garrettnixsoft.util;

import org.javacord.api.entity.server.Server;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileUtil {

	public static String loadTextFile(String pathStr) {

		Path path = Paths.get(pathStr);

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

			String line;
			StringBuilder result = new StringBuilder();

			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					result.append(line);
				}
			}

			return result.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";

	}

}
