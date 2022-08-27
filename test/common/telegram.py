from telethon.tl.custom.conversation import Conversation
from telethon.tl.types import KeyboardButton, ReplyKeyboardMarkup


def parse_keyboard(keyboard: ReplyKeyboardMarkup):
    return [[x.text for x in row.buttons] for row in keyboard.rows]


def parse_button(button: KeyboardButton):
    return button.text


def get_conversation(context, timeout=5) -> Conversation:
    return context.client.conversation(context.bot_username, timeout=timeout)
