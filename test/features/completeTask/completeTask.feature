@completeTask
Feature: Complete task
    As a user
    I want to complete a task


    @authorization
    Scenario: Guest users can't use the bot
        When I send the message "Complete task" to the bot
        Then the bot sends the message "You don't have permission to execute this action"


    Scenario Outline: Complete task happy path
        Given I am a user
        And I create 2 more users
        And I create 3 chore types
        And I create the tasks for the week "<week_id>"
        When I send the message "Complete task" to the bot
        Then the bot sends the message "Select task to complete"
        And I send the inline query "<week_id> - <task_type>"
        Then the bot sends the message "Select task to complete"
        # TODO: bot should edit the "Select task to complete" message to "Task completed"
        # Then the bot sends the message "Task completed"
        And I can't click the last clicked button

        Examples: week_id = <week_id>, task_type = <task_type>
            | week_id | task_type |
            | 2030.01 | C         |
