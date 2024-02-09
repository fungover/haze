package org.fungover.haze;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class InitializeTest {

    Initialize initialize = new Initialize();

    @BeforeEach
    void setup() {
        initialize.clearCliOptions();
    }

    @Test
    void portSetByCLIWithDashPShouldReturn1234() {
        String[] args = {"-p", "1234"};
        initialize.importCliOptions(args);

        assertThat(initialize.getPort()).isEqualTo(1234);
    }

    @Test
    void portSetByEnvironmentVariableShouldReturn6380() {
        assertThat(initialize.getPort()).isEqualTo(6380);
    }

    @Test
    void portSetByCLIWithDashPortShouldReturn1233() {
        String[] args = {"--port", "1233"};
        initialize.importCliOptions(args);

        assertThat(initialize.getPort()).isEqualTo(1233);
    }

    @Test
    void passwordSetByCLIWithDashPShouldReturn1234() {
        String[] args = {"-pw", "1234"};
        initialize.importCliOptions(args);

        assertThat(initialize.getPassword()).isEqualTo("1234");
    }

    @Test
    void passwordSetByEnvironmentVariableShouldReturn12345() {
        assertThat(initialize.getPassword()).isEqualTo("12345");
    }

    @Test
    void passwordSetByCLIWithDashPortShouldReturn1233() {
        String[] args = {"--password", "1233"};
        initialize.importCliOptions(args);

        assertThat(initialize.getPassword()).isEqualTo("1233");
    }






}
