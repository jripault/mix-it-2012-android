package fr.mixit.android.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import fr.mixit.android.utils.Lists;
import android.content.ContentResolver;
import android.content.Context;

public class JsonExecutor {

	final ContentResolver resolver;

	public JsonExecutor(ContentResolver contentResolver) {
		resolver = contentResolver;
	}

	public void execute(Context context, String assetName, JsonHandler handler) throws JsonHandler.JsonHandlerException {
		try {
			final InputStream input = context.getAssets().open(assetName);
			byte[] buffer = new byte[input.available()];
			while (input.read(buffer) != -1)
				;
			String jsontext = new String(buffer);
			execute(jsontext, handler);
		} catch (JsonHandler.JsonHandlerException e) {
			throw e;
		} catch (IOException e) {
			throw new JsonHandler.JsonHandlerException("Problem parsing local asset: " + assetName, e);
		}
	}

	public void execute(String jsonText, JsonHandler handler) throws JsonHandler.JsonHandlerException {
		try {
			final ArrayList<JSONArray> entries = Lists.newArrayList();
	        JSONArray requestEntries = new JSONArray(jsonText);
	        entries.add(requestEntries);
			handler.parseAndApply(entries, resolver);
		} catch (JsonHandler.JsonHandlerException e) {
			throw e;
		} catch (JSONException e) {
			throw new JsonHandler.JsonHandlerException("Problem parsing jsonText :" /*+ jsonText*/, e);
		}
	}

}
