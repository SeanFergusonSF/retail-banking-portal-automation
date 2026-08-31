package com.bankportal.ui.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features/accessibility",
        glue = "com.bankportal.ui.steps",
        tags = "@accessibility",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/accessibility-report.html",
                "json:target/cucumber-reports/accessibility-report.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class AccessibilityTestRunner extends AbstractTestNGCucumberTests {
}