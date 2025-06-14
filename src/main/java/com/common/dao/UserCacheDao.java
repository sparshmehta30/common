package com.common.dao;

import java.util.Map;

import com.common.dto.UserAuthDto;

public interface UserCacheDao {

	void saveUser(UserAuthDto user);

	UserAuthDto getUserById(Long userId);

	void updateUser(UserAuthDto user);

	Map<Long, UserAuthDto> getAllUsers();

	void deleteUser(Long userId);

	void saveAllUsers(Map<Long, UserAuthDto> usersMap);

}
