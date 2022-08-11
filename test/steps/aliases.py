from behave import *


@step("the bot returns the menu")
@step('the bot returns the menu with text "{text}"')
def step_impl(context, text=None):
    text  = "API connected" if text is None else text

    context.execute_steps(
        f"""
    Then The bot sends the message "{text}" with the keyboard
            '''
            [
                [
                    "Skip",
                    "Tasks",
                    "Complete task"
                ],
                [
                    "Unskip",
                    "Tickets",
                    "Transfer"
                ]
            ]
            '''
    """
    )
