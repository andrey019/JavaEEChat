
import Client.Message;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddServlet extends HttpServlet {

	private MessageList msgList = MessageList.getInstance();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		InputStream is = req.getInputStream();
        byte[] buf = new byte[req.getContentLength()];
        is.read(buf);
		Message msg = Message.fromJSON(new String(buf));
        process(msg, resp);
	}

	private void process(Message msg, HttpServletResponse resp) {
        if (msg != null) {
            if (RegData.getAccessCode().containsKey(msg.getFrom())) {
                if (RegData.getAccessCode().get(msg.getFrom()).equalsIgnoreCase(msg.getAccess())) {
                    if (RegData.getRooms().containsKey(msg.getTo())) {
                        if (RegData.getRooms().get(msg.getTo()).contains(msg.getFrom())) {
                            addMessage(msg);
                        } else {
                            resp.setStatus(401);
                        }
                    } else {
                        if (RegData.getUsers().containsKey(msg.getTo()) || msg.getTo().equalsIgnoreCase("all")) {
                            addMessage(msg);
                        }
                    }
                } else {
                    resp.setStatus(401);
                }
            } else {
                resp.setStatus(401);
            }
        } else {
            resp.setStatus(400); // Bad request
        }
    }

    private void addMessage(Message msg) {
        msg.setAccess("");
        msg.setNumber(msgList.getSize());
        msgList.add(msg);
        RegData.getLastActivity().put(msg.getFrom(), System.currentTimeMillis());
    }
}
