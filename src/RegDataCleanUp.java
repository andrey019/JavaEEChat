import java.util.Map;

public class RegDataCleanUp extends Thread {
    public RegDataCleanUp() {
        this.start();
    }

    @Override
    public void run() {
        final long zero = 0;
        while (!isInterrupted()) {
            try {
                if (!RegData.getLastActivity().isEmpty()) {
                    for (Map.Entry<String, Long> entry : RegData.getLastActivity().entrySet()) {
                        if ( (System.currentTimeMillis() - entry.getValue()) > RegData.getMaxTimeOut() ) {
                            RegData.getLastActivity().put(entry.getKey(), zero);
                            RegData.getAccessCode().put(entry.getKey(), "");
                        }
                    }
                }
                Thread.sleep(60000);    // 1 min
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
