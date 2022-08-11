from telethon.tl.types import KeyboardButton, ReplyKeyboardMarkup


def parse_keyboard(keyboard: ReplyKeyboardMarkup):
    return [[x.text for x in row.buttons] for row in keyboard.rows]


def parse_button(button: KeyboardButton):
    return button.text
