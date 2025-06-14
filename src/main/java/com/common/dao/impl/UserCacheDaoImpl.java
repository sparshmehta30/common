package com.common.dao.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import com.common.dao.UserCacheDao;
import com.common.dto.UserAuthDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserCacheDaoImpl implements UserCacheDao {

	private final String hashReference = "user-cache";

	@Resource(name = "userDetailsRedisTemplate")
	private HashOperations<String, Long, UserAuthDto> hashOperations;

	@Override
	public void saveUser(UserAuthDto user) {
		log.info(user.toString());
		hashOperations.putIfAbsent(hashReference, user.getId(), user);
	}

	@Override
	public UserAuthDto getUserById(Long userId) {
		return hashOperations.get(hashReference, userId);
	}

	@Override
	public void updateUser(UserAuthDto user) {
		hashOperations.put(hashReference, user.getId(), user);
	}

	@Override
	public Map<Long, UserAuthDto> getAllUsers() {
		return hashOperations.entries(hashReference);
	}

	@Override
	public void deleteUser(Long userId) {
		hashOperations.delete(hashReference, userId);

	}

	@Override
	public void saveAllUsers(Map<Long, UserAuthDto> usersMap) {
		hashOperations.putAll(hashReference, usersMap);
	}

}
