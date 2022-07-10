package com.example.cinema.config.jpa.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Time;
import java.time.LocalTime;

@Converter
public class LocalTimeConverter implements AttributeConverter<LocalTime, Time> {
    @Override
    public Time convertToDatabaseColumn(LocalTime localTime) {
        return Time.valueOf(localTime);
    }

    @Override
    public LocalTime convertToEntityAttribute(Time time) {
        return time.toLocalTime();
    }
}
