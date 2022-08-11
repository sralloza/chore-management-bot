package bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static constants.Messages.COMPLETE_TASK;
import static constants.Messages.SKIP;
import static constants.Messages.TASKS;
import static constants.Messages.TICKETS;
import static constants.Messages.TRANSFER;
import static constants.Messages.UNSKIP;


public class Keyboards {
    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(SKIP);
        row.add(TASKS);
        row.add(COMPLETE_TASK);

        keyboard.add(row);
        row = new KeyboardRow();
        row.add(UNSKIP);
        row.add(TICKETS);
        row.add(TRANSFER);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setInputFieldPlaceholder("Type your message");
        keyboardMarkup.setResizeKeyboard(true);

        return keyboardMarkup;
    }
}
