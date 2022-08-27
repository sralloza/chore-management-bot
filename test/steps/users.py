from behave import *
from toolium.utils import dataset

from common.api import request


@step("I am a tenant")
def step_impl(context):
    context.execute_steps("When I use the admin token")
    res = request(
        context,
        "http://chore-management-api:8080/v1/tenants",
        "POST",
        json={"username": "qa.user", "tenant_id": context.telegram_creator_id},
    )
    if not res.ok:
        raise Exception(
            {"request-payload": res.request.body, "response-payload": res.text}
        )


@step("I create {n:d} more tenants")
def step_impl(context, n):
    tenants = dataset.project_config["extra_tenants"] = []
    for i in range(n):
        context.execute_steps("When I use the admin token")
        res = request(
            context,
            "http://chore-management-api:8080/v1/tenants",
            "POST",
            json={"username": f"user.{i}", "tenant_id": 10**5 + i},
        )
        if not res.ok:
            raise Exception(
                {"request-payload": res.request.body, "response-payload": res.text}
            )
        tenants.append(res.json()["tenant_id"])
