from random import randint

from behave import *

from common.db import execute_query


@step("the bot returns the menu")
@step('the bot returns the menu with text "{text}"')
def step_impl(context, text=None):
    text = "API connected" if text is None else text

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


@step('I skip the week with id "{week_id}"')
def step_impl(context, week_id):
    tenant_id = context.telegram_creator_id
    row_id = randint(1, 10**8)

    execute_query(
        "INSERT INTO skipped_weeks (id, week_id, tenant_id) VALUES (%s, %s, %s)",
        (row_id, week_id, tenant_id),
        commit=True,
    )
