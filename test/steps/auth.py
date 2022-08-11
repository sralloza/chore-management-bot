from behave import *


@step("I use the admin token")
def step_impl(context):
    context.token = context.admin_token
