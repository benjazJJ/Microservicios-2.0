package Microservicio.Microservicio.Roles.Y.Permisos.Service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EncriptadorServiceTest {

    @Test
    void encriptarYComparar_deberiaRetornarTrueConContrasenaCorrecta() {
        String original = "miClave123";
        String hash = Encriptador.encriptar(original);

        boolean resultado = Encriptador.comparar(original, hash);

        assertThat(resultado).isTrue();
    }

    @Test
    void comparar_conContrasenaIncorrecta_deberiaRetornarFalse() {
        String original = "miClave123";
        String hash = Encriptador.encriptar(original);

        boolean resultado = Encriptador.comparar("otraClave", hash);

        assertThat(resultado).isFalse();
    }
}
