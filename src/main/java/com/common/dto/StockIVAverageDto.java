package com.common.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class StockIVAverageDto  implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Long id;

	private String stockSymbol;

	private String instrumentToken;

	private String year;

	private Double average;
	
	

}
