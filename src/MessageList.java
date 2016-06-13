import java.util.ArrayList;
import Client.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MessageList {
	
	private static final MessageList msgList = new MessageList();

	private final ArrayList<Message> list = new ArrayList<>();

	public static MessageList getInstance() {
		return msgList;
	}
  
  	private MessageList() {}
	
	public synchronized void add(Message m) {
		list.add(m);
	}

    public synchronized int getSize() {
        return list.size();
    }
	
	public synchronized String toJSON(int n, String to) {
		ArrayList<Message> res = new ArrayList<>();
		for (int i = n; i < list.size(); i++) {
			if ( list.get(i).getTo().equalsIgnoreCase(to) || list.get(i).getTo().equalsIgnoreCase("all") ||
                    list.get(i).getFrom().equalsIgnoreCase(to) || belongsToRoom(list.get(i), to)) {
                res.add(list.get(i));
            }
		}

		if (res.size() > 0) {
			Gson gson = new GsonBuilder().create();
			return gson.toJson(res.toArray());
		} else
			return null;
	}

	private boolean belongsToRoom(Message message, String to) {
        if (RegData.getRooms().containsKey(message.getTo())) {
            if (RegData.getRooms().get(message.getTo()).contains(to)) {
                return true;
            }
        }
        return false;
    }
}
