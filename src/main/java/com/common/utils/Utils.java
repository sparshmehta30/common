package com.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

	public static boolean checkCollectionIsNotEmpty(Collection<?> collection) {
		return Boolean.FALSE.equals(CollectionUtils.isEmpty(collection));
	}

	public static boolean checkCollectionIsNotEmpty(Map<?, ?> collection) {
		return Boolean.FALSE.equals(CollectionUtils.isEmpty(collection));
	}

	public static boolean checkCollectionIsEmpty(Collection<?> collection) {
		return CollectionUtils.isEmpty(collection);
	}

	public static boolean checkCollectionIsEmpty(Map<?, ?> collection) {
		return CollectionUtils.isEmpty(collection);
	}

	public static void copyProperties(Object source, Object target) {
		BeanUtils.copyProperties(source, target);
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String toJsonString(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}"; // Return empty JSON object in case of an error
		}
	}

	public static <T> T convertJsonToDto(String json, Class<T> dtoClass) {
		try {
			return objectMapper.readValue(json, dtoClass);
		} catch (Exception e) {
			throw new RuntimeException("Failed to convert JSON to DTO: " + e.getMessage(), e);
		}
	}

	public static String displayErrorForWeb(StackTraceElement[] t) {
		if (t == null)
			return "null";

		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : t) {
			sb.append(element.toString());
			sb.append("<br/>");
		}
		return sb.toString();
	}

	public static String generateOtp(int n) {
		Random random = new Random();
		StringBuilder otp = new StringBuilder();

		for (int i = 0; i < n; i++) {
			int digit = random.nextInt(10);
			otp.append(digit);
		}

		return String.format("%0" + n + "d", Long.parseLong(otp.toString()));
	}

	public static Date convertUnixToDate(long unixTimestamp) {
		return new Date(TimeUnit.SECONDS.toMillis(unixTimestamp)); // Convert seconds → milliseconds
	}

	public static Date addDaysToDate(Date date, int daysToAdd) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
		return calendar.getTime();
	}

	public static String formateDate(Date date, String formate) {
		SimpleDateFormat formater = new SimpleDateFormat(formate);
		return formater.format(date);
	}

	public static Date toDate(LocalDateTime dateTime) {
		return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static String getCurrentYear() {
		return String.valueOf(Year.now().getValue());
	}

	public static List<String> getLastNYears(int n) {
		int currentYear = Year.now().getValue();
		List<String> years = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			years.add(String.valueOf(currentYear - i));
		}
		return years;
	}

	public static double formatTwoDecimal(double value) {
		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
		return sourceList.parallelStream().map(source -> {
			try {
				T target = targetClass.getDeclaredConstructor().newInstance();
				BeanUtils.copyProperties(source, target);
				return target;
			} catch (Exception e) {
				throw new RuntimeException("Copy failed", e);
			}
		}).collect(Collectors.toList());
	}

}
