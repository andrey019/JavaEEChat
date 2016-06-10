import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

public class RegServlet extends HttpServlet {
    private static RegDataCleanUp regDataCleanUp = new RegDataCleanUp();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        byte[] buffer = new byte[req.getContentLength()];
        req.getInputStream().read(buffer);
        process(new String(buffer), resp);
    }

    private void process(String buffer, HttpServletResponse resp) throws IOException {
        String[] incoming = new String(buffer).split(" ");  // login password
        if (incoming != null) {
            if (incoming.length == 2) {
                if (RegData.getUsers().containsKey(incoming[0])) {
                    if (RegData.getUsers().get(incoming[0]).equalsIgnoreCase(incoming[1])) {
                        Random random = new Random();
                        String access = Long.toString(random.nextLong());
                        RegData.getAccessCode().put(incoming[0], access);
                        RegData.getLastActivity().put(incoming[0], System.currentTimeMillis());
                        resp.getWriter().write(access);
                        resp.getWriter().flush();
                        resp.setStatus(200);
                    } else {
                        resp.setStatus(401);    // unauthorized
                    }
                } else {
                    RegData.getUsers().put(incoming[0], incoming[1]);
                    Random random = new Random();
                    String access = Long.toString(random.nextLong());
                    RegData.getAccessCode().put(incoming[0], access);
                    RegData.getLastActivity().put(incoming[0], System.currentTimeMillis());
                    resp.getWriter().write(access);
                    resp.getWriter().flush();
                    resp.setStatus(200);
                }
            } else {
                resp.setStatus(400);
            }
        } else {
            resp.setStatus(400);
        }
    }
}
