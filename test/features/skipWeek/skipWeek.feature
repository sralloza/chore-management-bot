@skipWeek
Feature: Skip week
    As a user
    I want to skip a week


    @authorization
    Scenario: Guest users can't use the bot
        When I send the message "Skip" to the bot
        Then the bot sends the message "You don't have permission to execute this action"


    Scenario Outline: Skip week happy path
        Given I am a tenant
        When I send the message "Skip" to the bot
        And I reply to the bot's message with the text "<week_id>"
        Then the bot sends the message "Week skipped: <week_id>"

        Examples:
            | week_id |
            | 2030.01 |
            | 2031.30 |
            | 2031.50 |


    Scenario Outline: Validate bot response skipping past week
        Given I am a tenant
        When I send the message "Skip" to the bot
        And I reply to the bot's message with the text "<week_id>"
        Then the bot sends the message "Error: Cannot skip a week in the past"

        Examples:
            | week_id |
            | 2000.01 |
            | 2015.01 |
