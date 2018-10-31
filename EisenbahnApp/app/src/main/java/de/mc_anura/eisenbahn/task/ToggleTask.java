package de.mc_anura.eisenbahn.task;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.mc_anura.eisenbahn.Callback;

public class ToggleTask extends AsyncTask<String, Void, String> {

    private final Callback<String> callback;

    public ToggleTask(Callback<String> cb) {
        this.callback = cb;
    }

    @Override
    protected String doInBackground(String... params) {
        String status;
        try {
            URL url = new URL("http://Eisenbahn/toggle");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Eisenbahn_App");
            con.setRequestProperty("Accept-Language", "de-DE,de;q=0.5");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(params[0]);
            wr.flush();
            wr.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            status = in.readLine();
            if (status == null) {
                status = "error:readError";
            } else if (status.equals("ok")) {
                String inputLine = in.readLine();
                if (inputLine == null) {
                    status = "error:protocolMismatch";
                } else {
                    status = inputLine;
                }
            } else {
                status = "error:" + status;
            }
            in.close();

        } catch (IOException e) {
            status = "error:IOException";
            e.printStackTrace();
        }
        return status;
    }

    @Override
    protected void onPostExecute(String result) {
        callback.callback(result);
    }
}
