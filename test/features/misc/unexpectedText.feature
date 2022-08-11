@unexpectedText
Feature: Unexpected text

    As a user
    I want to receive the menu if I send an unexpected text to the bot


    @authorization
    Scenario: Guest users can't use the bot
        When I send the message "Skip" to the bot
        Then the bot sends the message "You don't have permission to execute this action"


    Scenario Outline: Send an unexpected text to the bot
        Given I am a tenant
        When I send the message "<text>" to the bot
        Then the bot returns the menu with text "Undefined command"

        Examples: text = <text>
            | text     |
            | Hi       |
            | _        |
