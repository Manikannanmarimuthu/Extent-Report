package com.qa.extentreport;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
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
		extentTest.log(Status.PASS, "Successfull Test Method");
	}

	@Test
	public void onTestFail() {
		extentTest = extent.createTest("Failed test");
		extentTest.log(Status.FAIL, "Failed Test Method");
		Assert.fail("Executing Failed Test method");
	}

	@Test
	public void onTestSkip() {
		extentTest = extent.createTest("Skipped test");
		extentTest.log(Status.SKIP, "Skipped Test Method");
		throw new SkipException("Skipped Test Method");
	}

	@AfterClass
	public void afterClass() {
		driver.quit();
		extent.flush();
	}

}
