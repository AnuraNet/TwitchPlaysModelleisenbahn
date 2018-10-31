package de.mc_anura.eisenbahn.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import de.mc_anura.eisenbahn.Callback;
import de.mc_anura.eisenbahn.DataContainer;
import de.mc_anura.eisenbahn.DataContainer.DataType;

public class StatesTask extends AsyncTask<Void, Void, Map<String, DataContainer>> {

    private final Callback<Map<String, DataContainer>> callback;

    public StatesTask(Callback<Map<String, DataContainer>> cb) {
        this.callback = cb;
    }

    @Override
    protected Map<String, DataContainer> doInBackground(Void... params) {
        Map<String, DataContainer> states = new LinkedHashMap<>();
        try {
            URL url = new URL("http://Eisenbahn/state");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Eisenbahn_App");
            con.setRequestProperty("Accept-Language", "de-DE,de;q=0.5");
            con.setDoOutput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            in.lines().map((s) -> s.split(":"))
                    .forEach((s) -> {
                        DataType dataType = DataType.getDataTypeById(Integer.parseInt(s[1]));
                        DataContainer dc;
                        switch (dataType) {
                            case BOOLEAN:
                                dc = new DataContainer(s[0], Boolean.valueOf(s[2]), dataType);
                                break;
                            case INT:
                                dc = new DataContainer(s[0], Integer.valueOf(s[2]), dataType);
                                break;
                            default:
                                return;
                        }
                        states.put(s[0], dc);
                    });
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return states;
    }

    @Override
    protected void onPostExecute(Map<String, DataContainer> result) {
        callback.callback(result);
    }
}
