from json import loads
from pathlib import Path
from uuid import uuid4

from behave import *
from behave.api.async_step import async_run_until_complete
from hamcrest import assert_that, equal_to
from telethon.errors.rpcerrorlist import MessageIdInvalidError

from common.telegram import get_conversation, parse_keyboard


@step('I send the message "{msg}" to the bot')
@async_run_until_complete
async def step_impl(context, msg):
    async with get_conversation(context) as conv:
        context.msg = await conv.send_message(msg)
        context.res = await conv.get_response()


@step('I reply to the bot\'s message with the text "{msg}"')
@async_run_until_complete
async def step_impl(context, msg):
    async with get_conversation(context) as conv:
        context.msg = await conv.send_message(msg, reply_to=context.res)
        context.res = await conv.get_response()


@step('the bot sends the message "{msg}"')
def step_impl(context, msg):
    assert_that(context.res.raw_text, equal_to(msg))


@step('the bot sends the message "{msg}" with the keyboard')
def step_impl(context, msg):
    context.execute_steps(
        f"""
    Then the bot sends the message "{msg}"
    """
    )

    expected = loads(context.text)
    keyboard = context.res.reply_markup
    actual = parse_keyboard(keyboard)

    assert_that(actual, equal_to(expected))


@step('the bot sends the image "{img}"')
@async_run_until_complete
async def step_impl(context, img):
    expected_path = Path(__file__).parent.parent / f"settings/photos/{img}.png"
    # if not expected_path.exists():
    #     raise FileNotFoundError(f"File {expected_path} does not exist")

    received_path = Path(__file__).with_name(uuid4().hex + ".png")
    await context.client.download_media(context.res.media, received_path)

    real_bytes = received_path.read_bytes()

    expected_bytes = expected_path.read_bytes()

    if real_bytes != expected_bytes:
        raise AssertionError(f"Images not equal: {expected_path} != {received_path}")

    received_path.unlink()


@step('I send the inline query "{inline_query}"')
@async_run_until_complete
async def step_impl(context, inline_query):
    buttons = context.res.buttons
    buttons = [button for row in buttons for button in row]

    for button in buttons:
        if button.text == inline_query:
            async with get_conversation(context) as conv:
                context.last_button = button
                result = await button.click()
                assert result is not None
                context.res = await conv.get_response(message=context.res)
                return

    raise ValueError("Inline query not found")


@step("I can't click the last clicked button")
@async_run_until_complete
async def step_impl(context):
    try:
        await context.last_button.click()
    except MessageIdInvalidError:
        pass
    else:
        raise ValueError("Button click did not raise an error")
