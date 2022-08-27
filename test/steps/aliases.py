from random import randint
from string import ascii_uppercase

from behave import *
from toolium.utils.dataset import map_param

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
                {
                    "request-url": res.request.url,
                    "request-payload": res.request.body,
                    "response-payload": res.text,
                }
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
            {
                "request-url": res.request.url,
                "request-payload": res.request.body,
                "response-payload": res.text,
            }
        )


@step("I create the tasks for the following weeks")
def step_impl(context):
    context.table.require_columns(["week_id"])
    for row in context.table:
        week_id = row["week_id"]
        context.execute_steps(f'When I create the tasks for the week "{week_id}"')


@step('I skip the week with id "{week_id}"')
def step_impl(context, week_id):
    tenant_id = context.telegram_creator_id
    row_id = randint(1, 10**8)

    execute_query(
        "INSERT INTO skipped_weeks (id, week_id, tenant_id) VALUES (%s, %s, %s)",
        (row_id, week_id, tenant_id),
        commit=True,
    )


@step('I complete the task "{chore_type}" for the week "{week_id}"')
def step_impl(context, chore_type, week_id):
    res = request(
        context,
        f"http://chore-management-api:8080/v1/weekly-chores/{week_id}/choreType/{chore_type}/complete",
        "POST",
    )
    if not res.ok:
        raise Exception(
            {
                "request-url": res.request.url,
                "request-payload": res.request.body,
                "response-payload": res.text,
            }
        )


@step("I complete the following tasks")
def step_impl(context):
    context.table.require_columns(["chore_type", "week_id"])
    for row in context.table:
        week_id = row["week_id"]
        chore_type = row["chore_type"]
        context.execute_steps(
            f'When I complete the task "{chore_type}" for the week "{week_id}"'
        )


@step("I transfer a chore")
def step_impl(context):
    context.table.require_columns(
        ["tenant_id_from", "tenant_id_to", "chore_type", "week_id"]
    )
    context.execute_steps("Given I use the admin token")
    for row in context.table:
        tenant_id_from = map_param(row["tenant_id_from"])
        tenant_id_to = map_param(row["tenant_id_to"])
        chore_type = row["chore_type"]
        week_id = row["week_id"]

        res = request(
            context,
            f"http://chore-management-api:8080/v1/transfers/start",
            "POST",
            json={
                "tenant_id_from": tenant_id_from,
                "tenant_id_to": tenant_id_to,
                "chore_type": chore_type,
                "week_id": week_id,
            },
        )
        if not res.ok:
            raise Exception(
                {
                    "request-url": res.request.url,
                    "request-payload": res.request.body,
                    "response-payload": res.text,
                }
            )

        transfer_id = res.json()["id"]
        res = request(
            context,
            f"http://chore-management-api:8080/v1/transfers/{transfer_id}/accept",
            "POST",
        )
        if not res.ok:
            raise Exception(
                {
                    "request-url": res.request.url,
                    "request-payload": res.request.body,
                    "response-payload": res.text,
                }
            )


@step('the tenant "{tenant_id}" skips the week "{week_id}"')
def step_impl(context, tenant_id, week_id):
    context.execute_steps("Given I use the admin token")

    tenant_id = map_param(tenant_id)
    res = request(
        context,
        f"http://chore-management-api:8080/v1/tenants/{tenant_id}/skip/{week_id}",
        "POST",
        json={"tenant_id": tenant_id},
    )
    if not res.ok:
        raise Exception(
            {
                "request-url": res.request.url,
                "request-payload": res.request.body,
                "response-payload": res.text,
            }
        )
