import logging
from base64 import b64decode
from pathlib import Path

import allure
from behave import fixture, use_fixture
from telethon import TelegramClient
from toolium.behave.environment import after_all as tlm_after_all
from toolium.behave.environment import after_feature as tlm_after_feature
from toolium.behave.environment import after_scenario as tlm_after_scenario
from toolium.behave.environment import before_all as tlm_before_all
from toolium.behave.environment import before_feature as tlm_before_feature
from toolium.behave.environment import before_scenario as tlm_before_scenario
from toolium.utils import dataset

from common.ansible import get_environment
from common.db import reset_databases

logging.disable(logging.CRITICAL)


def before_all(context):
    tlm_before_all(context)
    context.admin_token = "bc6acdd7-9de0-495f-86ea-20beda48d626"


def before_feature(context, feature):
    tlm_before_feature(context, feature)
    context.api = Path(feature.filename).parent.name
    context.resource = feature.name.split(" - ")[-1]


def get_dataset():
    dataset = {}

    return dataset


def before_scenario(context, scenario):
    tlm_before_scenario(context, scenario)
    context.storage = {}
    check_naming(scenario)

    dataset.project_config = get_dataset()

    reset_databases()
    use_fixture(telegram_client, context)


@fixture
def telegram_client(context):
    env = get_environment()

    context.token = None
    context.bot_username = env["bot_username"]

    api_id = env["api_id"]
    api_hash = env["api_hash"]
    server_ip = env["server_ip"]
    server_port = env["server_port"]
    server_id = env["server_id"]
    phone = env["phone"]
    session_file = env["session_file"]
    context.telegram_creator_id = env["telegram_creator_id"]
    context.admin_token = env["admin_token"]

    Path("qa.session").write_bytes(b64decode(session_file.encode()))

    client = TelegramClient("qa", api_id, api_hash)
    client.session.set_dc(server_id, server_ip, server_port)
    context.client = client

    async def main():
        await client.start(phone=phone)
        authorized = await client.is_user_authorized()
        if not authorized:
            print("WARNING: NOT AUTHORIZED")

    client.loop.run_until_complete(main())

    yield client

    client.disconnect()


def register_allure_stdout_stderr(context):
    stdout = None if not context.stdout_capture else context.stdout_capture.getvalue()
    stderr = None if not context.stderr_capture else context.stderr_capture.getvalue()
    logs = None if not context.log_capture else context.log_capture.getvalue()

    if stdout:
        allure.attach(
            stdout, name="stdout", attachment_type=allure.attachment_type.TEXT
        )
    if stderr:
        allure.attach(
            stderr, name="stderr", attachment_type=allure.attachment_type.TEXT
        )
    if logs:
        allure.attach(logs, name="logs", attachment_type=allure.attachment_type.TEXT)


def after_scenario(context, scenario):
    register_allure_stdout_stderr(context)
    tlm_after_scenario(context, scenario)


def after_feature(context, feature):
    tlm_after_feature(context, feature)


def after_all(context):
    tlm_after_all(context)

    session_path = Path("qa.session")
    if session_path.is_file():
        session_path.unlink()


def check_naming(scenario):
    scenario_name = scenario.name
    if not scenario_name[0].isupper():
        name = scenario_name[0].upper() + scenario_name[1:]
        msg = f"Scenario name should be titled ({name})"
        raise AssertionError(msg)
    if "validate error" in scenario_name.lower():
        assert "validate error response" in scenario_name.lower()
