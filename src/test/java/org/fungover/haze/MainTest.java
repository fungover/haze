package org.fungover.haze;
import org.apache.logging.log4j.core.jmx.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.Mockito.*;

class MainTest {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    @BeforeEach
    void setUp() throws IOException {
        serverSocket = new ServerSocket(0);
        clientSocket = new Socket("localhost", serverSocket.getLocalPort());
    }

    @AfterEach
    void tearDown() throws IOException {
        serverSocket.close();
        clientSocket.close();
    }

    HazeDatabase database = new HazeDatabase();
    HazeList hazeList = new HazeList(database);



    @Test
    void callingExecuteCommandWithValidNonExistingInputReturnsColonOne() {
        assertThat(Main.executeCommand(database, List.of("SETNX", "1", "This is a value"), hazeList)).isEqualTo(":1\r\n");
    }

    @Test
    void callingExecuteCommandWithInvalidInputStringReturnsErrorMessage() {
        assertThat(Main.executeCommand(database, List.of(""), hazeList)).isEqualTo("-ERR no command provided\r\n");

    }

    @Test
    void executeCommandCanHandleCommandsInBothUpperAndLowerCase() {
        Main.executeCommand(database, List.of("sEtNx", "1", "This is a value"), hazeList);
        assertThat(Main.executeCommand(database, List.of("sEtNx", "1", "This is also a value"), hazeList)).isEqualTo(":0\r\n");
    }

    @Test
    void callExecuteCommandWithAnEmptyPingShouldReturnPong() {
        assertThat(Main.executeCommand(database, List.of("Ping"), hazeList)).isEqualTo("+PONG\r\n");
    }

    @Test
    void callExecuteCommandWithPingAndMessageShouldReturnTheMessageAsBulkString() {
        assertThat(Main.executeCommand(database, List.of("Ping", "test message"), hazeList)).isEqualTo("$12\r\ntest message\r\n");
    }

