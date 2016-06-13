import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class RoomRegServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        byte[] buffer = new byte[req.getContentLength()];
        req.getInputStream().read(buffer);
        process(new String(buffer), resp);
    }

    private void process(String buffer, HttpServletResponse resp) throws IOException {
        String[] roomNameAndUsers = new String(buffer).split(" ");
        if ( (roomNameAndUsers != null) && (roomNameAndUsers.length > 1) ) {
            if (!RegData.getRooms().containsKey(roomNameAndUsers[0]) && (!RegData.getUsers().containsKey(roomNameAndUsers[0]))) {
                ArrayList<String> users = new ArrayList<>();
                for (int i = 1; i < roomNameAndUsers.length; i++) {
                    if (RegData.getUsers().containsKey(roomNameAndUsers[i])) {
                        users.add(roomNameAndUsers[i]);
                    }
                }
                RegData.getRooms().put(roomNameAndUsers[0], users);
            } else {
                resp.setStatus(401);
            }
        } else {
            resp.setStatus(400);
        }
    }
}
