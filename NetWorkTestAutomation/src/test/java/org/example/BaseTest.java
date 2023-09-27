package org.example;

import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BaseTest {
    public static   WebDriver driver;
   private String baseUrl = "https://www.network.com.tr/"; // Projeye özgü URL'yi burada tanımlayın
    //private String baseUrl = "https://www.amazon.com.tr/"; // Projeye özgü URL'yi burada tanımlayın


    @BeforeScenario
    public void setUp() {
        // WebDriver başlatma işlemleri
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe"); // ChromeDriver'ın yolu
      //  ChromeOptions options = new ChromeOptions();
        // İsteğe bağlı olarak Chrome seçenekleri ayarlayabilirsiniz.
        // Örneğin, başlatma seçenekleri, proxy ayarları, başlatma sayfası vb. ekleyebilirsiniz.
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(baseUrl);
    }

    @AfterScenario
    public void tearDown() {
        // WebDriver kapatma işlemi
        if (driver != null) {
            driver.quit();
        }
    }
}