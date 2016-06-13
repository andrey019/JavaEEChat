package Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	private static String access = "";
	private static String login = "";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
            instructionsOnScreen();
			while (!registration(scanner)) {}
			System.out.println("Access granted");

			GetThread th = new GetThread();
            th.setLogin(login);
            th.setAccess(access);
			th.setDaemon(true);
			th.start();

            startChatting(scanner);
		} finally {
			scanner.close();
		}
	}

    private static void instructionsOnScreen() {
        System.out.println("Instructions:");
        System.out.println("To send public message just type in your text");
        System.out.println("To send private message use this example - username@@@text");
        System.out.println("To get list of all users type in urersall@@@");
        System.out.println("To get list of online users type in usersonline@@@");
        System.out.println("To get user status use this example - status@@@username");
        System.out.println("To create chatroom use this example - chatroom@@@roomname username1 username2 username3...");
        System.out.println("To get list of chatroom users type in roomall@@@roomname");
        System.out.println("To get list of online chatroom users type in roomonline@@@roomname");
        System.out.println("To logout just hit enter button");
        System.out.println();
    }

    private static void startChatting(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                logOut();
            }

            Message message = processInput(input);
            if (message != null) {
                try {
                    int res = message.send("http://localhost:8080/add");
                    if (res == 401) {
                        System.out.println("Access denied!");
                    } else if (res != 200) {
                        System.out.println("HTTP error: " + res);
                        return;
                    }
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    return;
                }
            }
        }
    }

    private static Message processInput(String input) {
        if (input.contains("@@@")) {
            String[] splittedInput = input.split("@@@");
            switch (splittedInput[0]) {
                case "chatroom":
                    caseChatroom(splittedInput);
                    break;
                case "status":
                    caseStatus(splittedInput);
                    break;
                case "usersall":
                    getUsersStatus(splittedInput[0]);
                    break;
                case "usersonline":
                    getUsersStatus(splittedInput[0]);
                    break;
                case "roomall":
                    caseRoomAllOrOnline(splittedInput);
                    break;
                case "roomonline":
                    caseRoomAllOrOnline(splittedInput);
                    break;
                default:
                    if (splittedInput.length == 2) {
                        return privateMessage(splittedInput[0], splittedInput[1]);
                    } else {
                        System.out.println("Message is empty!");
                    }
                    break;
            }
        } else {
            return publicMessage(input);
        }
        return null;
    }

    private static void caseChatroom(String[] splittedInput) {
        if (splittedInput.length == 2) {
            chatRoomRegistration(splittedInput[1]);
        } else {
            System.out.println("Wrong command!");
        }
    }

    private static void caseStatus(String[] splittedInput) {
        if (splittedInput.length == 2) {
            getUsersStatus(splittedInput[1]);
        } else {
            System.out.println("Wrong command!");
        }
    }

    private static void caseRoomAllOrOnline(String[] splittedInput) {
        if (splittedInput.length == 2) {
            getUsersStatus(splittedInput[0] + "@" + splittedInput[1]);
        } else {
            System.out.println("Wrong command!");
        }
    }

    private static void chatRoomRegistration(String roomName) {
        String[] roomNameUsers = roomName.split(" ");
        StringBuilder roomNameAndUsers = new StringBuilder();
        ArrayList<String> users = new ArrayList<>();
        if ( (roomNameUsers != null) && (roomNameUsers.length > 1) ) {
            for (int i = 0; i < roomNameUsers.length; i++) {
                users.add(roomNameUsers[i]);
                roomNameAndUsers.append(roomNameUsers[i]);
                roomNameAndUsers.append(" ");
            }
            if (!users.contains(login)) {
                roomNameAndUsers.append(login);
            }
        }
        if (roomRegSend(roomNameAndUsers)) {
            System.out.println("Chatroom '" + roomNameUsers[0] + "' is created");
        }
    }

    private static boolean roomRegSend(StringBuilder roomNameAndUsers) {
        try {
            URL obj = new URL("http://localhost:8080/regroom");
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setDoOutput(true);

            conn.getOutputStream().write(roomNameAndUsers.toString().getBytes());
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
            if (conn.getResponseCode() == 200) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void getUsersStatus(String parameter) {
        try {
            URL obj = new URL("http://localhost:8080/status?status=" + parameter);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            if (conn.getResponseCode() == 200) {
                byte[] respBytes = new byte[conn.getInputStream().available()];
                conn.getInputStream().read(respBytes);
                conn.getInputStream().close();
                System.out.println(new String(respBytes));
            } else {
                System.out.println("Http error: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Message publicMessage(String text) {
        Message message = new Message();
        message.setFrom(login);
        message.setTo("all");
        message.setAccess(access);
        message.setText(text);
        return message;
    }

    private static Message privateMessage(String to, String text) {
        Message message = new Message();
        message.setFrom(login);
        message.setTo(to);
        message.setAccess(access);
        message.setText(text);
        return message;
    }

    private static void logOut() {
        try {
            URL obj = new URL("http://localhost:8080/logout");
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setDoOutput(true);
            conn.getOutputStream().write((login + " " + access).getBytes());
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
            if (conn.getResponseCode() == 200) {
                System.out.println("Logged out successfully!");
            } else {
                System.out.println("Logout error: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

	private static boolean registration(Scanner scanner) {
		try {
			System.out.println("Enter your login and password, putting a space between them (login pass)");
			String input = "";
			while (input.isEmpty()) {
				input = scanner.nextLine();
                if (input.contains("@")) {
                    System.out.println("You can't use '@' sign!");
                    input = "";
                } else if (input.equalsIgnoreCase("chatroom") || input.equalsIgnoreCase("status") ||
                        input.equalsIgnoreCase("usersall") || input.equalsIgnoreCase("usersonline")) {
                    System.out.println("This name is reserved by system!");
                    input = "";
                }
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
                is.close();
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