    @Test
    void callExecuteCommandWithGetAndKeyShouldReturnTheValueAsBulkString() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"), hazeList);
        assertThat(Main.executeCommand(database, List.of("GET", "theKey"), hazeList)).isEqualTo("$8\r\ntheValue\r\n");
    }

    @Test
    void callExecuteCommandWithDelAndExistingKeyShouldReturnOne() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"), hazeList);
        assertThat(Main.executeCommand(database, List.of("Del", "theKey"), hazeList)).isEqualTo(":1\r\n");
    }

    @Test
    void callExecuteCommandWithExistsAndOneExistingKeyShouldReturnOne() {
        Main.executeCommand(database, List.of("SET", "theKey", "theValue"), hazeList);
        assertThat(Main.executeCommand(database, List.of("Exists", "theKey", "secondKey"), hazeList)).isEqualTo(":1\r\n");
    }

    @Test
    void callExecuteCommandWithSaveShouldReturnOK() {
        Main.executeCommand(database, List.of("SAVE"), hazeList);
        assertThat(SaveFile.writeOnFile(database.copy())).isEqualTo("+OK\r\n");
    }

    @ParameterizedTest
    @ValueSource(strings = {"SET", "SETNX"})
    void callingExecuteCommandWithWrongNumberOfArgumentsResultsInErrorMessage(String command) {
        assertThat(Main.executeCommand(database, List.of(command, "key"), hazeList))
                .isEqualTo("-ERR wrong number of arguments for command\r\n");
    }

    @ParameterizedTest
    @ValueSource(strings = {"LPUSH", "RPUSH"})
    void callingExecuteCommandWithListOfSizeOneReturnsOne(String command) {
        assertThat(Main.executeCommand(database, List.of(command, "key", "1"), hazeList))
                .isEqualTo(":1\r\n");
    }

    @ParameterizedTest
    @CsvSource({"LPUSH, LPOP", "RPUSH, RPOP"})
    void callingExecuteCommandWithListOfSizeOneReturnsBulkString(String firstCommand, String secondCommand) {
        Main.executeCommand(database, List.of(firstCommand, "key", "hello"), hazeList);
        assertThat(Main.executeCommand(database, List.of(secondCommand, "key"), hazeList))
                .isEqualTo("$5\r\nhello\r\n");
    }

    @Test
    void callExecuteCommandWithLLENShouldReturnThree() {
        Main.executeCommand(database, List.of("LPUSH", "key", "1", "2", "3"), hazeList);
        assertThat(Main.executeCommand(database, List.of("LLEN", "key"), hazeList)).isEqualTo(":3\r\n");
    }

    @Test
    void callExecuteCommandWithLMOVEShouldReturnErrorMessageWhenListIsEmpty() {
        assertThat(Main.executeCommand(database, List.of("LMOVE", "key"), hazeList)).isEqualTo("-ERR wrong number of arguments for command.\r\n");
    }

    @Test
    void callExecuteCommandWithLTRIMShouldReturnErrorMessageWhenKeyDoesNotExist() {
        assertThat(Main.executeCommand(database, List.of("LTRIM", "key", "2", "3"), hazeList)).isEqualTo("-The key is not present in the database.\r\n");
    }




    @Test
    @DisplayName("getInputList Should Return List With Correct Data Based On Index")
    void getInputListShouldReturn(){
        String inputString = "First\nSecond\nThird";
        BufferedReader input = new BufferedReader(new StringReader(inputString));
        try {
            List<String> result = Main.getInputList(input);
            assertThat(result.get(1)).isEqualTo("Second");

        } catch (Exception e) {
            System.out.println("Exception");
        }
    }



    @Test
    @DisplayName("Call authCommandReceived with valid input should return true")
    void callAuthCommandReceivedWithValidInputShouldReturnTrue() {
        boolean isPasswordSet = true;
        boolean clientAuthenticated = false;
        List<String> inputList = List.of("AUTH", "password");
        boolean result = Main.authCommandReceived(isPasswordSet, inputList, clientAuthenticated);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Call AuthCommandReceived With Invalid Input Should Return False")
    void callAuthCommandReceivedWithInvalidInputShouldReturnFalse() {
        boolean isPasswordSet = true;
        boolean clientAuthenticated = false;
        List<String> inputList = List.of("AUTHO", "password");
        boolean result = Main.authCommandReceived(isPasswordSet, inputList, clientAuthenticated);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Call AuthCommandReceived With Invalid Password Should Return False")
    void callAuthCommandReceivedWithInvalidPasswordShouldReturnFalse() {
        boolean isPasswordSet = false;
        boolean clientAuthenticated = false;
        List<String> inputList = List.of("AUTH", "password");
        boolean result = Main.authCommandReceived(isPasswordSet, inputList, clientAuthenticated);
        assertThat(result).isFalse();
    }

    @Test
    void callToInitializeServerShouldSetPasswordToAuth(){
        String[] testArgs = {"arg1", "arg2"};
        Initialize initialize = new Initialize();
        Auth auth = new Auth();
        Main.initializeServer(testArgs, initialize, auth);
        assertThat(auth.isPasswordSet()).isTrue();
    }

    @Test
    @DisplayName("Call to shutdownClientIfNotAuthenticated should shut down output for non authenticated user")
    void callToShutDownClientIfNotAuthenticatedShouldShutDownOutputForNonAuthenticatedUser() throws IOException {
        boolean clientAuthenticated = false;
        boolean isPasswordSet = true;
        Main.shutdownClientIfNotAuthenticated(clientSocket, clientAuthenticated, isPasswordSet);
        assertThat(clientSocket.isOutputShutdown()).isEqualTo(true);
    }

    @Test
    @DisplayName("Call to shutdownClientIfNotAuthenticated should not shut down output for authenticated user")
    void callToShutDownClientIfNotAuthenticatedShouldNotShutDownOutputForAuthenticatedUser() throws IOException {
        boolean clientAuthenticated = true;
        boolean isPasswordSet = true;
        Main.shutdownClientIfNotAuthenticated(clientSocket, clientAuthenticated, isPasswordSet);
        assertThat(clientSocket.isOutputShutdown()).isEqualTo(false);
    }


    @Test
    @DisplayName("Call to initSocket should bind serversocket port to same as initialize")
    void callToInitSocketShouldBindServerSocketPortToSameAsInitialize() throws IOException {
        Initialize initialize = new Initialize();
        ServerSocket ss = new ServerSocket();
        int initializePort = initialize.getPort();
        int serverSocketPort = ss.getLocalPort();
        assertThat(initializePort).isNotEqualTo(serverSocketPort);

        Main.initSocket(initialize, ss);
        initializePort = initialize.getPort();
        serverSocketPort = ss.getLocalPort();
        assertThat(initializePort).isEqualTo(serverSocketPort);
    }

}
