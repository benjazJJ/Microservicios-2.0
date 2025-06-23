package Microservicio.Microservicio.Roles.Y.Permisos.Service;

import org.mindrot.jbcrypt.BCrypt;

public class Encriptador {
    public static String encriptar(String contrasena) {
        return BCrypt.hashpw(contrasena, BCrypt.gensalt());
    }

    public static boolean comparar(String contrasena, String hashGuardado) {
        return BCrypt.checkpw(contrasena, hashGuardado);
    }
}
