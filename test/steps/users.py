from behave import *
from toolium.utils import dataset

from common.api import request


@step("I am a user")
def step_impl(context):
    context.execute_steps("When I use the admin token")
    res = request(
        context,
        "http://chore-management-api:8080/api/v1/users",
        "POST",
        json={"username": "qa.user", "id": context.telegram_creator_id},
    )
    if not res.ok:
        raise Exception(
            {"request-payload": res.request.body, "response-payload": res.text}
        )


@step("I create {n:d} more users")
def step_impl(context, n):
    for i in range(n):
        context.execute_steps("When I use the admin token")
        res = request(
            context,
            "http://chore-management-api:8080/api/v1/users",
            "POST",
            json={"username": f"user.{i}", "id": 10**5 + i},
        )
        if not res.ok:
            raise Exception(
                {"request-payload": res.request.body, "response-payload": res.text}
            )
