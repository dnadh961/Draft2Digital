package com.kindle.util;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

public class WaitHandler {

	private static int GLOBAL_TIME_OUT = 60;

	public static void waitForElementPresence(WebDriver driver, By by) {
		Wait<WebDriver> wait = new WebDriverWait(driver, GLOBAL_TIME_OUT);
		wait.until(ExpectedConditions.presenceOfElementLocated(by));
	}
	
	public static void waitForElementPresence(WebDriver driver, WebElement elmt) {
		Wait<WebDriver> wait = new WebDriverWait(driver, GLOBAL_TIME_OUT);
		wait.until(ExpectedConditions.elementToBeClickable(elmt));
	}

	public static void waitForElementVisibility(WebDriver driver, By by) {
		Wait<WebDriver> wait = new WebDriverWait(driver, GLOBAL_TIME_OUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	public static void waitForElementClickable(WebDriver driver, By by) {
		Wait<WebDriver> wait = new WebDriverWait(driver, GLOBAL_TIME_OUT);
		wait.until(ExpectedConditions.elementToBeClickable(by));
	}
	
	public static void waitForPageLoaded(WebDriver driver) {
		Wait<WebDriver> wait = new WebDriverWait(driver, GLOBAL_TIME_OUT);
		wait.until(checkPageLoadStatus());
	}
	
	public static void waitForElementNotVisible(WebDriver driver, By locator) {
		Wait<WebDriver> wait = new WebDriverWait(driver, GLOBAL_TIME_OUT);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}
	
	public static void waitTillTitleContains(WebDriver driver, String text) {
		Wait<WebDriver> wait = new WebDriverWait(driver, GLOBAL_TIME_OUT);
		wait.until(ExpectedConditions.titleContains(text));
	}
	
	public static void waitForWindowPresence(WebDriver driver, Integer requiredWindowNo){
		Wait<WebDriver> wait = new WebDriverWait(driver, GLOBAL_TIME_OUT);
		wait.until(checkWindowPresence(requiredWindowNo.toString()));
	}
	
	public static void sleep(int seconds){
		try {
			Thread.sleep(seconds*1000);
		} catch (InterruptedException e) {
			//Ignore the exception
		}
	}
	
	private static Function<WebDriver, Boolean> checkPageLoadStatus() {
		return new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript(
						"return document.readyState").equals("complete");
			}
		};
	}
	
	private static Function<WebDriver, Boolean> checkWindowPresence(
			final String requireWindowCount) {
		return new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				int actualCount = driver.getWindowHandles().size();
				int requiredWindowNo = 0;
				try{
					requiredWindowNo = Integer.parseInt(requireWindowCount);
				}catch(NumberFormatException e){
					//ignore exception
				}
				
				if(requiredWindowNo <= actualCount){
					return true;
				}else{
					return false;
				}
				
			}
		};
	}
}
