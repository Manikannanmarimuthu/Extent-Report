package com.qa.extentreport;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportTest {

	public ExtentHtmlReporter htmlReporter;
	public ExtentReports extent;
	public ExtentTest extentTest;
	public WebDriver driver;

	@BeforeClass
	public void beforeClass() {
		htmlReporter = new ExtentHtmlReporter("./reports/extent.html");
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setDocumentTitle("Selenium Automation Report");
		htmlReporter.config().setReportName("Automation Test Results");
		htmlReporter.config().setTheme(Theme.STANDARD);

		extent = new ExtentReports();
		extent.setSystemInfo("Organization", "MVI Technologies");
		extent.setSystemInfo("Browser", "Chrome");
		extent.attachReporter(htmlReporter);

		System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://www.mvitech.com");
	}

	@Test
	public void onTestPass() {
		extentTest = extent.createTest("Successful test");
	}

	@Test
	public void onTestFail() {
		extentTest = extent.createTest("Failed test");
		Assert.fail("Executing Failed Test method");
	}

	@Test
	public void onTestSkip() {
		extentTest = extent.createTest("Skipped test");
		throw new SkipException("Skipped Test Method");
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		String methodName = result.getMethod().getMethodName();
		if (result.getStatus() == ITestResult.FAILURE) {
			String exceptionMessage = Arrays.toString(result.getThrowable().getStackTrace());
		
			extentTest.fail("<details><summary><b><font color=red>Exception Occurred, Click to see the details:"
					+ "</font></b></summary>" + exceptionMessage.replaceAll(",", "<br>") + "</details> \n");
			String path = takeScreenshot(result.getMethod().getMethodName());
			
			try {
				extentTest.fail("<b><font color=red>" + "Scrrenshot of failiure " + "</font></b>",
						MediaEntityBuilder.createScreenCaptureFromPath(path).build());
			} catch (IOException e) {
				extentTest.fail("Tets Failed, Cannot attach Screenshot");
			}
			
			String logText = "<b> Test Method" + methodName + "Failed</b>";
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.RED);
			extentTest.log(Status.FAIL, m);
		}

		else if (result.getStatus() == ITestResult.SUCCESS) {
			String logText = "<b> Test Method" + methodName + "Success</b>";
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
			extentTest.log(Status.PASS, m);
		}

		else if (result.getStatus() == ITestResult.SKIP) {
			String logText = "<b>Test Method" + methodName + "Skipped</b>";
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.YELLOW);
			extentTest.log(Status.SKIP, m);
		}

	}

	public String takeScreenshot(String methodName) {
		String fileName = getScrrenshotName(methodName);
		String directory = System.getProperty("user.dir") + "/screenshot/";
		new File(directory).mkdirs();
		String path = directory + fileName;
		try {
			File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenShot, new File(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	public String getScrrenshotName(String methodName) {
		Date d = new Date();
		String fileName = methodName + "_" + d.toString().replace(":", "_").replace(" ", "_") + ".png";
		return fileName;
	}

	@AfterClass
	public void afterClass() {
		driver.quit();
		extent.flush();
	}

}
