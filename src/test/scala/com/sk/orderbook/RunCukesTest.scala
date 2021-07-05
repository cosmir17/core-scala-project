package com.sk.orderbook

import io.cucumber.junit.{Cucumber, CucumberOptions}
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("classpath:/orderbook/features/Order_Book.feature"),
  tags = "not @Wip",
  glue = Array("classpath:/src/test/com/sk/orderbook/steps"),
  plugin = Array("pretty", "html:target/cucumber/html"))
class RunCukesTest
