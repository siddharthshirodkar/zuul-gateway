package utilities.bill.notify;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import global.utils.GlobalContext;
import utilities.bill.datatypes.NotificationDetails;

public class NotificationSender {

	private final String LOGIN_MOBILE_NO = "9823928686"; 
	private final String LOGIN_PASSWOWD = "tandusha";
	private final String NOTIFICATION_SENDER_PAGE_URL = "https://www.160by2.com/";
	private WebDriver mWebDriver;
	
	private boolean isInitialized = false;
	
	public void triggerNotification(NotificationDetails notifDetails) throws Exception
	{
		if(!isInitialized)
			initialize();
		
		String idRecipientPhoneNumberTxtBox = "mobile";
		String idNotificationMessageTxtBox = "message";
		mWebDriver.findElement(By.id(idRecipientPhoneNumberTxtBox)).sendKeys(notifDetails.getNotificationReceiver());
		mWebDriver.findElement(By.id(idNotificationMessageTxtBox)).sendKeys(notifDetails.getNotificationText());
		
		String idSendBtn = "sendButton";
		mWebDriver.findElement(By.id(idSendBtn)).click();
		Thread.sleep(1000);
		System.out.println("Notification sent!!");
	}

	private void initialize() throws InterruptedException
	{
		mWebDriver = GlobalContext.getInstance().getWebDriver();
		mWebDriver.get(NOTIFICATION_SENDER_PAGE_URL);
		String namePhoneNumberTxtBox = "phone no";
		String namePasswordTxtBox = "pasword";
		String idLoginBtn = "sendLogin";
		mWebDriver.findElement(By.name(namePhoneNumberTxtBox)).sendKeys(LOGIN_MOBILE_NO);
		mWebDriver.findElement(By.name(namePasswordTxtBox)).sendKeys(LOGIN_PASSWOWD);
		mWebDriver.findElement(By.id(idLoginBtn)).click();
		Thread.sleep(1000);
		isInitialized = true;
	}

	public void logout() throws InterruptedException
	{
		String selLogoutLink = "body > div.wrapper > div.fixed-top > div > div.navbar-top-right > ul > li:nth-child(5) > a";
		mWebDriver.findElement(By.cssSelector((selLogoutLink))).click();
		Thread.sleep(1000);
	}
}
