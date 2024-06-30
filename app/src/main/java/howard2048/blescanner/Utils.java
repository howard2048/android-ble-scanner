package howard2048.blescanner;

public class Utils {
    public static String bytesToString(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bs) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
