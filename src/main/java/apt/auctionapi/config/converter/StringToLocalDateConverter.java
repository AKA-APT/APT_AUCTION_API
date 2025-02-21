package apt.auctionapi.config.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * 📌 MongoDB String 날짜값 ("20240509") ↔ LocalDate 변환 컨버터
 */
@ReadingConverter
@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.BASIC_ISO_DATE; // "yyyyMMdd"

    @Override
    public LocalDate convert(@NonNull String source) {
        return LocalDate.parse(source, FORMATTER);
    }
}