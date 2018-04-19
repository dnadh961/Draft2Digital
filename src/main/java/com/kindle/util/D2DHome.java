package com.kindle.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;

public class D2DHome {

	private WebDriver driver;
	private Actions actions;
	private Properties dataProp;

	public D2DHome() {
		openBrowser();
		actions = new Actions(driver);
		dataProp = new Properties();
		try {
			FileInputStream fis1 = new FileInputStream(
					System.getProperty("user.dir") + "/src/main/resources/Data.properties");
			dataProp.load(fis1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getNumOfBooks(){
		return Integer.parseInt(dataProp.getProperty("totalBooks"));
	}
	
	public String[] getTitles() {
		String[] titleList = dataProp.getProperty("titles").split("\\$");
		return titleList;
	}
	
	public String getBooksFldrPath(){
		return dataProp.getProperty("bookpath");
	}
	
	public String getImgFldrPath(){
		return dataProp.getProperty("imgpath");
	}
	
	public String[] getDesc() {
		String[] descList = new String[getNumOfBooks()];
		File file = new File(dataProp.getProperty("descpath"));
		for(int i=1; i<=descList.length; i++){
			String text = "";
			try{
				Scanner sc = new Scanner(new File(file.getAbsolutePath()+"/"+i+".txt"));
				while(sc.hasNextLine()){
					text = text+sc.nextLine()+"\r\n";
				}
				sc.close();
			}catch(Exception e){
				
			}
			descList[i-1]=text;
		}
		return descList;
	}
	
	public void logout(){
		click(By.linkText("Log Out"));
		WaitHandler.waitForPageLoaded(driver);
		driver.close();
		driver.quit();
	}
	
	public void login(){
		driver.navigate().to(Constants.URL);
		click(By.xpath("//span[text()='Log In']"));
		findElmt(By.id("id_username")).sendKeys(dataProp.getProperty("username"));
		findElmt(By.id("id_password")).sendKeys(dataProp.getProperty("password"));
		click(By.xpath("//input[@value='SUBMIT']"));
	}
	
	public void addNewBook(){
		click(By.xpath("//li[text()='MY BOOKS']"));
		WaitHandler.sleep(6);
		try{
			driver.findElement(By.id("no_thanks_button")).click();
			WaitHandler.waitForPageLoaded(driver);
		}catch(Exception e){
			//ignore exception
		}
		click(By.xpath("//div[@id='btn-add-book']//img"));
		WaitHandler.waitForPageLoaded(driver);
	}
	
	public void confirmRights(){
		findElmt(By.id("rights-confirm-radio-1")).click();
		findElmt(By.id("publish_approved")).click();
	}
	
	public void clickSubmit(){
		findElmt(By.id("publish_submit_button")).click();
	}
	
	public void publishMyBook(){
		findElmt(By.id("publish_submit")).click();
		WaitHandler.waitForElementPresence(driver, By.id("publish-success-header"));
	}
	
	public void publishToAllStores(){
		List<WebElement> toggles = driver.findElements(By.xpath("//div[@class='toggle']"));
		for(WebElement toggle : toggles){
			toggle.click();
			String label = toggle.getAttribute("aria-label");
			if(label.equals("Amazon")){
				findElmt(By.id("amazon_approved")).click();
				WaitHandler.sleep(2);
				findElmt(By.id("amazon_submit")).click();
			}
		}
	}
	
	public void enterPrice(String price){
		findElmt(By.id("id_bookprice")).sendKeys(price);
	}
	
	public void tickReviewBox(){
		findElmt(By.xpath("//div[@class='layout-approval-wrapper']/span")).click();
		WaitHandler.sleep(3);
	}
	
	public void fillBookDetails(String filePath, String title, String author, String desc,
			String publisher, String searchTerm){
		findElmt(By.id("id_content_original")).sendKeys(filePath);
		WaitHandler.waitForElementVisibility(driver, By.cssSelector("img[class*='upload-success']"));
		findElmt(By.id("id_title")).sendKeys(title);
		addContributor(author);
		addDescription(desc);
		addPublisher(publisher);
		addSearchTerm(searchTerm);
		addSubjects();
	}
	
	public void saveAndContinue(){
		saveAndContinue(false);
	}
	
	public void saveAndContinue(boolean next){
		String id = next?"id_form_submit_button_next":"id_form_submit_button";
		findElmt(By.id(id)).click();
		WaitHandler.waitForPageLoaded(driver);
	}
	
	private void addPublisher(String publisher) {
		findElmt(By.id("id_publisherSelectBoxIt")).click();
		WaitHandler.sleep(3);
		findElmt(By.linkText("Add Publisher")).click();
		WaitHandler.sleep(2);
		findElmt(By.id("newpublishername")).sendKeys(publisher+Keys.ENTER);
		WaitHandler.sleep(4);
	}

	public void addSubjects(){
		findElmt(By.id("BISACSearch")).sendKeys("Fiction"+Keys.ENTER);
		WaitHandler.sleep(2);
		findElmt(By.xpath("(//li[@literal='FICTION'])[last()]/ins")).click();
		WaitHandler.sleep(3);
		findElmt(By.xpath("(//li[@literal='FICTION / Romance'])[last()]/ins")).click();
		WaitHandler.sleep(3);
		String[] subjects = dataProp.getProperty("subjects").split(",");
		for(String subj : subjects) {
			findElmt(By.xpath("(//li[@literal='FICTION / Romance'])[last()]//a[text()='"+subj+"']")).click();
		}
	}
	
	private void addSearchTerm(String searchTerm) {
		findElmt(By.id("id_searchterms")).sendKeys(searchTerm+Keys.ENTER);
	}

	private void addDescription(String desc) {
		WebElement frameElmt = driver.findElement(By.xpath("//div[@id='cke_1_contents']/iframe"));
		driver.switchTo().frame(frameElmt);
		WebElement el  =  driver.switchTo().activeElement();
		new Actions(driver).moveToElement(el).perform();
		driver.findElement(By.xpath("/html/body")).sendKeys(desc);
		driver.switchTo().defaultContent();
	}

	private void addContributor(String author) {
		WebElement contributor = findElmt(By.xpath("(//span[contains(@id, 'contributorSelectBoxItTex')])[last()]"));
		String contributorId = contributor.getAttribute("id");
		String roleSelectId = contributorId.replace("SelectBoxItText", "_roleSelectBoxItText");
		String authorOptionsId = contributorId.replace("SelectBoxItText", "_roleSelectBoxItOptions");
		authorOptionsId = authorOptionsId.substring(authorOptionsId.indexOf("id_form"));
		contributor.click();
		click(By.xpath("(//a[text()='Add Contributor'])[last()]"));
		findElmt(By.id("newcname")).sendKeys(author+Keys.ENTER);
		WaitHandler.sleep(3);
		clickWithJs(By.id(roleSelectId));
		WaitHandler.sleep(3);
		clickVisible(By.xpath("//a[text()='Author']"));
	}

	public String[] getAuthors() {
		String[] authorList = dataProp.getProperty("authors").split(",");
		return authorList;
	}
	
	public String[] getPublishers() {
		String[] publishers = dataProp.getProperty("publishers").split(",");
		return publishers;
	}
	
	public String[] getSearchTerms() {
		String[] authorList = dataProp.getProperty("searchTerms").split(",");
		return authorList;
	}

	public void openBrowser() {
		String browserType = Constants.BROWSER;
		if (browserType.equalsIgnoreCase("mozilla")) {
			System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
		} else if (browserType.equalsIgnoreCase("chrome")) {
			System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--start-maximized");
			options.addArguments("disable-infobars");
			driver = new ChromeDriver(options);

		} else if (browserType.equalsIgnoreCase("ie")) {
			System.setProperty("webdriver.ie.driver", "IEDriverServer.exe");
			driver = new InternetExplorerDriver();
		}
	}

	public void click(By by) {
		click(by, false);
	}
	
	public void mouseover(By by) {
		WebElement e = findElmt(by);
		actions.moveToElement(e).build().perform();
	}
	
	public void clickVisible(By by){
		List<WebElement> elmts = driver.findElements(by);
		for(WebElement elmt: elmts){
			if(elmt.isDisplayed()){
				elmt.click();
				break;
			}
		}
	}
	
	public void click(By by, boolean scroll) {
		WebElement e = findElmt(by);
		if(scroll){
			((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView()", e);
		}
		e.click();
		WaitHandler.waitForPageLoaded(driver);
	}
	
	public void clickWithJs(By by) {
		WebElement e = findElmt(by);
		((JavascriptExecutor)driver).executeScript("arguments[0].click()", e);
		WaitHandler.waitForPageLoaded(driver);
	}

	public WebElement findElmt(By by){
		WaitHandler.waitForElementPresence(driver, by);
		return driver.findElement(by);
	}
	
	public boolean verifyElmt(By by){
		WaitHandler.sleep(3);
		boolean isPresent = true;
		try{
			driver.findElement(by);
		}catch (Exception e) {
			isPresent = false;
		}
		return isPresent;
	}

	public void uploadCoverPage(String imgPath) {
		findElmt(By.id("id_cover_image")).sendKeys(imgPath);
		WaitHandler.waitForElementVisibility(driver, By.cssSelector("img[class*='upload-success']"));
	}
}
