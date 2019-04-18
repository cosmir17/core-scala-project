package com.sk.orderbook

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("classpath:/orderbook/features/Order_Book.feature"),
  tags = Array("not @Wip"),
  glue = Array("classpath:/src/test/com/sk/orderbook/steps"),
  plugin = Array("pretty", "html:target/cucumber/html"))
class RunCukesTest

