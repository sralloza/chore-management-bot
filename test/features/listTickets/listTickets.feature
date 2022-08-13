@listTickets
Feature: List tickets
    As a tenant
    I want to list the tickets


    @authorization
    Scenario: Guest users can't use the bot
        When I send the message "Tickets" to the bot
        Then the bot sends the message "You don't have permission to execute this action"


    Scenario: List tickets with only one tenant
        Given I am a tenant
        And I create 1 chore type
        And I create the tasks for the week "2030.01"
        When I send the message "Tickets" to the bot
        Then the bot sends the image "tickets/singleTenant"


    Scenario: List tickets with multiple tenants
        Given I am a tenant
        And I create 2 more tenants
        And I create 3 chore types
        And I create the tasks for the week "2030.01"
        When I send the message "Tickets" to the bot
        Then the bot sends the image "tickets/multipleTenants"


    Scenario: List tickets when db is empty
        Given I am a tenant
        When i send the message "Tickets" to the bot
        Then the bot sends the message "No tickets found"
