package com.kdob.resourceservice.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:features",
    glue = "com.kdob.resourceservice.component",
    plugin = {"pretty", "html:target/cucumber-reports"}
)
public class CucumberTest {
}