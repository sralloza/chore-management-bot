@listTasks
Feature: List tasks
    As a tenant
    I want to list the tasks


    @authorization
    Scenario: Guest users can't use the bot
        When I send the message "Tasks" to the bot
        Then the bot sends the message "You don't have permission to execute this action"


    Scenario: List tasks with only one tenant
        Given I am a user
        And I create 1 chore type
        And I create the tasks for the week "2030.01"
        When I send the message "Tasks" to the bot
        Then the bot sends the image "tasks/1_week_1_tenant"


    Scenario: List tasks with one task and multiple tenants
        Given I am a user
        And I create 2 more users
        And I create 3 chore types
        And I create the tasks for the week "2030.01"
        When I send the message "Tasks" to the bot
        Then the bot sends the image "tasks/1_week_3_tenants"


    Scenario: List tasks with multiple tasks and multiple tenants
        Given I am a user
        And I create 2 more users
        And I create 3 chore types
        And I create the tasks for the following weeks
            | week_id |
            | 2030.01 |
            | 2030.02 |
            | 2030.03 |
        When I send the message "Tasks" to the bot
        Then the bot sends the image "tasks/3_weeks_3_tenants"


    Scenario: List tasks with multiple mixed tasks and multiple tenants
        Given I am a user
        And I create 2 more users
        And I create 3 chore types
        And the tenant "[CONTEXT:telegram_creator_id]" skips the week "2030.01"
        And I create the tasks for the following weeks
            | week_id |
            | 2030.01 |
            | 2030.02 |
            | 2030.03 |
        And I complete the following tasks
            | chore_type | week_id |
            | B          | 2030.01 |
            | A          | 2030.02 |
            | B          | 2030.02 |
            | C          | 2030.02 |
            | A          | 2030.03 |
            | C          | 2030.03 |
        When I send the message "Tasks" to the bot
        Then the bot sends the image "tasks/3_weeks_3_tenants_mixed"


    Scenario: List tasks when db is empty
        Given I am a user
        When i send the message "Tasks" to the bot
        Then the bot sends the message "No tasks found"
