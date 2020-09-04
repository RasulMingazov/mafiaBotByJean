import DBs.DataBaseGames;
import DBs.DataBaseUsers;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private static String adminId = "";
    String messageText;
    String chatId;
    DataBaseUsers dBUsers;
    DataBaseGames dBGames;

    @Override
    public void onUpdateReceived(Update update) {
        messageText = update.getMessage().getText();
        chatId = update.getMessage().getChatId().toString();
        dBUsers = new DataBaseUsers(update.getMessage().getChat().getId(), update.getMessage().getChat().getUserName());

        if (!dBUsers.existanceOfUser()) {
            sendMesg(adminId, dBUsers.writeNewUser(), 0);
        }

        switch (messageText) {
            case "/start": {
                sendMesg(chatId, "Выберите язык.", 1);
                sendMesg(chatId, "Choose a language.",1);
                break;
            }
            case "Русский":
            case "English": {
                dBUsers.setLanguage(messageText);
                if (dBUsers.giveMeLanguage().equals("English"))
                    sendMesg(chatId, "You set the language.", 3);
                if (dBUsers.giveMeLanguage().equals("Русский"))
                    sendMesg(chatId, "Язык установлен.", 2);
                break;
            }
            case "Новая игра":
            case "New game": {

                if (dBUsers.giveMeLanguage().equals("English"))
                    sendMesg(chatId, "Enter the number of players.", 0);
                if (dBUsers.giveMeLanguage().equals("Русский"))
                    sendMesg(chatId, "Введите количество игроков.", 0);
                dBUsers.makeNewGame(true);
                break;
            }
            case "Вступить в игру":
            case "Join into the game": {
                if (dBUsers.giveMeLanguage().equals("Русский")) {
                    sendMesg(chatId, "Введите уникальный идентификатор.", 0);
                }
                if (dBUsers.giveMeLanguage().equals("English")) {
                    sendMesg(chatId, "Enter unique ID of the game.", 0);
                }
                dBUsers.readyToJoinGame(true);
                break;
            }
            case "Закончить игру":
            case "Finish the game": {
                dBGames.deleteTheGame(update.getMessage().getChatId());
                if (dBUsers.giveMeLanguage().equals("Русский"))
                    sendMesg(chatId, "Вы завершили игру.", 2);
                if (dBUsers.giveMeLanguage().equals("English"))
                    sendMesg(chatId, "You finished the game.", 3);
                break;
            }

            default: {
                if (dBUsers.isReadyToNewGame()) {
                    dBUsers.makeNewGame(false);
                    String sM1 = "", sM2 = "";

                    if (dBUsers.giveMeLanguage().equals("Русский")) {
                        sM1 = "Введите роль и количество игроков в этой роли в формате: " + "\n" +
                                "РОЛЬ - КОЛИЧЕСТВО ИГРОКОВ В ЭТОЙ РОЛИ " + "\n" +
                                "Например: ";
                        sM2 = "Мафия - 2" + "\n" + "Комиссар - 1" + "\n" + "и так далее.";
                    }
                    if (dBUsers.giveMeLanguage().equals("English")) {
                        sM1 =
                                "Enter the role and the number of players in this role in the format: " + "\n" +
                                "ROLE - NUMBER OF PLAYERS IN THIS ROLE  " + "\n" +
                                "For example: ";
                        sM2 = "Mafia - 2 "+ "\n" + "Doctor - 1" + "\n" + "and so on.";
                    }

                    sendMesg(chatId, sM1, 0);
                    sendMesg(chatId, sM2, 0);
                    dBGames = new DataBaseGames(dBUsers.getUser(update.getMessage().getChatId()), Integer.parseInt(messageText));
                    dBGames.makeHpFull(update.getMessage().getChat().getId(), true);
                    break;
                }
                if (dBUsers.isReadyToJoinGame()) {
                    dBUsers.readyToJoinGame(false);
                    String sM1 = "", sM2 = "";
                    if (dBUsers.giveMeLanguage().equals("Русский")) {
                        sM1 = "Вы вступили в игру.";
                        sM2 = "Новый игрок: ";
                    }
                    if (dBUsers.giveMeLanguage().equals("English")) {
                        sM1 = "You joined the game.";
                        sM2 = "New player: ";
                    }
                        try {
                            dBGames.enterIntoGame(update.getMessage().getChatId(), Long.parseLong(messageText), dBUsers.giveMeLanguage());
                            sendMesg(chatId, sM1, 0);
                            sendMesg(messageText, sM2 + update.getMessage().getChat().getUserName(), 0);
                            if (dBGames.checkTheFullish(Long.parseLong(messageText))) {
                                if (dBUsers.giveMeLanguage().equals("Русский"))
                                    sendMesg(messageText, "Набор игроков завершен, начинаю раздачу", 0);
                                if (dBUsers.giveMeLanguage().equals("English"))
                                    sendMesg(messageText, "The set of players is complete,  start the distribution.", 0);

                                distribusion();
                            }
                        }
                        catch (Exception e) {
                            sendMesg(chatId, e.getMessage(),0);
                        }
                }
                if (dBGames.isHpFull(update.getMessage().getChat().getId())) {
                    try {
                        dBGames.letsFullHM(messageText, update.getMessage().getChat().getId(), dBUsers.giveMeLanguage());
                        dBGames.makeHpFull(update.getMessage().getChat().getId(), false);
                        if (dBUsers.giveMeLanguage().equals("Русский"))
                            sendMesg(chatId, "Вы создали игру, ваш уникальный идентификатор: " + update.getMessage().getChat().getId() + "\n" +
                                    "Количество игроков: "  + dBGames.getQuanOfUsers(update.getMessage().getChat().getId()) + "\n" + "Ждем подключения игроков.",4);
                        if (dBUsers.giveMeLanguage().equals("English"))
                            sendMesg(chatId, "You created the game, your unique ID: " + update.getMessage().getChat().getId() +"\n" +
                                    "Number of players: " + dBGames.getQuanOfUsers(update.getMessage().getChat().getId()) + "\n" + "Waiting for the players to connect.",5);
                    } catch (Exception e) {
                        sendMesg(chatId, e.getMessage(),0);
                        dBGames.makeHpFull(update.getMessage().getChat().getId(), true);
                    }
                }
                break;
            }
        }
    }

    private void sendMesg(String chatId, String text, int keyNumber) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            setButton(sendMessage, keyNumber);
            execute(sendMessage);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    void distribusion() {
        List<Long> usersId = dBGames.getUsersId(Long.parseLong(messageText));
        HashMap<String, Integer> rolesAndQuan = dBGames.getHashMap(Long.parseLong(messageText));
        ArrayList<String> keysA = new ArrayList<>(rolesAndQuan.keySet());
        ArrayList<Integer> values = new ArrayList<>(rolesAndQuan.values());
        ArrayList<String> keys = new ArrayList<>();


        for (int i = 0; i < keysA.size(); i++) {
            for (int j = 0; j < values.get(i); j++) {
                keys.add(keysA.get(i));
            }
        }
        Collections.shuffle(keys);
        for (int i = 0; i < usersId.size(); i++) {
            sendMesg(usersId.get(i).toString(), keys.get(i), 0);
        }

        for (int i = 0; i < usersId.size(); i++) {
            if (dBUsers.giveMeLanguageByUniq(usersId.get(i)).equals("Русский")) {
                sendMesg(usersId.get(i).toString(), "Раздача завершенва, приятной игры!", 6);
                sendMesg(usersId.get(i).toString(), "Поддержать проект - https://vk.cc/ayqWm2", 6);
            }
            if (dBUsers.giveMeLanguageByUniq(usersId.get(i)).equals("English")) {
                sendMesg(usersId.get(i).toString(), "Distribution is complete, enjoy the game!", 7);
                sendMesg(usersId.get(i).toString(), "To support the project - https://vk.cc/ayqWm2", 7);
            }
        }
    }

    void setButton(SendMessage sendMessage, int keyNumber) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        switch (keyNumber) {
            case 1: {
                List<KeyboardRow> keyboardRowList = new ArrayList<>();
                KeyboardRow keyboardRow = new KeyboardRow();

                keyboardRow.add(new KeyboardButton("Русский"));
                keyboardRow.add(new KeyboardButton("English"));

                keyboardRowList.add(keyboardRow);
                replyKeyboardMarkup.setKeyboard(keyboardRowList);
                break;
            }
            case 2: {
                List<KeyboardRow> keyboardRowList = new ArrayList<>();
                KeyboardRow keyboardRow = new KeyboardRow();

                keyboardRow.add(new KeyboardButton("Новая игра"));
                keyboardRow.add(new KeyboardButton("Вступить в игру"));

                keyboardRowList.add(keyboardRow);
                replyKeyboardMarkup.setKeyboard(keyboardRowList);
                break;
            }
            case 3: {
                List<KeyboardRow> keyboardRowList = new ArrayList<>();
                KeyboardRow keyboardRow = new KeyboardRow();

                keyboardRow.add(new KeyboardButton("New game"));
                keyboardRow.add(new KeyboardButton("Join into the game"));

                keyboardRowList.add(keyboardRow);
                replyKeyboardMarkup.setKeyboard(keyboardRowList);
                break;
            }
            case 4: {
                List<KeyboardRow> keyboardRowList = new ArrayList<>();
                KeyboardRow keyboardRow = new KeyboardRow();

                keyboardRow.add(new KeyboardButton("Закончить игру"));

                keyboardRowList.add(keyboardRow);
                replyKeyboardMarkup.setKeyboard(keyboardRowList);
                break;
            }
            case 5: {
                List<KeyboardRow> keyboardRowList = new ArrayList<>();
                KeyboardRow keyboardRow = new KeyboardRow();

                keyboardRow.add(new KeyboardButton("Finish the game"));

                keyboardRowList.add(keyboardRow);
                replyKeyboardMarkup.setKeyboard(keyboardRowList);
                break;
            }

            default:
                List<KeyboardRow> keyboardRowList = new ArrayList<>();
                KeyboardRow keyboardRow = new KeyboardRow();

                keyboardRowList.add(keyboardRow);
                replyKeyboardMarkup.setKeyboard(keyboardRowList);
                break;
        }
    }

    @Override
    public String getBotUsername() {
        return "mafiaByJeanBot";
    }

    @Override
    public String getBotToken() {
        return "";
    }
}