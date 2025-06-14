package com.common.dao;

import java.util.Map;

import com.common.dto.StockIVAverageDto;

public interface IVAverageYearAndStockCacheDao {

	void saveIVAverageByYearAndStock(StockIVAverageDto ivAverageByYearAndStock);

	StockIVAverageDto getIVAverageByYearAndStock(String yearAndStock);

	void updateIVAverageByYearAndStock(StockIVAverageDto ivAverageByYearAndStock);

	Map<String, StockIVAverageDto> getAllIVAverageByYearAndStock();

	void deleteIVAverageByYearAndStock(String yearAndStock);

	void saveAllIVAverageByYearAndStock(Map<String, StockIVAverageDto> ivAverageByYearAndStockMap);

}
