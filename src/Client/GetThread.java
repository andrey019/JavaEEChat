package Client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class GetThread extends Thread {
    private int n;
    private String login;
    private String access;

    public void setLogin(String login) {
        this.login = login;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                URL url = new URL("http://localhost:8080/get");
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setDoOutput(true);
                sendRequest(http);
                Message[] messages = getResponse(http);
                if (messages != null) {
                    for (Message message : messages) {
                        System.out.println(message);
                        n = message.getNumber() + 1;
                    }
                }
                Thread.sleep(500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    private void sendRequest(HttpURLConnection http) throws IOException {
        String request = (n + " " + login + " " + access);
        http.getOutputStream().write(request.getBytes());
        http.getOutputStream().flush();
    }

    private Message[] getResponse(HttpURLConnection http) throws IOException {
        if (http.getResponseCode() == 200) {
            InputStream is = http.getInputStream();
            int sz = is.available();
            if (sz > 0) {
                byte[] buf = new byte[is.available()];
                is.read(buf);
                Gson gson = new GsonBuilder().create();
                Message[] list = gson.fromJson(new String(buf), Message[].class);
                return list;
            }
        }
        return null;
    }
}