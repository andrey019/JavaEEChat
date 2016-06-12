import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public final class BackUpHandler extends Thread {

    private static final String USERS_PATH = "c:\\temp\\users.dbs";
    private static final String USERS_PATH_BAK = "c:\\temp\\users.dbs.bak";
    private static long usersAmount;

    public BackUpHandler() {
        this.start();
    }

    @Override
    public void run() {
        restoreUsers();

        while (!isInterrupted()) {
            makeUsersBackup();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void restoreUsers() {
        if (!getUsersFromDisk(USERS_PATH)) {
            System.out.println("Main user database restoring is failed!");
            if (!getUsersFromDisk(USERS_PATH_BAK)) {
                System.out.println("Auxiliary user database restoring is failed!");
            } else {
                System.out.println("Users restored from auxiliary backup");
                usersAmount = RegData.getUsers().size();
            }
        } else {
            System.out.println("Users restored from main backup");
            usersAmount = RegData.getUsers().size();
        }
    }

    private boolean getUsersFromDisk(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(path);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                try {
                    RegData.setUsers((ConcurrentHashMap<String, String>) objectInputStream.readObject());
                    objectInputStream.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    objectInputStream.close();
                }
            } else {
                System.out.println("No backup info at '" + path + "'. Restoring failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void makeUsersBackup() {
        if (usersAmount < RegData.getUsers().size()) {
            if (writeUsersBackup(USERS_PATH)) {
                usersAmount = RegData.getUsers().size();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (writeUsersBackup(USERS_PATH_BAK)) {
                usersAmount = RegData.getUsers().size();
            }
        }
    }

    private boolean writeUsersBackup(String path) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            try {
                objectOutputStream.writeObject(RegData.getUsers());
                objectOutputStream.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Creating backup at '" + path + "' failed!");
            } finally {
                objectOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
