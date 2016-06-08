package Client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date date = new Date();
	private String from;
	private String to;
	private String text;
	
	public String toJSON() {
        Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}
	
	public static Message fromJSON(String s) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(s, Message.class);
	}

    public static Message deserialize(InputStream is) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(is);
            return (Message) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	@Override
	public String toString() {
		return new StringBuilder().append("[").append(date.toString())
				.append(", From: ").append(from).append(", To: ").append(to)
				.append("] ").append(text).toString();
	}

	public int send(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
	
//		OutputStream os = conn.getOutputStream();
        //ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
        PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
		try {
			String json = toJSON();
//			os.write(json.getBytes());
//            os.flush();

			printWriter.write(json);
            printWriter.flush();
            printWriter.close();


//            objectOutputStream.writeObject(this);
//            objectOutputStream.flush();

			
			return conn.getResponseCode();
		} finally {
			//os.close();
            //objectOutputStream.close();
		}
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
