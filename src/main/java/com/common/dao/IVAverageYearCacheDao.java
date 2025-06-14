package com.common.dao;

import java.util.List;
import java.util.Map;

import com.common.dto.StockIVAverageDto;

public interface IVAverageYearCacheDao {

	void saveIVAverageByYear(List<StockIVAverageDto> ivAverageByYearList);

	List<StockIVAverageDto> getIVAveragesByYear(String year);

	void updateIVAverageByYear(List<StockIVAverageDto> ivAverageByYearList);

	Map<String, List<StockIVAverageDto>> getAllIVAverageByYear();

	void deleteIVAverageByYear(String year);

	void saveAllIVAverageByYear(Map<String, List<StockIVAverageDto>> ivAverageByYearMap);

}
