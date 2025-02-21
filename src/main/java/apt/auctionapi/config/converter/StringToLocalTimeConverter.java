package apt.auctionapi.config.converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@ReadingConverter
@Component
public class StringToLocalTimeConverter implements Converter<String, LocalTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HHmm");

    @Override
    public LocalTime convert(@NonNull String source) {
        if (source.length() != 4 || !source.matches("\\d{4}")) {
            throw new IllegalArgumentException("Invalid time format: " + source + " (expected format: HHmm)");
        }
        return LocalTime.parse(source, FORMATTER);
    }
}
