@command.start
@startCommand
Feature: Start command

    As a user
    I want to start a conversation with the bot

    @authorization
    Scenario: Guest users can't use the bot
        When I send the message "Skip" to the bot
        Then the bot sends the message "You don't have permission to execute this action"


    Scenario: Send start command to the bot
        Given I am a tenant
        When I send the message "/start" to the bot
        Then the bot returns the menu
