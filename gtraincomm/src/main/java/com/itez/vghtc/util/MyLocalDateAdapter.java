package com.itez.vghtc.util;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MyLocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
	@Override
    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @Override
    public LocalDate deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        String strDate = element.getAsJsonPrimitive().getAsString().trim();
        if(strDate.isEmpty()) return null;
        //
        try{
        	return LocalDate.parse(strDate, DateTimeFormatter.ISO_LOCAL_DATE);
        }catch(Exception _ex) {
        }
        //
        if(strDate.contains("-")) {
        	return LocalDate.parse(strDate);
        }else if(strDate.contains("/")) {
        	String[] ss = strDate.split("/");
        	if(ss.length == 3) {
        		return LocalDate.of(Integer.parseInt(ss[0].trim()), Integer.parseInt(ss[1].trim()), Integer.parseInt(ss[2].trim()));
        	}else {
        		return null;
        	}
        }else if(strDate.length() == 8) {
        	return LocalDate.of(Integer.parseInt(strDate.substring(0, 4)),
					Integer.parseInt(strDate.substring(4, 6)),
					Integer.parseInt(strDate.substring(6)));
        }else if(strDate.length() == 7) {
        	return LocalDate.of(Integer.parseInt(strDate.substring(0, 3)),
        						Integer.parseInt(strDate.substring(3, 5)),
        						Integer.parseInt(strDate.substring(5)));
        }else if(strDate.length() == 6) {
        	return LocalDate.of(Integer.parseInt(strDate.substring(0, 2)),
					Integer.parseInt(strDate.substring(2, 4)),
					Integer.parseInt(strDate.substring(4)));
        }
        return null;        
    }
}
