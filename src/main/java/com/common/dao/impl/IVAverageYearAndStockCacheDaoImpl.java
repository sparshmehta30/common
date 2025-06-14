package com.common.dao.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import com.common.dao.IVAverageYearAndStockCacheDao;
import com.common.dto.StockIVAverageDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IVAverageYearAndStockCacheDaoImpl implements IVAverageYearAndStockCacheDao {

	private final String hashReference = "iv-average-by-year-and-stock";

	@Resource(name = "ivAverageByYearAndStockRedisTemplate")
	private HashOperations<String, String, StockIVAverageDto> hashOperations;

	@Override
	public void saveAllIVAverageByYearAndStock(Map<String, StockIVAverageDto> ivAverageByYearAndStock) {
		hashOperations.putAll(hashReference, ivAverageByYearAndStock);
	}

	@Override
	public void saveIVAverageByYearAndStock(StockIVAverageDto ivAverageByYearAndStock) {
		hashOperations.putIfAbsent(hashReference,
				ivAverageByYearAndStock.getStockSymbol() + "|" + ivAverageByYearAndStock.getYear(),
				ivAverageByYearAndStock);
	}

	@Override
	public StockIVAverageDto getIVAverageByYearAndStock(String yearAndStock) {
		return hashOperations.get(hashReference, yearAndStock);
	}

	@Override
	public void updateIVAverageByYearAndStock(StockIVAverageDto ivAverageByYearAndStock) {
		hashOperations.put(hashReference,
				ivAverageByYearAndStock.getStockSymbol() + "|" + ivAverageByYearAndStock.getYear(),
				ivAverageByYearAndStock);
	}

	@Override
	public Map<String, StockIVAverageDto> getAllIVAverageByYearAndStock() {
		return hashOperations.entries(hashReference);
	}

	@Override
	public void deleteIVAverageByYearAndStock(String yearAndStock) {
		hashOperations.delete(hashReference, yearAndStock);

	}

}
