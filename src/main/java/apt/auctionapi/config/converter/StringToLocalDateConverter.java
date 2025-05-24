package apt.auctionapi.config.converter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * MongoDB String 날짜값 ↔ LocalDate 변환 컨버터
 * 지원 포맷:
 *   - "yyyyMMdd"        (BASIC_ISO_DATE)
 *   - "yyyy-MM-dd"      (ISO_LOCAL_DATE)
 *   - "yyyy-MM-dd'T'HH:mm:ssXXX" (ISO_OFFSET_DATE_TIME)
 */
@ReadingConverter
@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    private static final DateTimeFormatter BASIC_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;           // "yyyyMMdd"
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;          // "yyyy-MM-dd"
    private static final DateTimeFormatter ISO_OFFSET_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME; // "yyyy-MM-dd'T'HH:mm:ssXXX"

    @Override
    public LocalDate convert(@NonNull String source) {
        // 1) "yyyyMMdd"
        try {
            return LocalDate.parse(source, BASIC_FORMATTER);
        } catch (DateTimeParseException ignored) {
        }

        // 2) "yyyy-MM-dd"
        try {
            return LocalDate.parse(source, ISO_DATE_FORMATTER);
        } catch (DateTimeParseException ignored) {
        }

        // 3) "2021-01-29T00:00:00+00:00" 같은 ISO_OFFSET_DATE_TIME
        try {
            return OffsetDateTime.parse(source, ISO_OFFSET_FORMATTER).toLocalDate();
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                "Cannot convert String to LocalDate: unsupported format '" + source + "'", ex
            );
        }
    }
}
