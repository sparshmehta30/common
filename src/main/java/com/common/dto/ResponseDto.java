/*
 * @author Sparsh Mehta
 * @since 2021
 * @version 1.0
 */
package com.common.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * The Class ResponseDto.
 */
@Data
public class ResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The entity id. */
	private String entityId;

	/** The error message. */
	private List<String> errorMessage;

	/** The row number. */
	private int rowNumber;

	@Override
	public String toString() {
		return "ResponseDto [entityId=" + entityId + ", errorMessage=" + errorMessage + ", rowNumber=" + rowNumber
				+ "]";
	}

}
