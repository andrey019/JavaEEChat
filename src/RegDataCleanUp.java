import java.util.Map;

public final class RegDataCleanUp extends Thread {

    private static final long MAX_TIME_OUT = 1800000;   // 30 min

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
                        if ( (System.currentTimeMillis() - entry.getValue()) > MAX_TIME_OUT ) {
                            RegData.getLastActivity().remove(entry.getKey());
                            RegData.getAccessCode().remove(entry.getKey());
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
