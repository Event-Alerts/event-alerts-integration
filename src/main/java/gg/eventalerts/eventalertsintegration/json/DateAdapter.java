package gg.eventalerts.eventalertsintegration.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;


public class DateAdapter extends TypeAdapter<Date> {
    @Override
    public void write(@NotNull JsonWriter out, @NotNull Date value) throws IOException {
        out.value(String.valueOf(value.getTime()));
    }

    @Override
    public Date read(@NotNull JsonReader in) throws IOException {
        return new Date(Long.parseLong(in.nextString()));
    }
}