package org.fungover.haze;


import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


class AuthTest {
    Auth auth = new Auth();
    @Mock
    Socket client = new Socket();
    @Mock
    private OutputStream myOutputStream;

    @Captor
    private ArgumentCaptor<byte[]> valueCapture;


    @Test
    void passwordIsNotSetShouldReturnOK() {
        auth.setPassword(null);
        assertThat(auth.authenticate()).isEqualTo("+OK\\r\\n\n");
    }

    @Test
    void wrongPasswordShouldReturnFalse() {
        auth.setPassword("12345");
        assertThat(auth.authenticate("123456", client)).isFalse();

    }

    @Test
    void authenticateWithEmptyStringIfPasswordIsSetShouldReturnError() {
        auth.setPassword("12345");
        assertThat(auth.authenticate()).isEqualTo("-Ah ah ah, you didn't say the magic word.");
    }

}
