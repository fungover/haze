package org.fungover.haze;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


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

    @Test
    public void testGetInitialize() throws NoSuchFieldException, IllegalAccessException {
        // Ange testargument
        String[] args = {"-p", "1234", "--password", "test"};

        // Anropa getInitialize-metoden för att skapa en Initialize-instans
        Initialize initialize = Initialize.getInitialize(args);

        // Hämta cliOptions från den skapade Initialize-instansen med reflektion
        Field field = Initialize.class.getDeclaredField("cliOptions");
        field.setAccessible(true);
        Object obj = field.get(initialize);

        // Kontrollera att cliOptions-mapen har rätt värden
        Assertions.assertNotNull(obj);
        Assertions.assertEquals("1234", ((Map<String, String>) obj).get("-p"));
        Assertions.assertEquals("test", ((Map<String, String>) obj).get("--password"));
    }



    }


