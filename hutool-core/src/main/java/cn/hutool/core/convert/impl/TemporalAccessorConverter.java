package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.date.DateUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * JDK8中新加入的java.time包对象解析转换器<br>
 * 支持的对象包括：
 *
 * <pre>
 * java.time.Instant
 * java.time.LocalDateTime
 * java.time.LocalDate
 * java.time.LocalTime
 * java.time.ZonedDateTime
 * java.time.OffsetDateTime
 * java.time.OffsetTime
 * </pre>
 *
 * @author looly
 * @since 5.0.0
 */
public class TemporalAccessorConverter extends AbstractConverter<TemporalAccessor> {
	private static final long serialVersionUID = 1L;

	private Class<?> targetType;
	/**
	 * 日期格式化
	 */
	private String format;

	/**
	 * 构造
	 *
	 * @param targetType 目标类型
	 */
	public TemporalAccessorConverter(Class<?> targetType) {
		this.targetType = targetType;
	}

	/**
	 * 构造
	 *
	 * @param targetType 目标类型
	 * @param format     日期格式
	 */
	public TemporalAccessorConverter(Class<?> targetType, String format) {
		this.targetType = targetType;
		this.format = format;
	}

	/**
	 * 获取日期格式
	 *
	 * @return 设置日期格式
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * 设置日期格式
	 *
	 * @param format 日期格式
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	protected TemporalAccessor convertInternal(Object value) {
		if (value instanceof Long) {
			return parseFromLong((Long) value);
		} else if (value instanceof TemporalAccessor) {
			return parseFromTemporalAccessor((TemporalAccessor) value);
		} else if (value instanceof Date) {
			return parseFromTemporalAccessor(((Date) value).toInstant());
		}else if (value instanceof Calendar) {
			return parseFromTemporalAccessor(((Calendar) value).toInstant());
		} else {
			return parseFromCharSequence(convertToStr(value));
		}
	}

	/**
	 * 通过反射从字符串转java.time中的对象
	 *
	 * @param value 字符串值
	 * @return 日期对象
	 */
	private TemporalAccessor parseFromCharSequence(CharSequence value) {
		final Instant instant;
		if (null != this.format) {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.format);
			instant = formatter.parse(value, Instant::from);
		} else {
			instant = DateUtil.parse(value).toInstant();
		}
		return parseFromTemporalAccessor(instant);
	}

	/**
	 * 将Long型时间戳转换为java.time中的对象
	 *
	 * @param time 时间戳
	 * @return java.time中的对象
	 */
	private TemporalAccessor parseFromLong(Long time) {
		return parseFromTemporalAccessor(Instant.ofEpochMilli(time));
	}

	/**
	 * 将TemporalAccessor型时间戳转换为java.time中的对象
	 *
	 * @param temporalAccessor TemporalAccessor对象
	 * @return java.time中的对象
	 */
	private TemporalAccessor parseFromTemporalAccessor(TemporalAccessor temporalAccessor) {
		return parseFromIntant(Instant.from(temporalAccessor));
	}

	/**
	 * 将TemporalAccessor型时间戳转换为java.time中的对象
	 *
	 * @param instant TemporalAccessor对象
	 * @return java.time中的对象
	 */
	private TemporalAccessor parseFromIntant(Instant instant) {
		if(Instant.class.equals(this.targetType)){
			return instant;
		}else if (LocalDateTime.class.equals(this.targetType)) {
			return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		} else if (LocalDate.class.equals(this.targetType)) {
			return instant.atZone(ZoneId.systemDefault()).toLocalDate();
		} else if (LocalTime.class.equals(this.targetType)) {
			return instant.atZone(ZoneId.systemDefault()).toLocalTime();
		} else if (ZonedDateTime.class.equals(this.targetType)) {
			return instant.atZone(ZoneId.systemDefault());
		} else if (OffsetDateTime.class.equals(this.targetType)) {
			return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
		} else if (OffsetTime.class.equals(this.targetType)) {
			return OffsetTime.ofInstant(instant, ZoneId.systemDefault());
		}
		return null;
	}
}
