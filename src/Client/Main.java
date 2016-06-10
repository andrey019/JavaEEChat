package Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class GetThread extends Thread {
	private int n;

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				URL url = new URL("http://localhost:8080/get?from=" + n);
				HttpURLConnection http = (HttpURLConnection) url.openConnection();

				InputStream is = http.getInputStream();
				try {
					int sz = is.available();
					if (sz > 0) {
						byte[] buf = new byte[is.available()];
						is.read(buf);

						Gson gson = new GsonBuilder().create();
						Message[] list = gson.fromJson(new String(buf), Message[].class);

						for (Message m : list) {
							System.out.println(m);
							n++;
						}
					}
				} finally {
					is.close();
				}
				Thread.sleep(100);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
}

public class Main {
	private static String access = "";
	private static String login = "";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
			while (!registration(scanner)) {}
			System.out.println("Access granted");
	
			GetThread th = new GetThread();
			th.setDaemon(true);
			th.start();

			while (true) {
				String text = scanner.nextLine();
				if (text.isEmpty())
					break;

				Message m = new Message();
				m.setText(text);
				m.setFrom(login);
				m.setAccess(access);
				m.setTo("all");

				try {
					int res = m.send("http://localhost:8080/add");
					if (res != 200) {
						System.out.println("HTTP error: " + res);
						return;
					}
				} catch (IOException ex) {
					System.out.println("Error: " + ex.getMessage());
					return;
				}
			}
		} finally {
			scanner.close();
		}
	}

	private static boolean registration(Scanner scanner) {
		try {
			System.out.println("Enter your login and password, putting a space between them (login pass)");
			String input = "";
			while (input.isEmpty()) {
				input = scanner.nextLine();
			}
			URL obj = new URL("http://localhost:8080/reg");
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

			if (regSend(input, conn) && regReceive(conn)) {
				String[] loginPass = input.split(" ");
				login = loginPass[0];
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean regSend(String input, HttpURLConnection conn) {
		try {
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			os.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean regReceive(HttpURLConnection conn) {
		try {
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				while (is.available() < 1) {
					Thread.sleep(100);
				}
				byte[] buffer = new byte[is.available()];
				is.read(buffer);
				access = new String(buffer);
				return true;
			} else {
				System.out.println("Http error " + conn.getResponseCode());
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
