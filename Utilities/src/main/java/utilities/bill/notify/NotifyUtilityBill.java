package utilities.bill.notify;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import utilities.bill.datatypes.BillingDetails;
import utilities.bill.datatypes.ElectricityBillingDetails;
import utilities.bill.datatypes.WaterBillingDetails;

public class NotifyUtilityBill {

	//Selenium specific constants
	private static final String BASE_HOME_DIR = "C:/Users/siddharth.s/";
	private static final String DRIVER_PATH = BASE_HOME_DIR + "eclipse-workspace/artifacts/driverss/";
	
	private static final String WATER_BILL_PREVIEW_PAGE_TITLE = "Goa Online";
	private static final String ELECTRICITY_BILL_PREVIEW_PAGE_TITLE = "Electricity Bill Payment";
	
	private static final String WATER_BILL_PREVIEW_PAGE_URL = "https://goaonline.gov.in/PG/21_PWD/pwdPaymt";
	private static final String ELECTRICITY_BILL_PREVIEW_PAGE_URL = "https://goaelectricity.gov.in/pay_ca_no_details.aspx?ca_no=";
	
	private static final long SLEEP_TIME_BETWEEN_PAGE_LOAD_CHECKS = 3000;
	
	private static final String DRIVER_PROPERTY_NAME = "webdriver.chrome.driver";
	private static final String DRIVER_EXE_NAME ="chromedriver.exe";
	private static final String SELENIUM_LOGGER_CLASS = "org.openqa.selenium";
	
	private static final String WATER_BILL_CONSUMER_ID = "29191161";
	private static final String ELECTRICITY_BILL_CONTRACT_ID1 = "60004767434";
	private static final String ELECTRICITY_BILL_CONTRACT_ID2 = "60004887711";
	
	private static final String SCREENSHOT_NAME_EXT_FORMAT = "yyyyMMdd_HH_mm_ss";
	private static final String SCREENSHOT_SAVE_DIR = BASE_HOME_DIR + "screenshots/";
	private static final String SCREENSHOT_WATER_BILL_PREFIX = "WaterBill_";
	private static final String SCREENSHOT_ELECTRICITY_BILL_PREFIX = "ElectricityBill_";
	
	private static final String DATE_PATTERN_ISSUE_DATE = "dd-MMM-yyyy";
	private static final String DATE_PATTERN_BILLING_PERIOD = "dd MMM yyyy";
	
	private WebDriver webDriver;
	
	public static void main(String[] args)  throws Exception{
		System.out.println("Started.. ");
		long startTime = System.currentTimeMillis();
		NotifyUtilityBill notifyUtilityGeneration = new NotifyUtilityBill();
		notifyUtilityGeneration.notifyBillGeneration();
		System.out.println("Finished..");
		System.out.println("Time taken = "+(System.currentTimeMillis() - startTime)/1000+" secs");
	}

	private void notifyBillGeneration()  throws Exception
	{
		openBrowser();
		ArrayList<BillingDetails> billingDetailsList = populateElectricityBillList();
		BillingDetails waterBillDetails = populateWaterBillingDetails();
		billingDetailsList.add(waterBillDetails);
		handleBillNotifications(billingDetailsList);
		closeBrowser();
	}
	
	private void handleBillNotifications(ArrayList<BillingDetails> billingDetailsList) {
		
		for(BillingDetails bill : billingDetailsList)
		{
			System.out.print(bill.getContractAccNo()+" - ");
			if(bill.getPaymentStatus().equalsIgnoreCase("Payment is already done"))
				System.out.println("Congratulations, Bill is already paid");
			else
			{
				if(bill.getDueDate().after(new Date()))
					System.out.println("Congratulations, Bill can be paid!!!");
				else	
					System.out.println("Alas!!, Bill cannot be paid anymore...");
			}
		}
		//System.out.println(bill);
	}

	private BillingDetails populateWaterBillingDetails() throws Exception
	{
		webDriver.get(WATER_BILL_PREVIEW_PAGE_URL);
		waitForPageToLoad(WATER_BILL_PREVIEW_PAGE_TITLE);
		
		String idConsumerIdTextBox = "Main_txtConsKey";
		String idClickToPreviewBillBtn = "Main_btnSubmit";
		
		webDriver.findElement(By.id(idConsumerIdTextBox)).sendKeys(WATER_BILL_CONSUMER_ID);
		webDriver.findElement(By.id(idClickToPreviewBillBtn)).click();
		
		Thread.sleep(1000);
		saveScreenshot(SCREENSHOT_WATER_BILL_PREFIX);
		
		String idContractNo = "Main_lblConId";
		String idIssueDate = "Main_lblIssDate";
		String idDueDate = "Main_lblDueDt";
		String idBillAmount = "Main_lblBillAmt";
		String idPaymentStatus = "Main_lblPayStatus";
		String idBillingPeriod = "Main_lblPeriod";
		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_ISSUE_DATE);
		
