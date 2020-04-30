package com.techtask.martianrobots;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Scanner;

@SpringBootApplication
public class MartianRobotsApplication {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String[] grid = scanner.nextLine().split("\\s");

		while (scanner.hasNextLine()) {
			String[] startPosition = scanner.nextLine().split("\\s");
			char[] instructions = scanner.nextLine().toCharArray();
			System.out.println("Grid " + Arrays.toString(grid));
			System.out.println("Start position " + Arrays.toString(startPosition));
			System.out.println("instructions " + Arrays.toString(instructions));
		}

		scanner.close();
	}

}
