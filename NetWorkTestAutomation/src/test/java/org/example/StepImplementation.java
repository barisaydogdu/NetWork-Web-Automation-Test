package org.example;

import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.Table;
import com.thoughtworks.gauge.TableRow;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class StepImplementation extends BaseTest {

    private boolean cookiePopupPresent = false;
    private double productPrice;
    private String randomProductText;
    private By emailField = By.id("n-input-email");
    private By passwordField = By.id("n-input-password");
    private By loginButton = By.id("login-button");
    private double originalPrice = 0; // originalPrice değişkenini genişletilmiş tanımlama yaparak sıfırladık
    private double discountedPrice = 0; // discountedPrice değişkenini genişletilmiş tanımlama yaparak sıfırladık
    private String userEmail; // userEmail tanımlandı
    private String userPassword; // userPassword tanımlandı
    @Step("Sayfanın yüklenmesi beklenir")
    public void waitForPageToLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30L)); // Max bekleme süresi 30 saniye
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));
    }

    @Step("<locatorValue> yüklenmesi beklenir <locatorType>")
    public void WaitUntilLoad(String LocatorValue,String locatorType) {

       By locator= null;
        switch (locatorType.toLowerCase()) {
            case "css":
                locator = By.cssSelector(LocatorValue);
                break;
            case "xpath":
                locator = By.xpath(LocatorValue);
                break;
            case "id":
                locator = By.id(LocatorValue);
                break;
            case "classname":
                locator = By.className(LocatorValue);
                break;
            default:
                System.out.println("Geçersiz locator türü: " + locatorType);
                return;
        }
        try {
            int timeoutSeconds = 30; // Bekleme süresi (örneğin, 30 saniye)
            Duration timeoutDuration = Duration.ofSeconds(timeoutSeconds); // Duration tipine çevirin
            WebDriverWait wait = new WebDriverWait(driver, timeoutDuration);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            System.out.println("Wait until load metodu çalışıyor");
        } catch (Exception e) {
            System.out.println("Element belirtilen süre içinde yüklenmedi.");
            // İsterseniz burada hata işleme veya raporlama yapabilirsiniz.
        }
    }

    @Step("<key> elementine tıkla")
    public void clickElementWithId(String key) {
        driver.findElement(By.id(key)).click();
    }
    @Step("<key> elementine tıkla Css")
    public void clickElementWithCss(String key) {
        driver.findElement(By.cssSelector(key)).click();
    }
    @Step("<key> elementine tıkla xpath")
    public void clickElementWithxPath(String key) {
        driver.findElement(By.xpath(key)).click();
    }
    @Step("<key> elementine tıkla class")
    public void clickElementWithClass(String key) {
        driver.findElement(By.className(key)).click();
    }

    @Step("<key> saniye kadar bekle")
    public void WaitWitSecond(int key) throws InterruptedException {
        Thread.sleep(key * 1000);
    }


    @Step("<targetURL> URL'sinin geldiği kontrol edilir")
    public void verifyURL(String targetURL) {
        String currentURL = driver.getCurrentUrl(); // Mevcut sayfanın URL'sini alın
        System.out.println("CurrentURL: " +currentURL);
        // URL'nin beklendiği şekilde olduğunu doğrulayın veya bir şartı kontrol edin
        if (currentURL.equals(targetURL)) {
            System.out.println("URL dogru: " + currentURL);
        } else {
            System.out.println("URL yanlis: " + currentURL);
        }
    }

    @Step("<inputElement> Arama sekmesine <key> yazdırılır")
    public void typeSearchKeyword(String inputElement,String key) {
        WebElement searchArea = findElement("xpath",inputElement);
        searchArea.sendKeys(key); // Belirtilen kelimeyi arama alanına yaz
    }

    @Step("Enter tuşuna basılır")
    public void PressEnter() {

        // Enter tuşuna basmak için Actions sınıfını kullanın
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.ENTER).build().perform();
    }
    @Step("Sayfada <locatorType> ile <LocatorValue> öğesine scroll yapılır")
    public void scrollToElement(String locatorType, String LocatorValue) {
        WebElement element = findElement(locatorType, LocatorValue);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        int yOffset = -300; // Yukarıda ne kadar durmasını ayarladık
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + yOffset + ");");
    }

    @Step("İlk indirimli <firstProduct> ürünü bul ve tıkla")
    public void clickFirstDiscountedProduct(String firstProduct) {
        // Ürünleri bulmak için uygun bir CSS veya XPath ifadesini kullanabilirsiniz.
        //List<WebElement> products = driver.findElements(By.className("products__item col-6 col-md-4")); // Örnek bir classname seçici
        // Ürünleri bulmak için uygun bir XPath ifadesini kullanabilirsiniz.
        List<WebElement> products = driver.findElements(By.xpath(firstProduct));
        for (WebElement product : products) {
            // Her ürünün indirim bilgisini kontrol edin (örneğin, indirim yüzdesini içeren bir etiket varsa).
            WebElement discountLabel = product.findElement(By.className("product__discountPercent")); // Örnek bir CSS seçici
            // İndirimli ürünü bulduk.
            if (discountLabel != null) {
                scrollDownToElementNew(driver,discountLabel);
                product.click();
                break; // İlk indirimli ürünü bulduktan sonra döngüyü sonlandırın.

            }
        }
    }
    @Step("Rastgele bir <size> beden seçilir ve tıklanır")
    public void chooseRandomSize(String size)
    {
        List<WebElement> sizes = driver.findElements(By.className(size));

        List<WebElement> activeSizes = sizes.stream()
                .filter(element -> element.getAttribute("class").equals("radio-box__label"))
                .collect(Collectors.toList());

        if (activeSizes.isEmpty()) {
            System.out.println("Aktif beden bulunamadı.");
            return;
        }
        int randomIndex = new Random().nextInt(activeSizes.size());
        // Seçilen ürünü tıklanır
        WebElement randomProduct = activeSizes.get(randomIndex);
         randomProductText=randomProduct.getText();
        System.out.println("Seçilen beden" + randomProduct.getText());
        //WaitUntilLoadCSS(cssElement);
        randomProduct.click();
    }
    public void scrollDownToElementNew(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'end', inline: 'nearest'});", element);
    }
    @Step("<cookieDiv> Çerez var mı kontrol edilir")
    public void checkForCookiePopup(String cookieDiv) {
        try {
            WebElement cookiePopup = driver.findElement(By.id(cookieDiv)); // Çerez kutusunun HTML ID'sini belirtin
            if (cookiePopup != null) {
                System.out.println("Cookie geldi");
                cookiePopupPresent = true;
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            if (!cookiePopupPresent) {
                System.out.println("Çerez yok");
            }
        }
    }
    @Step("<cookieAcceptButton> butonu ile kabul edilir")
    public void acceptCookie(String cookieAcceptButton) {
        if (cookiePopupPresent) {
            WebElement acceptButton = findElement("id", cookieAcceptButton);
            if (acceptButton != null) {
                acceptButton.click();
            }
        }
    }
    @Step("Sayfada <locatorType> ile <LocatorValue> tıklanır")
    public void clickButton(String locatorType, String LocatorValue) {
        WebElement element=findElement(locatorType,LocatorValue);
        element.click();
        System.out.println( LocatorValue+ " butonuna tıklandı.");
    }
    @Step("<locatorType> tipinde <locator> varlığı kontrol edilir <errorMessage>")
    public void verifyElementIsDisplayed(String locatorType,String locator,String errorMessage) {
        WebElement element = findElement(locatorType,locator);
        Assert.assertTrue(element.isDisplayed(), errorMessage);
        System.out.println(element.isDisplayed()+ "verifyElementIsDisplayed metoduna girildi.");
    }
    @Step("Ürün sayfasındaki <productPriceElementP> fiyat alınır")
    public void productPagePrice(String productPriceElementP)
    {
        WebElement productPriceElement = driver.findElement(By.xpath(productPriceElementP));
        String productPriceText = productPriceElement.getText();
        productPrice = Double.parseDouble(productPriceText.replaceAll("[^0-9.]", ""));
        System.out.println(productPriceText);
    }

    @Step("<productPriceElement> Ürün fiyatı sepet fiyatıyla karşılaştırılır")
    public void comparePrice(String productPriceElement) {
        System.out.println("karşılaştır metoduna girildi");
        List<WebElement> cartItemPriceElements = driver.findElements(By.xpath(productPriceElement));
        // Her bir ürün fiyatını sepet fiyatlarıyla karşılaştırın
        for (WebElement cartItemPriceElement : cartItemPriceElements) {
                System.out.println("for icerisine girildi");
                String cartItemPriceText = cartItemPriceElement.getText();
            if (!cartItemPriceText.isEmpty()) {
                double cartItemPrice = Double.parseDouble(cartItemPriceText.replaceAll("[^0-9.]", ""));
                // Fiyatları karşılaştırın ve eşitlik kontrolü yapın
                Assertions.assertEquals(productPrice, cartItemPrice, 0.01); //0.01 tölerans değeri
                System.out.println("ProductPrice: " +productPrice +"ve"+ "CartItemPrice:" +cartItemPriceText);
            }
        }
    }
    @Step("Sepet sayfasındaki <selectedSizeElement> beden kontrol edilir")
    public void verifyProductInfoInCart(String selectedSizeElement) {
        WebElement selectedSizeInCart = driver.findElement(By.cssSelector(selectedSizeElement));
        String selectedSizeInCartText = selectedSizeInCart.getText();
        System.out.println("selectedsizeelement: "+selectedSizeInCartText);
        System.out.println("randomproduct: "+randomProductText);

        // Seçilen bedeni ve ürün fiyatını sepetteki bilgilerle karşılaştırın
        Assert.assertEquals(randomProductText, selectedSizeInCartText);
      //  Assert.assertEquals(productPrice, productPriceInCart, 0.01); // Tolerans: 0.01
    }
    @Step("Sepet sayfasındaki <selectedPriceElement> fiyat kontrol edilir")
    public void verifyProductPriceInfoInCart(String selectedPriceElement) {
        WebElement productPriceInCartElement = driver.findElement(By.xpath(selectedPriceElement));
        double productPriceInCart = Double.parseDouble(productPriceInCartElement.getText().replaceAll("[^0-9.]", ""));
        // Seçilen bedeni ve ürün fiyatını sepetteki bilgilerle karşılaştırın
          Assert.assertEquals(productPrice, productPriceInCart, 0.01); // Tolerans: 0.01
    }


    @Step("Ürünün orjinal <originalPriceElementP> fiyatını al")
    public void getOriginalPrice(String originalPriceElementP) {
      //  WebElement originalPriceElement = driver.findElement(By.xpath("//span[contains(.,'15.599,00 TL')]"));
        WebElement originalPriceElement = driver.findElement(By.cssSelector(originalPriceElementP));
        String originalPriceText = originalPriceElement.getText();
        originalPrice = Double.parseDouble(originalPriceText.replaceAll("[^0-9.]", ""));
        System.out.println("original price : "+ originalPrice);

    }
    @Step("İndirimli <discountedElement> fiyatı al")
    public void getDiscountedPrice(String discountedElement) {
        WebElement discountedPriceElement = driver.findElement(By.xpath(discountedElement));
        //WebElement discountedPriceElement = findElement(locatorValue,discountedElement);
        String discountedPriceText = discountedPriceElement.getText();
        discountedPrice = Double.parseDouble(discountedPriceText.replaceAll("[^0-9.]", ""));
        System.out.println("Discounted Price: "+ discountedPrice);
    }

    @Step("Eski fiyatın indirimli fiyattan büyük olduğunu kontrol edilir")
    public void comparePrices() {
       Assert.assertTrue(originalPrice > discountedPrice,"Eski fiyat, indirimli fiyattan büyük değil." );
       System.out.println("Fiyat karşılaştırılması yapıldı: "+ originalPrice +discountedPrice);
    }
    @Step("Sayfa kullanıcının CSV dosyasını açar")
    public void openCSVFile() {
        String csvFilePath = "ExcelFile/networkexcel.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Her satırı işleme koyma kodu
                System.out.println(line); // Örnek olarak satırı ekrana yazdırır
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Step("Sayfa CSV dosyasından kullanıcı bilgilerini okur")
    public void readUserDataFromCSV() {
        String csvFilePath = "ExcelFile/networkexcel.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                userEmail = data[0].trim();
                userPassword = data[1].trim();
                System.out.println("IYISIN");

                // Kullanıcı bilgilerini kullanmak için
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("mahvolduk");
        }
    }
    @Step("Sayfa kullanıcı giriş yapar")
    public void performLogin() {

        enterEmail(userEmail);
        enterPassword(userPassword);
    }
    public void enterEmail(String email) {
        driver.findElement(emailField).sendKeys(email);
    }
    public void enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }
    @Step("Sepetin boş olduğu kontrol edilir")
    public void checkIfCartIsEmpty() {

        WebElement emptyCartElement =driver.findElement(By.xpath("//span[contains(.,'Sepetiniz Henüz Boş')]"));
           String emptyCartText= emptyCartElement.getText();
        Assert.assertEquals(emptyCartText, "Sepetiniz Henüz Boş", "Sepetin durumu doğrulanamadı.");
        System.out.println("sepetin bos oldugu kontrol edildi");
    }


    private WebElement findElement(String locatorType, String LocatorValue) {
        WebElement element = null;
        switch (locatorType.toLowerCase()) {
            case "id":
                element = driver.findElement(By.id(LocatorValue));
                break;
            case "css":
                element = driver.findElement(By.cssSelector(LocatorValue));
                break;
                case "classname":
                element = driver.findElement(By.className(LocatorValue));
                break;
            case "xpath":
                element = driver.findElement(By.xpath(LocatorValue));
                break;
            default:
                throw new IllegalArgumentException("Invalid locator type: " + locatorType);
        }
        return element;
    }
}
  /*  @Step("Id ile <element> butona tikla")
    public void ClickElementById(String element)
    {
        driver.findElement(By.id(element)).click();
    }

    @Step("ClassName ile <element> elemente tıkla")
    public void ClickElementByClassName(String element)
    {
        driver.findElement(By.className(element)).click();
    }
    @Step("Css ile <element >elementi bul ve <key> değerini yaz")
    public void sendByCss(String element,String key)
    {
        driver.findElement(By.cssSelector(element)).sendKeys(key);
    }
    @Step("ClassName ile <element> elementi bul ve <key> değerini yaz")
    public void sendByClassName(String element,String key)
    {
        driver.findElement(By.className(element)).sendKeys(key);
    }
*/


