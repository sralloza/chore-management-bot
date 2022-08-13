from random import randint
from string import ascii_uppercase

from behave import *

from common.api import request
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


@step("I create {n:d} chore type")
@step("I create {n:d} chore types")
def step_impl(context, n):
    for i in range(n):
        context.execute_steps("Given I use the admin token")

        chore_id = ascii_uppercase[i]
        res = request(
            context,
            "http://chore-management-api:8080/v1/chore-types",
            "POST",
            json={"id": chore_id, "description": f"{chore_id}-description"},
        )
        if not res.ok:
            raise Exception(
                {"request-payload": res.request.body, "response-payload": res.text}
            )


@step('I create the tasks for the week "{week_id}"')
def step_impl(context, week_id):
    res = request(
        context,
        f"http://chore-management-api:8080/v1/weekly-chores/{week_id}",
        "POST",
    )
    if not res.ok:
        raise Exception(
            {"request-payload": res.request.body, "response-payload": res.text}
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
