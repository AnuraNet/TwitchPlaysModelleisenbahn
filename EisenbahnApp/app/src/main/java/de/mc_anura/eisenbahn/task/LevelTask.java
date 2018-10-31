package de.mc_anura.eisenbahn.task;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.mc_anura.eisenbahn.Callback;
import de.mc_anura.eisenbahn.DataContainer;

public class LevelTask extends AsyncTask<DataContainer, Void, String> {

    private final Callback<String> callback;

    public LevelTask(Callback<String> cb) {
        this.callback = cb;
    }

    @Override
    protected String doInBackground(DataContainer... params) {
        DataContainer dc = params[0];
        String status;
        try {
            URL url = new URL("http://Eisenbahn/level");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Eisenbahn_App");
            con.setRequestProperty("Accept-Language", "de-DE,de;q=0.5");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            Integer value = dc.getValue();
            wr.writeBytes(dc.getName() + "\n" + value);
            wr.flush();
            wr.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            status = in.readLine();
            if (status == null) {
                status = "error:readError";
            } else if (!status.equals("ok")) {
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
