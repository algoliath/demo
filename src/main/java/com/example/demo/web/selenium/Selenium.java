package com.example.demo.web.selenium;

import com.example.demo.util.Source;
import com.example.demo.domain.source.datasource.wrapper.DataSourceId;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.demo.util.datasource.SpreadSheetUtils.getSpreadSheetURL;

@Slf4j
public class Selenium {

    private WebDriver driver;

    private DataSourceId sourceId;

    public Selenium(){
    }

    private void initDriver(){
        // System settings
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");

        // Option settings
        String user_agent = "User";
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1980,1020","--window-position=-2000,0");
        options.addArguments(String.format("--user-agent=%s", user_agent));
        options.addArguments("--allow-insecure-localhost");
        options.addArguments("--no-gpu");
        options.addArguments("--no-sandbox");

        // Capability settings
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(ChromeOptions.CAPABILITY, options);
        caps.setCapability("acceptInsecureCerts", caps);
        driver = new ChromeDriver(options);
        driver.get("https://accounts.google.com/v3/signin/identifier?dsh=S-738055924%3A1667033265502352&continue=https%3A%2F%2Faccounts.google.com%2F%3Fhl%3Dko&followup=https%3A%2F%2Faccounts.google.com%2F%3Fhl%3Dko&hl=ko&passive=1209600&flowName=WebLiteSignIn&flowEntry=ServiceLogin&ifkv=AQDHYWq_s6rdqk9D3g6Jlbbo1rV_cCTH0k4TyO8moW29IXdvnVVN76oEmaMxCTPMKExpwLPzmKDxpw");
    }

    public boolean startWebDriver(Source<DataSourceId> fileIdSource)  {

        sourceId = fileIdSource.get("sourceId").get();

        if(sourceId == null){
            return false;
        }

        initDriver();

        WebElement id,password;
        WebElement id_button, pw_button;
        id = getWebElementByXPath("//*[@id=\"identifierId\"]");
        id_button = getWebElementByXPath("//*[@id=\"identifierNext\"]");
        log.info("button={}", id_button.isDisplayed());
        log.info("button={}", id_button.isEnabled());
        id_button.click();

        password = getWebElementByXPath("//*[@id=\"password\"]");
        pw_button = getWebElementByXPath("//*[@id=\"passwordNext\"]");
        pw_button.click();

        driver.get(getSpreadSheetURL(sourceId.getOriginalFileId()));
        driver.manage().window().setPosition(new Point(0,0));
        driver.manage().window().maximize();
        return true;
    }

    public void redirectWebDriver(){;
        driver.manage().window().setSize(new Dimension(1000, 800));
        driver.manage().window().setPosition(new Point(300,0));
        driver.get(getSpreadSheetURL(sourceId.getOriginalFileId()));
    }

    public void closeWebDriver(){
        driver.close();
    }

    public void setSpreadSheetData() {
        WebElement menu;
        List<WebElement> sub_menu = getWebElementsByCssSelector("#docs-menubars");
        log.info("descendant={}", sub_menu);
        menu = getWebElementByXPath("//*[@id=\"docs-file-menu\"]");
        menu.click();
        sub_menu = getWebElementsByCssSelector("#docs-file-menu");
        log.info("descendant={}", sub_menu);
    }
    public Map<String, Object> getSpreadSheetData(){

        JSONObject jsonObject = new JSONObject();
        JsonParser jsonParser = new BasicJsonParser();
        WebElement sheet_title_tab, sheet_range_tab;
        WebElement checkbox;

        checkbox = getWebElementByCssSelector("#docs-star");
        if(isChecked(checkbox)){
            checkbox.click();
        }

        while(true){
            try{
                sheet_title_tab = getWebElementByXPath("//*[@id=\":y\"]/div/div/div[1]/span");
                sheet_range_tab = getWebElementByCssSelector("#t-name-box");
                checkbox = getWebElementByCssSelector("#docs-star");
                log.info("spreadSheetId={}", sheet_title_tab.getText());
                log.info("spreadSheetRange={}", sheet_range_tab.getAttribute("value"));
                log.info("checked={}", isChecked(checkbox));
                if(isChecked(checkbox)){
                    break;
                }
            }
            catch(UnhandledAlertException e){
                log.info("e={}", e);
            }
        }

        String spreadSheetId = sheet_title_tab.getText();
        String spreadSheetRange = sheet_range_tab.getAttribute("value");

        try{
            if(StringUtils.hasText(spreadSheetId)){
                jsonObject.put("spreadSheetId", spreadSheetId);
            }
            if(StringUtils.hasText(spreadSheetRange)){
                jsonObject.put("spreadSheetRange", spreadSheetRange);
            }
        }
        catch(JSONException e){
            System.out.println("json 예외 발생");
        }

        driver.close();
        return jsonParser.parseMap(jsonObject.toString());
    }

    private boolean isChecked(WebElement checkbox) {
        return checkbox.getAttribute("aria-checked").equals("true");
    }


    private void sleep(int timeSleepMillis) {
        try {
            Thread.sleep(timeSleepMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<WebElement> getWebElementsByCssSelector(String cssSelector) {
        List<WebElement> webElements = new ArrayList<>();
        while(webElements == null){
            try{
                webElements = driver.findElements(By.cssSelector(cssSelector));
            } catch (Exception e) {
                log.info("e={}", e);
            }
        }
        return webElements;
    }

    private WebElement getWebElementByCssSelector(String cssSelector) {
        WebElement webElement = null;
        while(webElement == null){
            try{
                webElement = driver.findElement(By.cssSelector(cssSelector));
            } catch (Exception e) {
                log.info("e={}", e);
            }
        }
        return webElement;
    }

    private WebElement getWebElementByXPath(String xPath) {
        WebElement webElement = null;
        try{
            while(webElement == null){
                webElement = driver.findElement(By.xpath(xPath));
            }
        } catch (Exception e) {
            log.info("element not found:{}", e);
        }
        return webElement;
    }


}

