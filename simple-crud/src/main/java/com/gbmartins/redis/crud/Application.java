/*
 * The MIT License (MIT)
 * Copyright © 2015-2016 Gerson B. Martins (gbmartins.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 * and associated documentation files (the “Software”), to deal in the Software without 
 * restriction, including without limitation the rights to use, copy, modify, merge, publish, 
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gbmartins.redis.crud;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.gbmartins.redis.crud.dto.User;
import com.gbmartins.redis.service.UserService;

/**
 * The Class Application.
 */
public class Application implements CommandLineRunner {

	/** The service. */
	@Autowired
	private UserService service;

	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(Application.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])
	 */
	@Override
	public void run(String... arg0) throws Exception {
		boolean stay = true;
		String command = "";

		while (stay) {
			try {
				printMenu();
				command = System.console().readLine();
				stay = execute(command);
			} catch (Exception e) {
				System.err.println("Error. Please see log for details");
				stay = false;
			}
		}

		LOG.info("Bye!");

	}

	/**
	 * Execute.
	 *
	 * @param command
	 *            the command
	 * @return true, if successful
	 */
	private boolean execute(String command) {
		boolean result = true;

		String cmd = command.substring(0, 1).toUpperCase();

		switch (cmd) {
		case "C":
			createOrUpdateUser(command);
			break;
		case "R":
			readUser(command);
			break;
		case "U":
			updateUser(command);
			break;
		case "D":
			deleteUser(command);
			break;
		case "Q":
			result = false;
			break;
		}

		return result;
	}

	/**
	 * Creates the or update user.
	 *
	 * @param command
	 *            the command
	 */
	private void createOrUpdateUser(String command) {
		User user = getUserFromStringCommand(command);
		if (user == null) {
			printWarning(command);
			return;
		}

		String response = service.saveUser(user);
		if (response == null) {
			response = "User with key " + user.getEmail() + " already exits";
		}
		printMsg(response);
	}
	
	/**
	 * Update user.
	 *
	 * @param command the command
	 */
	private void updateUser(String command) {
		User user = getUserFromStringCommand(command);
		if (user == null) {
			printWarning(command);
			return;
		}

		String response = service.updateUser(user);
		if (response == null) {
			response = "User not found";
		}
		printMsg(response);
	}

	/**
	 * Delete user.
	 *
	 * @param command
	 *            the command
	 */
	private void deleteUser(String command) {
		String email = getEmailFromStringCommand(command);
		if (email == null) {
			printWarning(command);
			return;
		}

		long number = service.deleteUser(email);
		printMsg("Number of users deleted: " + number);
	}

	/**
	 * Read user.
	 *
	 * @param command
	 *            the command
	 */
	private void readUser(String command) {
		String email = getEmailFromStringCommand(command);
		if (email == null) {
			printWarning(command);
			return;
		}

		User user = service.getUserByEmail(email);
		String userString = user != null ? user.toString() : "not found";
		printMsg(userString);
	}

	/**
	 * Prints the msg.
	 *
	 * @param string
	 *            the string
	 */
	private void printMsg(String string) {
		System.out.println(
				System.lineSeparator() + "--------------------------------------------" + System.lineSeparator());
		System.out.println(string);
		System.out.println(
				System.lineSeparator() + "--------------------------------------------" + System.lineSeparator());
	}

	/**
	 * Prints the warning.
	 *
	 * @param command
	 *            the command
	 */
	private void printWarning(String command) {
		System.out.println("\"" + command + "\" is not valid! Command is something like that: \"C email@example.com John Doe\"");
	}

	/**
	 * Gets the email from string command.
	 *
	 * @param command
	 *            the command
	 * @return the email from string command
	 */
	private String getEmailFromStringCommand(String command) {
		String result = null;
		String[] split = command.split(" ");
		if (split.length < 2) {
			return result;
		}

		result = split[1];

		return result;
	}

	/**
	 * Gets the user from string command.
	 *
	 * @param command
	 *            the command
	 * @return the user from string command
	 */
	private User getUserFromStringCommand(String command) {

		User user = null;

		String[] split = command.split(" ");
		if (split.length < 4) {
			return user;
		}

		user = new User();
		user.setEmail(split[1]);
		user.setFirstname(split[2]);
		user.setLastname(split[3]);

		return user;
	}

	/**
	 * Prints the menu.
	 */
	private void printMenu() {
		System.out.println(MENU);
	}

	/** The menu. */
	private static String MENU = System.lineSeparator() + //
			"Commands: (email is the unique identifier)" + System.lineSeparator() + //
			"Format: Command [Parameters]" + System.lineSeparator() + //
			"\tCreate User: C email name lastname" + System.lineSeparator() + //
			"\tRead User  : R email" + System.lineSeparator() + //
			"\tUpdate User: U email name lastname" + System.lineSeparator() + //
			"\tDelete User: D email" + System.lineSeparator() + System.lineSeparator() + //
			"\tQuit       : Q" + System.lineSeparator() + System.lineSeparator();

}
