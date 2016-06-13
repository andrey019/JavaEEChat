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
            } else if (parameter.equalsIgnoreCase("roomall")) {
                //
            } else if (parameter.equalsIgnoreCase("roomonline")) {
                //
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
}
