package org.fungover.haze;



import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class AuthTest {
    Auth auth = new Auth();
    @Mock
    Socket client = new Socket();

    @Test
    void wrongPasswordShouldReturnFalse() {
        auth.setPassword("12345");
        assertThat(auth.authenticate("123456", client)).isFalse();
    }

    @Test
    void isPasswordSetShouldReturnTrue() {
        auth.setPassword("12345");
        assertThat(auth.isPasswordSet()).isTrue();
    }

    @Test
    void isPasswordSetShouldReturnFalseIfNotSet() {
        auth.setPassword(null);
        assertThat(auth.isPasswordSet()).isFalse();
    }

    @Test
    void authenticateReturnsTrueIfCorrectPassword() {
        auth.setPassword("123");
        assertThat(auth.authenticate("123", client)).isTrue();
    }

    @Test
    void authErrorMessage() {
        assertThat(Auth.printAuthError()).isEqualTo("-Ah ah ah, you didn't say the magic word. https://tinyurl.com/38e7yvp8".getBytes());
    }

    @Test
    void authenticatedClient() throws IOException {

        List<String> inputList = new ArrayList<>();

        assertThat(Auth.authenticateClient(auth, true, client, inputList, true)).isTrue();

    }

    @Test
    void authCommandReceivedPasswordNotAuth() {
        List<String> inputList = new ArrayList<>();

        inputList.add("AUTH");
        inputList.add("password");

        boolean result = Auth.authCommandReceived(true, inputList, false);

        assertThat(result).isTrue();


    }

}



