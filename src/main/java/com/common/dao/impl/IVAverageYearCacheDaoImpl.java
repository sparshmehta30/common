package com.common.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import com.common.dao.IVAverageYearCacheDao;
import com.common.dto.StockIVAverageDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IVAverageYearCacheDaoImpl implements IVAverageYearCacheDao {

	private final String hashReference = "iv-average-by-year";

	@Resource(name = "ivAverageByYearRedisTemplate")
	private HashOperations<String, String, List<StockIVAverageDto>> hashOperations;

	@Override
	public void saveAllIVAverageByYear(Map<String, List<StockIVAverageDto>> stockIVMap) {
		hashOperations.putAll(hashReference, stockIVMap);
	}

	@Override
	public void saveIVAverageByYear(List<StockIVAverageDto> ivAverageByYearList) {
		hashOperations.putIfAbsent(hashReference, ivAverageByYearList.get(0).getYear(), ivAverageByYearList);
	}

	@Override
	public List<StockIVAverageDto> getIVAveragesByYear(String year) {
		return hashOperations.get(hashReference, year);
	}

	@Override
	public void updateIVAverageByYear(List<StockIVAverageDto> ivAverageByYearList) {
		hashOperations.put(hashReference, ivAverageByYearList.get(0).getYear(), ivAverageByYearList);
	}

	@Override
	public Map<String, List<StockIVAverageDto>> getAllIVAverageByYear() {
		return hashOperations.entries(hashReference);
	}

	@Override
	public void deleteIVAverageByYear(String year) {
		hashOperations.delete(hashReference, year);

	}

}
