Feature: API to manage Tags

  @REFEPTGITDIC12024-00020
  Scenario: Add tag with valid data should return 201
    Given tag name is "Work"
    When call add tag
    Then the returned tag http status is 201
    And the returned tag has name "Work"

  @REFEPTGITDIC12024-00021
  Scenario: Find tag by id should return correct tag
    Given tags table contains data:
      |id                                   |name |
      |17a281a6-0882-4460-9d95-9c28f5852db1|Work |
    When call find tag by id with id="17a281a6-0882-4460-9d95-9c28f5852db1"
    Then the returned tag http status is 200
    And the returned tag has name "Work"

  @REFEPTGITDIC12024-00022
  Scenario: Update tag with valid data should return 202
    Given tags table contains data:
      |id                                   |name |
      |17a281a6-0882-4460-9d95-9c28f5852db1|Work |
    And tag name is "Home"
    When call update tag with id="17a281a6-0882-4460-9d95-9c28f5852db1"
    Then the returned tag http status is 202
    And the returned tag has name "Home"

  @REFEPTGITDIC12024-00023
  Scenario: Delete tag by id should return 204
    Given tags table contains data:
      |id                                   |name |
      |17a281a6-0882-4460-9d95-9c28f5852db1|Work |
    When call delete tag with id="17a281a6-0882-4460-9d95-9c28f5852db1"
    Then the returned tag http status is 204

  @REFEPTGITDIC12024-00024
  Scenario: Find all tags should return correct list
    Given tags table contains data:
      |id                                   |name |
      |17a281a6-0882-4460-9d95-9c28f5852db1|Work |
      |18a281a6-0882-4460-9d95-9c28f5852db1|Home |
    When call find all tags with page = 0 and size = 10 and sort="name,asc"
    Then the returned tag http status is 200
    And the returned tags list has 2 elements
    And that tags list contains values:
      | name |
      | Home |
      | Work |

  @REFEPTGITDIC12024-00025
  Scenario: Add tag with name exceeding 50 characters should return 400
    Given tag name contains 51 characters
    When call add tag
    Then the returned tag http status is 400