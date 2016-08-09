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
package com.gbmartins.redis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gbmartins.redis.crud.dto.User;
import com.gbmartins.redis.dao.RedisOperations;
import com.google.common.collect.Lists;

/**
 * The Class UserService.
 */
@Service
public class UserService {

	/** The operations. */
	private RedisOperations operations;

	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(UserService.class);

	/**
	 * Instantiates a new user service.
	 *
	 * @param operations
	 *            the operations
	 */
	@Autowired
	public UserService(RedisOperations operations) {
		super();
		this.operations = operations;
	}

	/**
	 * Save or update user.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	public String saveOrUpdateUser(User user) {
		try {
			return operations.saveOrUpdateObject(user.getEmail(), user);
		} catch (IOException e) {
			LOG.error("Save User error");
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * Save user.
	 *
	 * @param user the user
	 * @return the string
	 */
	public String saveUser(User user) {
		try {
			if (operations.getObject(user.getEmail(), User.class) == null) {
				return operations.saveOrUpdateObject(user.getEmail(), user);
			}
			return null;
		} catch (Exception e) {
			LOG.error("Update User error");
			throw new RuntimeException(e);
		}
	}


	/**
	 * Update an existent user.
	 *
	 * @param user
	 *            the user
	 * @return the string
	 */
	public String updateUser(User user) {
		try {
			if (operations.getObject(user.getEmail(), User.class) != null) {
				return operations.saveOrUpdateObject(user.getEmail(), user);
			}
			return null;
		} catch (Exception e) {
			LOG.error("Update User error");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Save or update users.
	 *
	 * @param users
	 *            the users
	 * @return the list
	 */
	public List<User> saveOrUpdateUsers(User... users) {

		List<User> result = null;

		if (users != null) {

			try {
				Map<String, User> map = new HashMap<>();

				for (User user : users) {
					map.put(user.getEmail(), user);
				}
				operations.saveOrUpdateBulkObject(map);

			} catch (Exception e) {
				LOG.error("Save Users error");
				throw new RuntimeException(e);
			}

		} else {
			result = new ArrayList<>();
		}
		return result;

	}

	/**
	 * Delete user.
	 *
	 * @param email
	 *            the email
	 * @return the long
	 */
	public long deleteUser(String email) {
		try {
			return operations.deleteKeys(email);
		} catch (Exception e) {
			LOG.error("Delete User error");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Delete users.
	 *
	 * @param emails
	 *            the emails
	 * @return the long
	 */
	public long deleteUsers(String... emails) {
		try {
			return operations.deleteKeys(emails);
		} catch (Exception e) {
			LOG.error("Delete Users error");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the user by email.
	 *
	 * @param email
	 *            the email
	 * @return the user by email
	 */
	public User getUserByEmail(String email) {
		try {
			return operations.getObject(email, User.class);
		} catch (Exception e) {
			LOG.error("Get User error");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the users by emails.
	 *
	 * @param emails
	 *            the emails
	 * @return the users by emails
	 */
	public List<User> getUsersByEmails(String... emails) {
		try {
			return operations.getListObject(Lists.newArrayList(emails), User.class);
		} catch (Exception e) {
			LOG.error("Get Users error");
			throw new RuntimeException(e);
		}
	}

}