		String contractNoTxt = webDriver.findElement(By.id(idContractNo)).getText();
		String dueDateTxt = webDriver.findElement(By.id(idDueDate)).getText();
		String issueDateTxt = webDriver.findElement(By.id(idIssueDate)).getText();
		String billAmountTxt = webDriver.findElement(By.id(idBillAmount)).getText();
		String paymentStatusTxt = webDriver.findElement(By.id(idPaymentStatus)).getText();
		String billingPeriodTxt = webDriver.findElement(By.id(idBillingPeriod)).getText();
		
		WaterBillingDetails waterBillingDetails = new WaterBillingDetails();
		waterBillingDetails.setContractAccNo(contractNoTxt);
		waterBillingDetails.setBillAmount(Integer.parseInt(billAmountTxt));
		waterBillingDetails.setDueDate(sdf.parse(dueDateTxt));
		waterBillingDetails.setIssueDate(sdf.parse(issueDateTxt));
		waterBillingDetails.setPaymentStatus(paymentStatusTxt);
		
		sdf.applyPattern(DATE_PATTERN_BILLING_PERIOD);
		String billingPeriodDates[] = billingPeriodTxt.split("-");
		waterBillingDetails.setBillingPeriodStartDate(sdf.parse(billingPeriodDates[0]));
		waterBillingDetails.setBillingPeriodEndDate(sdf.parse(billingPeriodDates[1]));
		return waterBillingDetails;
	}
	
	private void saveScreenshot(String screenshotNamePrefix) throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat(SCREENSHOT_NAME_EXT_FORMAT);
		File screenshotFile = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
		Path destScreenshotPath = Paths.get(SCREENSHOT_SAVE_DIR+screenshotNamePrefix+sdf.format(new Date())+screenshotFile.getName().substring(screenshotFile.getName().lastIndexOf(".")));
	    Path srcScreenshotPath = screenshotFile.toPath();
	    Files.copy(srcScreenshotPath, destScreenshotPath, StandardCopyOption.REPLACE_EXISTING);
	    //Desktop.getDesktop().open(destScreenshotPath.toFile());
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
	}
	
	private ArrayList<BillingDetails> populateElectricityBillList() throws Exception
	{
		ArrayList<BillingDetails> electricityBillingDetailsList = new ArrayList<BillingDetails>(); 
		HashMap<String, String> electricityBillConsumerNameToIdMap = new HashMap<String, String>();
		electricityBillConsumerNameToIdMap.put("Home", ELECTRICITY_BILL_CONTRACT_ID1);
		electricityBillConsumerNameToIdMap.put("Shop", ELECTRICITY_BILL_CONTRACT_ID2);
		
		String idContractNo = "lbl_Contract_Account_txt";
		String idIssueDate = "lbl_Issue_date_txt";
		String idDueDate = "lbl_due_date_txt";
		String idBillAmount = "lbl_Bill_Amount_txt";
		String idPaymentStatus = "lbl_Payment_Status_txt";
		
		for(String consumerId : electricityBillConsumerNameToIdMap.values())
		{
			webDriver.get(ELECTRICITY_BILL_PREVIEW_PAGE_URL+consumerId);
			waitForPageToLoad(ELECTRICITY_BILL_PREVIEW_PAGE_TITLE);
			saveScreenshot(SCREENSHOT_ELECTRICITY_BILL_PREFIX);
			
			String contractNoTxt = webDriver.findElement(By.id(idContractNo)).getText();
			String issueDateTxt = webDriver.findElement(By.id(idIssueDate)).getText();
			String dueDateTxt = webDriver.findElement(By.id(idDueDate)).getText();
			String billAmountTxt = webDriver.findElement(By.id(idBillAmount)).getText();
			String paymentStatusTxt = webDriver.findElement(By.id(idPaymentStatus)).getText();
			
			ElectricityBillingDetails electricityBillingDetails = new ElectricityBillingDetails();
			electricityBillingDetails.setContractAccNo(contractNoTxt);
			electricityBillingDetails.setBillAmount(Integer.parseInt(billAmountTxt));
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_BILLING_PERIOD);
			electricityBillingDetails.setIssueDate(sdf.parse(issueDateTxt));
			electricityBillingDetails.setDueDate(sdf.parse(dueDateTxt));
			electricityBillingDetails.setPaymentStatus(paymentStatusTxt);
			
			electricityBillingDetailsList.add(electricityBillingDetails);
		}
		return electricityBillingDetailsList;
	}
	
	private void closeBrowser() {
		webDriver.close();
	}
}
