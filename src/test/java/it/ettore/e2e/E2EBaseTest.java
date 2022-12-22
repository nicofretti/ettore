package it.ettore.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Every class that provides end-to-end test should extend from E2EBaseTest. This class handles the compilation and
// deployment of the application on a random port and the setup of a browser driver.
public abstract class E2EBaseTest {
    protected WebDriver driver;

    @BeforeClass
    public static void setup() {
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void before() {
        ChromeOptions opts = new ChromeOptions();
        if (System.getenv().getOrDefault("ETTORE_HEADLESS_E2E", "FALSE").equals("TRUE")) {
            opts.setHeadless(true);
        }
        driver = new ChromeDriver(opts);
    }

    @After
    public void after() {
        driver.quit();
    }

    @LocalServerPort
    private int port;

    // Returns the base domain that end-to-end tests should work with. This is necessary because the port for the
    // testing web server is chosen at random to allow for running E2E tests while the default port is already in use.
    protected String baseDomain() {
        return String.format("http://localhost:%d/", port);
    }
}
