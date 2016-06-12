import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogOutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] loginAccess = getLoginAccess(req);
        if (RegData.getAccessCode().containsKey(loginAccess[0])) {
            if (RegData.getAccessCode().get(loginAccess[0]).equalsIgnoreCase(loginAccess[1])) {
                RegData.getAccessCode().remove(loginAccess[0]);
                RegData.getLastActivity().remove(loginAccess[0]);
            } else {
                resp.setStatus(401);
            }
        } else {
            resp.setStatus(401);
        }
    }

    private String[] getLoginAccess(HttpServletRequest req) {
        try {
            byte[] reqBytes = new byte[req.getContentLength()];
            req.getInputStream().read(reqBytes);
            String[] loginAccess = new String(reqBytes).split(" ");
            if (loginAccess.length == 2) {
                return loginAccess;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
