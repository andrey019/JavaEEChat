import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String parameter = req.getParameter("status");
        if ( (parameter != null) && (!parameter.equalsIgnoreCase("")) ) {
            if (parameter.equalsIgnoreCase("usersall")) {
                sendAllUsers(resp);
            } else if (parameter.equalsIgnoreCase("usersonline")) {
                sendOnlineUsers(resp);
            } else if (parameter.contains("roomall")) {
                sendRoomUsers(resp, parameter, false);
            } else if (parameter.contains("roomonline")) {
                sendRoomUsers(resp, parameter, true);
            } else {
                sendUserStatus(resp, parameter);
            }
        }
    }

    private void sendAllUsers(HttpServletResponse resp) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String user : RegData.getUsers().keySet()) {
            stringBuilder.append(user);
            stringBuilder.append(" ");
        }
        resp.getWriter().write(stringBuilder.toString());
        resp.getWriter().flush();
    }

    private void sendOnlineUsers(HttpServletResponse resp) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String user : RegData.getLastActivity().keySet()) {
            stringBuilder.append(user);
            stringBuilder.append(" ");
        }
        resp.getWriter().write(stringBuilder.toString());
        resp.getWriter().flush();
    }

    private void sendUserStatus(HttpServletResponse resp, String user) throws IOException {
        if (RegData.getUsers().containsKey(user)) {
            if (RegData.getAccessCode().containsKey(user)) {
                resp.getWriter().write(user + "=online");
                resp.getWriter().flush();
            } else {
                resp.getWriter().write(user + "=offline");
                resp.getWriter().flush();
            }
        } else {
            resp.setStatus(400);
        }
    }

    private void sendRoomUsers(HttpServletResponse resp, String roomRequest, boolean onlyOnline) throws IOException {
        String[] roomName = roomRequest.split("@");
        if (roomName.length != 2) {
            resp.setStatus(400);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (RegData.getRooms().containsKey(roomName[1])) {
            if (onlyOnline) {
                for (String user : RegData.getRooms().get(roomName[1])) {
                    if (RegData.getLastActivity().containsKey(user)) {
                        stringBuilder.append(user);
                        stringBuilder.append(" ");
                    }
                }
            } else {
                for (String user : RegData.getRooms().get(roomName[1])) {
                    stringBuilder.append(user);
                    stringBuilder.append(" ");
                }
            }
            if (stringBuilder.length() < 1) {
                stringBuilder.append("empty");
            }
            resp.getWriter().write(stringBuilder.toString());
            resp.getWriter().flush();
        } else {
            resp.setStatus(400);
        }
    }
}
