package utilities.waterbill.notify;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import utilities.waterbill.datatypes.BillingDetails;

public class NotifyWaterBill {

	//Selenium specific constants
	private static final String BASE_HOME_DIR = "C:/Users/siddharth.s/";
	private static final String DRIVER_PATH = BASE_HOME_DIR + "eclipse-workspace/artifacts/driverss/";
	
	private static final String BILL_PREVIEW_PAGE_TITLE = "Goa Online";
	private static final String BILL_PREVIEW_PAGE_URL = "https://goaonline.gov.in/PG/21_PWD/pwdPaymt";
	
	private static final long SLEEP_TIME_BETWEEN_PAGE_LOAD_CHECKS = 3000;
	
	private static final String DRIVER_PROPERTY_NAME = "webdriver.chrome.driver";
	private static final String DRIVER_EXE_NAME ="chromedriver.exe";
	private static final String SELENIUM_LOGGER_CLASS = "org.openqa.selenium";
	
	private static final String CONSUMER_ID = "29191161";
	
	private static final String SCREENSHOT_NAME_EXT_FORMAT = "yyyyMMdd_HH_mm_ss";
	private static final String SCREENSHOT_SAVE_DIR = BASE_HOME_DIR + "screenshots/";
	private static final String SCREENSHOT_PREFIX = "WaterBill_";
	
	private static final String DATE_PATTERN_ISSUE_DATE = "dd-MMM-yyyy";
	private static final String DATE_PATTERN_BILLING_PERIOD = "dd MMM yyyy";
	
	private WebDriver webDriver;
	
	public static void main(String[] args)  throws Exception{
		System.out.println("Started.. ");
		long startTime = System.currentTimeMillis();
		NotifyWaterBill notifyWaterBillGeneration = new NotifyWaterBill();
		notifyWaterBillGeneration.notifyBillGeneration();
		System.out.println("Finished..");
		System.out.println("Time taken = "+(System.currentTimeMillis() - startTime)/1000+" secs");
	}

	private void notifyBillGeneration()  throws Exception
	{
		openBrowser();
		BillingDetails bill = populatebillingDetails();
		saveScreenshot();
		handleBillNotification(bill);
		closeBrowser();
	}
	
	private void handleBillNotification(BillingDetails bill) {
		if(bill.getDueDate().after(new Date()))
			System.out.println("Congratulations, Bill can be paid!!!");
		else
			System.out.println("Alas!!, Bill cannot be paid anymore...");
		//System.out.println(bill);
	}

	private BillingDetails populatebillingDetails() throws Exception
	{
		waitForPageToLoad(BILL_PREVIEW_PAGE_TITLE);
		
		String idConsumerIdTextBox = "Main_txtConsKey";
		String idClickToPreviewBillBtn = "Main_btnSubmit";
		
		webDriver.findElement(By.id(idConsumerIdTextBox)).click();
		webDriver.findElement(By.id(idConsumerIdTextBox)).sendKeys(CONSUMER_ID);
		webDriver.findElement(By.id(idClickToPreviewBillBtn)).click();
		
		//String idDisplayPanel = "cphAdv_UpdatePanel1"; // --> PAYMENT NOT POSSIBLE:
													//~ Due Date of your bill is crossed. Online payment is not accepted after Due Date.
		
		String idResponseDisplayPanel = "Main_pnlPostHide";
		
		Thread.sleep(1000);
		String previewResponseString = webDriver.findElement(By.id(idResponseDisplayPanel)).getText();
		String[] previewResponseArr = previewResponseString.split("\n");
		
		BillingDetails billingDetails = new BillingDetails();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_BILLING_PERIOD);
		for(String previewResponse : previewResponseArr)
		{
			if(previewResponse.startsWith("Billing Period"))
			{	
				String billingPeriodDates[] = previewResponse.substring(previewResponse.indexOf(":")+1).split("-");
				billingDetails.setBillingPeriodStartDate(sdf.parse(billingPeriodDates[0]));
				billingDetails.setBillingPeriodEndDate(sdf.parse(billingPeriodDates[1]));
			}
			if(previewResponse.startsWith("Issue Date"))
			{
				String issueDate = previewResponse.trim().substring(previewResponse.indexOf(":")+1,previewResponse.indexOf(":")+DATE_PATTERN_ISSUE_DATE.length()+2);
				String dueDate = previewResponse.trim().substring(previewResponse.lastIndexOf(":")+1);
				sdf.applyPattern(DATE_PATTERN_ISSUE_DATE);
				billingDetails.setIssueDate(sdf.parse(issueDate));
				billingDetails.setDueDate(sdf.parse(dueDate));
			}
			if(previewResponse.startsWith("Bill Amount Payable"))
			{
				String amountPayableString = previewResponse.substring(previewResponse.indexOf(":")+1,previewResponse.indexOf("/")).trim();
				billingDetails.setBillAmount(Integer.valueOf(amountPayableString));
			}
		}
		//System.out.println(billingDetails);
		return billingDetails;
	}
	
	private void saveScreenshot() throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat(SCREENSHOT_NAME_EXT_FORMAT);
		File screenshotFile = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
		Path destScreenshotPath = Paths.get(SCREENSHOT_SAVE_DIR+SCREENSHOT_PREFIX+sdf.format(new Date())+screenshotFile.getName().substring(screenshotFile.getName().lastIndexOf(".")));
	    Path srcScreenshotPath = screenshotFile.toPath();
	    Files.copy(srcScreenshotPath, destScreenshotPath, StandardCopyOption.REPLACE_EXISTING);
	    Desktop.getDesktop().open(destScreenshotPath.toFile());
	}
	
	private void waitForPageToLoad(String pageTitle) throws InterruptedException
	{
		while(true)
		{
			if(webDriver.getTitle().trim().equalsIgnoreCase(pageTitle))
				return;
			Thread.sleep(SLEEP_TIME_BETWEEN_PAGE_LOAD_CHECKS);
		}
	}
	
	
	private void openBrowser()
	{
		Logger.getLogger(SELENIUM_LOGGER_CLASS).setLevel(Level.OFF);
		System.setProperty(DRIVER_PROPERTY_NAME, DRIVER_PATH+DRIVER_EXE_NAME);
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setHeadless(true);
		chromeOptions.addArguments("--start-maximized");
	    System.setProperty("webdriver.chrome.silentOutput", "true");

		webDriver = new ChromeDriver(chromeOptions);

		//webDriver.manage().window().maximize();
		webDriver.get(BILL_PREVIEW_PAGE_URL);
	}
	
	private void closeBrowser() {
		webDriver.close();
	}
}
