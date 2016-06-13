import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public final class BackUpHandler extends Thread {

    private static final String USERS_PATH = "c:\\temp\\users.dbs";
    private static final String USERS_PATH_BAK = "c:\\temp\\users.dbs.bak";
    private static final String CHATROOM_PATH = "c:\\temp\\chatroom.dbs";
    private static final String CHATROOM_PATH_BAK = "c:\\temp\\chatroom.dbs.bak";
    private static long usersAmount;
    private static long chatroomsAmount;

    public BackUpHandler() {
        this.start();
    }

    @Override
    public void run() {
        restoreUsers();
        restoreChatrooms();

        while (!isInterrupted()) {
            makeUsersBackup();
            makeChatroomsBackup();
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

    private void restoreChatrooms() {
        if (!getChatroomsFromDisk(CHATROOM_PATH)) {
            System.out.println("Main chatroom database restoring is failed!");
            if (!getChatroomsFromDisk(CHATROOM_PATH_BAK)) {
                System.out.println("Auxiliary chatroom database restoring is failed!");
            } else {
                System.out.println("Chatrooms restored from auxiliary backup");
                chatroomsAmount = RegData.getRooms().size();
            }
        } else {
            System.out.println("Chatrooms restored from main backup");
            chatroomsAmount = RegData.getRooms().size();
        }
    }

    private boolean getChatroomsFromDisk(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(path);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                try {
                    RegData.setRooms((ConcurrentHashMap<String, ArrayList<String>>) objectInputStream.readObject());
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

    private void makeChatroomsBackup() {
        if (chatroomsAmount < RegData.getRooms().size()) {
            if (writeChatroomsBackup(CHATROOM_PATH)) {
                chatroomsAmount = RegData.getRooms().size();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (writeChatroomsBackup(CHATROOM_PATH_BAK)) {
                chatroomsAmount = RegData.getRooms().size();
            }
        }
    }

    private boolean writeChatroomsBackup(String path) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            try {
                objectOutputStream.writeObject(RegData.getRooms());
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
