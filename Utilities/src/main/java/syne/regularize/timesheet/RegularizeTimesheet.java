package syne.regularize.timesheet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class RegularizeTimesheet {

	private static enum BROWSER_TYPE {FIREFOX, CHROME, INTERNET_EXPLORER};
	private static HashMap<BROWSER_TYPE, String> browserToDriverNameMap = new HashMap<BROWSER_TYPE, String>();
	static
	{
		browserToDriverNameMap.put(BROWSER_TYPE.FIREFOX, "gecko");
		browserToDriverNameMap.put(BROWSER_TYPE.CHROME, "chrome");
	}
	
	//Select the type of browser to be used
	private static final BROWSER_TYPE BROWSER = BROWSER_TYPE.CHROME;
	
	//Selenium specific constants
	//private static final String BASE_HOME_DIR = "C:/Users/siddharth.s/";
	//private static final String DRIVER_PATH = BASE_HOME_DIR + "eclipse-workspace/artifacts/driverss/";
	private static final String BASE_HOME_DIR = System.getProperty("user.dir");
	private static final String DRIVER_PATH = BASE_HOME_DIR + "/resources/";
	private static final String DRIVER_PROPERTY_NAME = "webdriver."+browserToDriverNameMap.get(BROWSER)+".driver";
	private static final String DRIVER_EXE_NAME = browserToDriverNameMap.get(BROWSER)+"driver.exe";
	private static final String SELENIUM_LOGGER_CLASS = "org.openqa.selenium";
	
	//EAG Portal URLs
	private static final String EAG_HOMEPAGE_URL = "https://eag.synechron.com/SYNE.UI/";
	private static final String REGULARIZATION_LIST_PAGE_URL = EAG_HOMEPAGE_URL +"/Attendance/Common/Home/Index#/applications/AttendanceRegularization/List";
	
	//EAG Portal Page Titles
	private static final String TITLE_EAG_HOMEPAGE = "Enterprise Application Portal";
	private static final String TITLE_SYNETIME_APPLICATIONS_PAGE = "Applications | Attendance System";
	private static final String TITLE_SYNETIME_REGULARIZATION_PAGE = "Attendance Regularization | Attendance System";
	private static final String TITLE_SYNETIME_REGULARIZATION_LIST_PAGE = "Attendance Regularization List | Attendance System";
	private static final String TITLE_SYNETIME_ATTENDENCE_HOME = "Home | Attendance System";
	
	private static final String BTN_APPLY_TXT = "Apply";
	
	// Reason Code constants
	private static final String REASON_CARD_SUBMITTED_TO_ADMIN = "Card submitted to Admin"+Keys.ENTER;
	private static final String REASON_TRAINING = "T"+Keys.ENTER;
	
	private static final String ATTRIBUTE_VALUE = "value";
	private static final String DATE_FORMAT = "dd MMM, yyyy";
	
	//EAG Login details
	public String EAG_USERNAME = "siddharth.s";
	public String EAG_PASSWORD = "qAzxsw@123";
	private static final String REASON_FOR_REG = REASON_CARD_SUBMITTED_TO_ADMIN;
	
	private static final String[] VALID_INPUT_ARGS = new String[] {"-user","-pass","-headless"};
	
	//Configuration
	private static final long SLEEP_TIME_BETWEEN_PAGE_LOAD_CHECKS = 3000;
	private static final long SLEEP_TO_AVOID_CLICK_INTERCEPT = 1000;
	private static final int MAX_WEEKS_IN_A_MONTH = 5;
	
	//Logs Configuration
	private static final String LOG_DIR = BASE_HOME_DIR + "/logs/selenium/";
	private static final String LOG_FILE_NAME = "DriverLog";
	private static final String LOG_FILE_EXTENSION = ".txt";
	private static final String LOG_NAME_DATE_FORMAT = "yyyyMMdd_HH_mm_ss";
	
	private static final String SCREENSHOT_DIR = BASE_HOME_DIR + "/screenshots/";
	private static final String SCREENSHOT_PREFIX = "Regularization_";
	private static final boolean IS_HEADLESS = true;
	
	private WebDriver webDriver;
	
	public static void main(String[] args) throws Exception {
		System.out.println("Started.. Browser Type - "+BROWSER);
		long startTime = System.currentTimeMillis();
		RegularizeTimesheet regularizeTimesheet = new RegularizeTimesheet();
		regularizeTimesheet.handleArgs(args);
		regularizeTimesheet.regularize();
		System.out.println("Finished..");
		System.out.println("Time taken = "+(System.currentTimeMillis() - startTime)/1000+" secs");
	}

	private void regularize() throws Exception {
		openBrowser();
		loginToEag();
		handleDiscrepencies();
		verify();
		saveScreenshot();
		closeBrowser();
	}

	private void handleArgs(String[] inputArgs) {
		ArrayList<String> validInputArgsList = new ArrayList<String>(Arrays.asList(VALID_INPUT_ARGS));
		HashMap<String, String> argToFieldMap = getInputArgToFieldMap();
		
		for(int i=0;i<inputArgs.length;i++)
		{
			if(validInputArgsList.contains(inputArgs[i]))
			{
				try {
					Field fld = this.getClass().getField(argToFieldMap.get(inputArgs[i]));
					fld.setAccessible(true);
					fld.set(this, inputArgs[++i]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println(inputArgs[i]+" - Not a valid arguement");
				i++;
			}
		}
	}

	private void handleDiscrepencies() throws InterruptedException, ParseException, IOException{
		String cssSelDecrepencyCountLink = ".attendenceDiscrepancy > span:nth-child(2)"; 
		//WebDriverWait wait = new WebDriverWait(webDriver,3);
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelDecrepencyCountLink))); 
		int descrepenciesCount = Integer.valueOf(webDriver.findElement(By.cssSelector(cssSelDecrepencyCountLink)).getText());
		System.out.println("Discrepencies Count == "+descrepenciesCount);
		if(descrepenciesCount == 0)
		{
			System.out.println("No Attendence Discrepencies to be handled..");
			return;
		}
		
		String cssSelRegLink = ".listing-table > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(6) > a:nth-child(2)";
		
		String cssSelInTime08hrFromDropDown = ".ui_tpicker_hour_slider > select:nth-child(1) > option:nth-child(9)";
		String cssSelInTime10hrFromDropDown = ".ui_tpicker_hour_slider > select:nth-child(1) > option:nth-child(11)";
		String cssSelInTime00minFromDropDown = ".ui_tpicker_minute_slider > select:nth-child(1) > option:nth-child(1)";
		String cssSelOutTime16hrFromDropDown = ".ui_tpicker_hour_slider > select:nth-child(1) > option:nth-child(17)";
		String cssSelOutTime18hrFromDropDown = ".ui_tpicker_hour_slider > select:nth-child(1) > option:nth-child(19)";
		String cssSelOutTime00minFromDropDown = ".ui_tpicker_minute_slider > select:nth-child(1) > option:nth-child(1)";
		String cssSelInandOutTimeDoneBtn = ".ui-datepicker-close";
		
		String cssSelInTimeHrDropDown = cssSelInTime10hrFromDropDown;
		String cssSelOutTimeHrFromDropDown = cssSelOutTime18hrFromDropDown;
		
		if(REASON_FOR_REG.equalsIgnoreCase(REASON_TRAINING))
		{
			cssSelInTimeHrDropDown = cssSelInTime08hrFromDropDown;
			cssSelOutTimeHrFromDropDown = cssSelOutTime16hrFromDropDown;
		}	
		
		String cssSelApplyBtn = "input.buttons:nth-child(3)";

		String idCalInDate = "InDate";
		String idCalOutDate = "OutDate";
		String idReasonDropDown = "Remarks";
		String idInTimeDropDown = "fromPicker";
		String idOutTimeDropDown = "Text1";
		
		String startTextForWarningToIgnore = "You have already applied";
		String containsTextForRetry = "Datatype size excedded";
		String cssSelForToastMessage = ".toast-message";
		
		String cssSelForLinkToRegPage = "#applicationHost > div > div > section > aside > div:nth-child(2) > div > aside.homeRightBlock > div.rightBlockLeftSection > div:nth-child(1) > span";
		
		if(descrepenciesCount > 0)
		{
			webDriver.findElement(By.cssSelector(cssSelDecrepencyCountLink)).click();
			waitForPageToLoad(TITLE_SYNETIME_APPLICATIONS_PAGE);
			ArrayList<Date> discrepancyDatesList = populateListOfDiscrepencyDates();
			for(Date discrepencyDate : discrepancyDatesList)
			{
				if(webDriver.getTitle().equalsIgnoreCase(TITLE_SYNETIME_ATTENDENCE_HOME))
					webDriver.findElement(By.cssSelector(cssSelForLinkToRegPage)).click();
				
				waitForPageToLoad(TITLE_SYNETIME_APPLICATIONS_PAGE);
				webDriver.findElement(By.cssSelector(cssSelRegLink)).click();
				waitForPageToLoad(TITLE_SYNETIME_REGULARIZATION_PAGE);
				webDriver.findElement(By.id(idReasonDropDown)).click();
				webDriver.findElement(By.id(idReasonDropDown)).sendKeys(REASON_FOR_REG);
				
				webDriver.findElement(By.id(idInTimeDropDown)).click();
				webDriver.findElement(By.cssSelector(cssSelInTimeHrDropDown)).click();
				webDriver.findElement(By.cssSelector(cssSelInTime00minFromDropDown)).click();
				webDriver.findElement(By.cssSelector(cssSelInandOutTimeDoneBtn)).click();
				
				Thread.sleep(SLEEP_TO_AVOID_CLICK_INTERCEPT);
				
				webDriver.findElement(By.id(idOutTimeDropDown)).click();
				webDriver.findElement(By.cssSelector(cssSelOutTimeHrFromDropDown)).click();
				webDriver.findElement(By.cssSelector(cssSelOutTime00minFromDropDown)).click();
				webDriver.findElement(By.cssSelector(cssSelInandOutTimeDoneBtn)).click();
				
				Thread.sleep(SLEEP_TO_AVOID_CLICK_INTERCEPT);
				setDateDropDown(discrepencyDate, idCalInDate);
				Thread.sleep(SLEEP_TO_AVOID_CLICK_INTERCEPT);
				setDateDropDown(discrepencyDate, idCalOutDate);
				
				WebElement applyBtn = webDriver.findElement(By.cssSelector(cssSelApplyBtn));
				if(applyBtn.getAttribute(ATTRIBUTE_VALUE).equalsIgnoreCase(BTN_APPLY_TXT))
				{
					applyBtn.click();
					Thread.sleep(SLEEP_TIME_BETWEEN_PAGE_LOAD_CHECKS);
					try
					{
						if(webDriver.findElement(By.cssSelector(cssSelForToastMessage)).getText().contains(containsTextForRetry))
						{
							applyBtn.click();
							Thread.sleep(SLEEP_TIME_BETWEEN_PAGE_LOAD_CHECKS);
						}
						if(webDriver.findElement(By.cssSelector(cssSelForToastMessage)).getText().startsWith(startTextForWarningToIgnore))
							continue;
					}
					catch(NoSuchElementException nse)
					{
						continue;
					}
				}
				descrepenciesCount--;
			}
		}
		System.out.println("Handled All discrepencies");
	}

	private void setDateDropDown(Date discrepencyDate, String idDate) {
		boolean selectedMonth = false, selectedYear = false;
		Calendar cal = Calendar.getInstance();
		cal.setTime(discrepencyDate);

		webDriver.findElement(By.id(idDate)).click();
		
		//List<WebElement> monthRows = webDriver.findElements(By.cssSelector("[class='ui-datepicker-month'] option"));
		List<WebElement> monthRows = webDriver.findElements(By.cssSelector(".ui-datepicker-month > option"));
		for(WebElement monthRow : monthRows)
		{
			if(monthRow.isSelected() && Integer.valueOf(monthRow.getAttribute(ATTRIBUTE_VALUE)) == cal.get(Calendar.MONTH))
				selectedMonth = true;
		}
		
		List<WebElement> yearRows = webDriver.findElements(By.cssSelector(".ui-datepicker-year option"));
		for(WebElement yearRow : yearRows)
		{
			if(yearRow.isSelected() && Integer.valueOf(yearRow.getAttribute(ATTRIBUTE_VALUE)) == cal.get(Calendar.YEAR))
				selectedYear = true;
		}

		if(!selectedMonth)
		{
			WebElement monthRow = null;
			if(idDate.equalsIgnoreCase("OutDate"))
				monthRow = webDriver.findElement(By.cssSelector(".ui-datepicker-month > option:nth-child(1)"));
			else
				monthRow = webDriver.findElement(By.cssSelector(".ui-datepicker-month > option:nth-child("+(cal.get(Calendar.MONTH)+1)+")"));
			
			monthRow.click();
		}
		
		if(!selectedYear)
		{
			for(WebElement yearRow : yearRows)
			{
				if(Integer.valueOf(yearRow.getAttribute(ATTRIBUTE_VALUE)) == cal.get(Calendar.YEAR))
				{
					selectedYear = true;
					yearRow.click();
					break;
				}
			}
		}
		
		for(int i=1;i<=MAX_WEEKS_IN_A_MONTH;i++)
		{
			String cssSelDayofMonth = ".ui-datepicker-calendar > tbody:nth-child(2) > tr:nth-child("+i+") > td:nth-child("+cal.get(Calendar.DAY_OF_WEEK)+") > a:nth-child(1)";
			try 
			{
				String dayOfMonthText = webDriver.findElement(By.cssSelector(cssSelDayofMonth)).getText();
				if(dayOfMonthText != null && !dayOfMonthText.trim().equalsIgnoreCase("") && Integer.valueOf(dayOfMonthText) == cal.get(Calendar.DAY_OF_MONTH))
					webDriver.findElement(By.cssSelector(cssSelDayofMonth)).click();
			}
			catch(NoSuchElementException nse)
			{
				continue;
			}
		}
	}

	private ArrayList<Date> populateListOfDiscrepencyDates() throws ParseException {
		ArrayList<Date> discrepancyDatesList = new ArrayList<Date>();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String cssSelListingTableRows = "[class='listing-table'] tr";
		int discrepencyRowsCount = webDriver.findElements(By.cssSelector(cssSelListingTableRows)).size();
		for(int i=2; i<=discrepencyRowsCount; i++)
		{
			String cssSelDescrepencyDate = ".listing-table > tbody:nth-child(1) > tr:nth-child("+i+") > td:nth-child(1)";
			WebElement discrepencyDateColumn = webDriver.findElement(By.cssSelector(cssSelDescrepencyDate));
			Date date = sdf.parse(discrepencyDateColumn.getText());
			discrepancyDatesList.add(date);
		}
		return discrepancyDatesList;
	}

	private void loginToEag() throws InterruptedException, WebDriverException, IOException
	{
		String idUserName = "UserName";
		String idPassword = "Password";
		String classNameSignInBtn = "signInBtn";
		webDriver.findElement(By.id(idUserName)).clear();
		webDriver.findElement(By.id(idUserName)).sendKeys(EAG_USERNAME);
		webDriver.findElement(By.id(idPassword)).clear();
		webDriver.findElement(By.id(idPassword)).sendKeys(EAG_PASSWORD);
		webDriver.findElement(By.className(classNameSignInBtn)).click();
		checkForInvalidLogin();
		waitForPageToLoad(TITLE_EAG_HOMEPAGE);
		System.out.println("Login Successful : user - "+EAG_USERNAME);
	}
	
	private void checkForInvalidLogin() throws InterruptedException, WebDriverException, IOException{
		Thread.sleep(SLEEP_TIME_BETWEEN_PAGE_LOAD_CHECKS);
		String cssSelForInvalidLoginText = "body > div.container.newLogOn > form > div > div.loginaccess > span";
		String INVALID_LOGIN_MESSAGE = "Invalid Username / Password. Please try again";
		try 
		{
			if(webDriver.getTitle().equalsIgnoreCase(TITLE_EAG_HOMEPAGE) &&
					INVALID_LOGIN_MESSAGE.equalsIgnoreCase(webDriver.findElement(By.cssSelector(cssSelForInvalidLoginText)).getText()))
			{
				System.out.println("Invalid login details : user - "+EAG_USERNAME+", pass - "+EAG_PASSWORD);
				//Desktop.getDesktop().open(((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE));
				Thread.sleep(1000);
				System.exit(-1);
			}
		}
		catch(NoSuchElementException nse)
		{}
	}

	private void waitForPageToLoad(String pageTitle) throws InterruptedException
	{
		while(true)
		{
			if(webDriver.getTitle().equalsIgnoreCase(pageTitle))
				return;
			Thread.sleep(SLEEP_TIME_BETWEEN_PAGE_LOAD_CHECKS);
		}
	}
	
	private void openBrowser()
	{
		SimpleDateFormat logFileNameDateFormat = new SimpleDateFormat(LOG_NAME_DATE_FORMAT);
		Logger.getLogger(SELENIUM_LOGGER_CLASS).setLevel(Level.OFF);
		System.setProperty(DRIVER_PROPERTY_NAME, DRIVER_PATH+DRIVER_EXE_NAME);
		switch(BROWSER)
		{
			case FIREFOX :
			{
				FirefoxBinary firefoxBinary = new FirefoxBinary();
				firefoxBinary.addCommandLineOptions("--disable-logging");
			    firefoxBinary.addCommandLineOptions("--headless");
			    FirefoxOptions firefoxOptions = new FirefoxOptions();
			    firefoxOptions.setBinary(firefoxBinary);
			   
				System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,
										LOG_DIR+BROWSER+"_"+LOG_FILE_NAME+"_"+logFileNameDateFormat.format(new Date())+LOG_FILE_EXTENSION);
				
				webDriver = new FirefoxDriver(firefoxOptions);
				break;
			}
			
			case CHROME :
			{
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.setHeadless(IS_HEADLESS);
				chromeOptions.addArguments("--start-maximized");
			    System.setProperty("webdriver.chrome.silentOutput", "true");
			    //System.setProperty("webdriver.chrome.logfile",  LOG_DIR+ BROWSER+"_"+LOG_FILE_NAME+"_"+logFileNameDateFormat.format(new Date())+LOG_FILE_EXTENSION);
			    //System.setProperty("webdriver.chrome.verboseLogging", "true");

				webDriver = new ChromeDriver(chromeOptions);
				break;
			}
			
			default:
			{
				System.err.println("Currently Browser Type: <"+BROWSER+"> is not supported..");
				System.exit(-1);
			}
		}
		//webDriver.manage().window().maximize();
		webDriver.get(EAG_HOMEPAGE_URL);
	}
	
	private void verify() throws InterruptedException, IOException{
		webDriver.get(REGULARIZATION_LIST_PAGE_URL);
		waitForPageToLoad(TITLE_SYNETIME_REGULARIZATION_LIST_PAGE);
        //Desktop.getDesktop().open(((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE));
	}

	private void closeBrowser() {
		webDriver.close();
	}

	private HashMap<String, String> getInputArgToFieldMap()
	{
		HashMap<String, String> argToFieldMap = new HashMap<String, String>();
		argToFieldMap.put(VALID_INPUT_ARGS[0], "EAG_USERNAME");
		argToFieldMap.put(VALID_INPUT_ARGS[1], "EAG_PASSWORD");
		argToFieldMap.put(VALID_INPUT_ARGS[2], "IS_HEADLESS");
		//argToFieldMap.put("-browser", "BROWSER");
		
		return argToFieldMap;
	}
	
	private void saveScreenshot() throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat(LOG_NAME_DATE_FORMAT);
		File screenshotFile = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
		Path destScreenshotPath = Paths.get(SCREENSHOT_DIR+SCREENSHOT_PREFIX+sdf.format(new Date())+screenshotFile.getName().substring(screenshotFile.getName().lastIndexOf(".")));
	    Path srcScreenshotPath = screenshotFile.toPath();
	    if(!new File(SCREENSHOT_DIR).exists())
	    	new File(SCREENSHOT_DIR).mkdirs();
	    Files.copy(srcScreenshotPath, destScreenshotPath, StandardCopyOption.REPLACE_EXISTING);
	}
}
