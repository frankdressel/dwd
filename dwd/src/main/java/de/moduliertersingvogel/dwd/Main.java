package de.moduliertersingvogel.dwd;

import java.util.Scanner;

import org.apache.meecrowave.Meecrowave;

public class Main {

	public static void main(String[] args) {
		try (final Meecrowave meecrowave = new Meecrowave(); final Scanner scanner = new Scanner(System.in);) {
			meecrowave.bake();
			scanner.nextLine();
		}
	}
}
