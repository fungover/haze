package org.fungover.haze;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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

    @Test
    void authCommandReceivedNoPassNoAuth() {
        List<String> inputList = new ArrayList<>();
        inputList.add("AUTH");
        inputList.add("password");

        boolean result = Auth.authCommandReceived(false, inputList, false);

        assertThat(result).isFalse();

    }

    @Test
    void authenticateShouldShutdownOutputForInvalidPassword() throws IOException {

        Auth auth = new Auth();
        auth.setPassword("12345");

        Socket client = Mockito.mock(Socket.class);
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        when(client.getOutputStream()).thenReturn(outputStream);

        auth.authenticate("wrongPassword", client);

        verify(client).shutdownOutput();
    }

    @Test
    void  authenticateClientShouldReturnTrueForValidPassword() throws IOException {

        Auth auth = Mockito.mock(Auth.class);
        Socket client = Mockito.mock(Socket.class);
        List<String> inputList = new ArrayList<>();
        inputList.add("AUTH");
        inputList.add("password");

        when(auth.authenticate(inputList.get(1), client)).thenReturn(true);

        boolean result = Auth.authenticateClient(auth, true, client, inputList, false);

        assertTrue(result);
    }

    @Test
    void shutdownClientIfNotAuthenticatedWhenClientNotAuthenticatedAndPasswordIsSet() throws Exception {

        Socket client = Mockito.mock(Socket.class);
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        when(client.getOutputStream()).thenReturn(outputStream);

        Method method = Auth.class.getDeclaredMethod("shutdownClientIfNotAuthenticated", Socket.class, boolean.class, boolean.class);
        method.setAccessible(true);

        method.invoke(null, client, false, true);

        verify(outputStream).write(Auth.printAuthError());
        verify(client).shutdownOutput();

    }

    @Test
    void clientShouldNotBeShutdownWhenAuthenticatedOrPasswordIsNotSet() throws Exception {

        Socket client = Mockito.mock(Socket.class);
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        when(client.getOutputStream()).thenReturn(outputStream);

        Method method = Auth.class.getDeclaredMethod("shutdownClientIfNotAuthenticated", Socket.class, boolean.class, boolean.class);
        method.setAccessible(true);

        method.invoke(null, client, true, true);
        verify(outputStream, Mockito.never()).write(Auth.printAuthError());
        verify(client, Mockito.never()).shutdownOutput();

        method.invoke(null, client, false, false);
        verify(outputStream, Mockito.never()).write(Auth.printAuthError());
        verify(client, Mockito.never()).shutdownOutput();
    }
}
