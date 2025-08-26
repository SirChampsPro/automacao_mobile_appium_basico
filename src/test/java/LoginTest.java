import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class LoginTest {

    private static AppiumDriverLocalService server;
    private AppiumDriver driver;

    @BeforeAll
    public static void setUpServer(){
        server = new AppiumServiceBuilder().usingPort(4723).withArgument(() -> "--base-path", "/").build();
        server.start();
    }

    @AfterAll
    public static void tearDownServer(){
        if(server != null) server.stop();
        server = null;
    }

    @BeforeEach
    public void setupDriver() throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "android");
        capabilities.setCapability("appium:automationName", "UiAutomator2");
        capabilities.setCapability("appium:deviceName", "Android Emulator"); // genérico
        capabilities.setCapability("appium:platformVersion", "13.0"); // versão do emulador
        capabilities.setCapability("appium:ignoreHiddenApiPolicyError", true);
        capabilities.setCapability("appium:ensureWebviewsHavePages", true);
        capabilities.setCapability("appium:newCommandTimeout", 3600);
        capabilities.setCapability("appium:connectHardwareKeyboard", true);
        capabilities.setCapability("appium:app", System.getProperty("user.dir") + "/apk/app.apk");

        URL url = new URL("http://127.0.0.1:4723/wd/hub"); // Appium server no runner

        driver = new AppiumDriver(url, capabilities);
    }

    @AfterEach
    public void tearDownDriver(){

        if (driver != null) driver.quit();
        driver = null;
    }

    public void tirarPrint(String ImageName) throws IOException {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String uniqueID = UUID.randomUUID().toString().substring(0, 8);
        Path destino = new File(System.getProperty("user.dir") + "/screenshots/" + ImageName + uniqueID + ".png").toPath();
        Files.createDirectories(destino.getParent());
        Files.copy(scrFile.toPath(), destino);
        System.out.println("Evidência salva em: " + destino);
    }

    public void esperarElemento(String xpath) {
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
    }

    @Test
    public void login() throws IOException {

        esperarElemento("//*[@resource-id='yourUsername']");

        WebElement user = driver.findElement(By.xpath("//*[@resource-id='yourUsername']"));
        WebElement passwd = driver.findElement(By.xpath("//*[@resource-id='yourPassword']"));
        WebElement btnLogin = driver.findElement(By.xpath("//*[@resource-id='logginButton']"));

        user.sendKeys("admin@automacao.org.br");
        passwd.sendKeys("password01");

        btnLogin.click();

        tirarPrint("evidencia");

        esperarElemento("//*[@text='Dashboard'][1]");

        tirarPrint("evidencia");
    }
}
