import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHashGenerator {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java PasswordHashGenerator.java <password>");
            System.exit(1);
        }
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(args[0].toCharArray(), salt, 120_000, 256);
        byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec).getEncoded();
        spec.clearPassword();
        System.out.println("SALT=" + Base64.getEncoder().encodeToString(salt));
        System.out.println("HASH=" + Base64.getEncoder().encodeToString(hash));
    }
}
