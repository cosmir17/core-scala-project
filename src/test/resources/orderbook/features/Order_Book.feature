Feature: Order Book Feature
  Scenario: Should produce the most recent order, Normal case
    Given The following order events are made
      |instruction |side |price_level_index |price |quantity
      |N           |B    |1                 |5     |30
      |N           |B    |2                 |4     |40
      |N           |A    |1                 |6     |10
      |N           |A    |2                 |7     |10
      |U           |A    |2                 |7     |20
      |U           |B    |1                 |5     |40
    When the main app runs with tick size 10.0 and book depth 2
    Then the following should be printed
      | Bid_price | Bid_quantity | ask_price | ask_quantity
      | 50.0      | 40           | 60.0      | 10
      | 40.0      | 40           | 70.0      | 20