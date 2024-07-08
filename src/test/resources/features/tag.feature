Feature: API to manage Tags

  @CreateTag
  Scenario: add tag with valid data should return 200
    Given tag name is "Work"
    When call add tag
    Then the returned http status is 200
    And the returned tag has name "Work"

  @ReadTag
  Scenario: find tag by id should return correct tag
    Given tags table contains data:
      |id                                  |name  |
      |17a281a6-0882-4460-9d95-9c28f5852db1|Work  |
    When call find tag by id with id="17a281a6-0882-4460-9d95-9c28f5852db1"
    Then the returned http status is 200
    And the returned tag has name "Work"

  @UpdateTag
  Scenario: update tag with valid data should return 202
    Given tags table contains data:
      |id                                  |name  |
      |17a281a6-0882-4460-9d95-9c28f5852db1|Work  |
    And tag name is "Home"
    When call update tag with id="17a281a6-0882-4460-9d95-9c28f5852db1"
    Then the returned http status is 202
    And the returned tag has name "Home"

  @DeleteTag
  Scenario: delete tag by id should return 200
    Given tags table contains data:
      |id                                  |name  |
      |17a281a6-0882-4460-9d95-9c28f5852db1|Work  |
    When call delete tag with id="17a281a6-0882-4460-9d95-9c28f5852db1"
    Then the returned http status is 200
