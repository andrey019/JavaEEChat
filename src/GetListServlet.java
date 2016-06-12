
import Client.Message;
import com.sun.org.apache.regexp.internal.RE;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetListServlet extends HttpServlet {
	
	private MessageList msgList = MessageList.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String[] numberLoginAccess = getNumberLoginAccess(req);
            if (checkAccess(numberLoginAccess)) {
                String json = msgList.toJSON(Integer.valueOf(numberLoginAccess[0]), numberLoginAccess[1]);
                if (json != null) {
                    OutputStream os = resp.getOutputStream();
                    os.write(json.getBytes());
                }
            } else {
                resp.setStatus(401);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(400);
        }
    }

    private String[] getNumberLoginAccess(HttpServletRequest req) {
        try {
            byte[] reqBytes = new byte[req.getContentLength()];
            req.getInputStream().read(reqBytes);
            String[] numberLoginAccess = new String(reqBytes).split(" ");
            if (numberLoginAccess.length == 3) {
                return numberLoginAccess;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkAccess(String[] numberLoginAccess) {
        try {
            if (RegData.getAccessCode().containsKey(numberLoginAccess[1])) {
                if (RegData.getAccessCode().get(numberLoginAccess[1]).equalsIgnoreCase(numberLoginAccess[2])) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
