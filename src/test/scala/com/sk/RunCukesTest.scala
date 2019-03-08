package com.sk

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("classpath:features/Order_Book.feature"),
  tags = Array("not @Wip"),
  glue = Array("classpath:/src/test/resources/com/sk/steps"),
  plugin = Array("pretty", "html:target/cucumber/html"))
class RunCukesTest

