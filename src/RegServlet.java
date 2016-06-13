import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

public class RegServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        byte[] buffer = new byte[req.getContentLength()];
        req.getInputStream().read(buffer);
        process(new String(buffer), resp);
    }

    private void process(String buffer, HttpServletResponse resp) throws IOException {
        String[] loginPassword = new String(buffer).split(" ");
        if (loginPassword != null) {
            if (loginPassword.length == 2) {
                if (RegData.getUsers().containsKey(loginPassword[0])) {
                    if (RegData.getUsers().get(loginPassword[0]).equalsIgnoreCase(loginPassword[1])) {
                        authorization(loginPassword, resp);
                    } else {
                        resp.setStatus(401);    // unauthorized
                    }
                } else if (!loginPassword[0].equalsIgnoreCase("usersall") &&
                        !loginPassword[0].equalsIgnoreCase("usersonline") &&
                        !RegData.getRooms().containsKey(loginPassword[0])) {
                    registration(loginPassword, resp);
                } else {
                    resp.setStatus(400);
                }
            } else {
                resp.setStatus(400);
            }
        } else {
            resp.setStatus(400);
        }
    }
    
    private void authorization(String[] loginPassword, HttpServletResponse resp) throws IOException {
        Random random = new Random();
        String access = Long.toString(random.nextLong());
        RegData.getAccessCode().put(loginPassword[0], access);
        RegData.getLastActivity().put(loginPassword[0], System.currentTimeMillis());
        resp.getWriter().write(access);
        resp.getWriter().flush();
    }
    
    private void registration(String[] loginPassword, HttpServletResponse resp) throws IOException {
        RegData.getUsers().put(loginPassword[0], loginPassword[1]);
        Random random = new Random();
        String access = Long.toString(random.nextLong());
        RegData.getAccessCode().put(loginPassword[0], access);
        RegData.getLastActivity().put(loginPassword[0], System.currentTimeMillis());
        resp.getWriter().write(access);
        resp.getWriter().flush();
    }
}
