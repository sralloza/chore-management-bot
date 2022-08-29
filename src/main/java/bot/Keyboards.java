package bot;

import constants.UserMessages;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;


public class Keyboards {
    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(UserMessages.SKIP);
        row.add(UserMessages.TASKS);
        row.add(UserMessages.COMPLETE_TASK);

        keyboard.add(row);
        row = new KeyboardRow();
        row.add(UserMessages.UNSKIP);
        row.add(UserMessages.TICKETS);
        row.add(UserMessages.TRANSFER);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setInputFieldPlaceholder("Type your message");
        keyboardMarkup.setResizeKeyboard(true);

        return keyboardMarkup;
    }
}
