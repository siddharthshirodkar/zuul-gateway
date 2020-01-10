package global.utils;

import org.openqa.selenium.WebDriver;

public class GlobalContext {
	
	private static GlobalContext _INSTANCE = null;

	private WebDriver webDriver = null;

	public static GlobalContext getInstance()
	{
		if(_INSTANCE == null)
			_INSTANCE = new GlobalContext();
		
		return _INSTANCE; 
	}
	
	public WebDriver getWebDriver() {
		return webDriver;
	}

	public void setWebDriver(WebDriver webDriver) {
		this.webDriver = webDriver;
	}
}
