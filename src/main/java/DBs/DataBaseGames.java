package DBs;
import Parents.Game;
import Parents.User;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBaseGames {

    private final MongoCollection<Game> collectionOfGames = new MongoClient(new MongoClientURI("mongodb+srv://JeanBernad:password@weatherbot-wlpyo.mongodb.net/test?authSource=admin&replicaSet=weatherBot-shard-0&readPreference=primary&appname=MongoDB%20Compass&ssl=true"))
            .getDatabase("telegramBot")
            .withCodecRegistry(CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()
                    )))
            .getCollection("mafiaBotGames", Game.class);

    Game game;

    public DataBaseGames(User user, int quantityOfUsers) {
        game = new Game();
        List<Long> usersId = new ArrayList<>();
        usersId.add(user.getTgId());
        game.setUsersId(usersId);
        game.setQuantityOfUsers(quantityOfUsers);
        game.setUniqId(user.getTgId());
        game.setHpFull(false);
        HashMap<String, Integer> hashMap = new HashMap<>();
        game.setRolesAndQuan(hashMap);
        collectionOfGames.insertOne(game);
    }

    public void enterIntoGame(long userId, long priorUserId, String lang) throws Exception {
        Game founded = collectionOfGames.find(Filters.eq("uniqId", priorUserId)).first();
        if (founded != null) {
            List<Long> usersId = founded.getUsersId();
            usersId.add(userId);
            collectionOfGames.deleteOne(Filters.eq("uniqId", priorUserId));
            collectionOfGames.insertOne(founded);
        }
        else {
            if (lang.equals("Русский")) throw new Exception("Неверный идентификатор, попробуйте снова. ");
            if (lang.equals("English")) throw new Exception("Wrong unique ID, try again.");
        }
    }

    public boolean checkTheFullish(long priorUserId) {
        Game founded = collectionOfGames.find(Filters.eq("uniqId", priorUserId)).first();
        if (founded != null) {
            List<Long> usersId = founded.getUsersId();
            return (usersId.size() == founded.getQuantityOfUsers());
        } else return false;
    }

    public boolean isHpFull(long uniqId) {
        Game founded = collectionOfGames.find(Filters.eq("uniqId", uniqId)).first();
        if (founded != null)
            return founded.isHpFull();
        else return false;
    }
    public void makeHpFull(long uniqId, boolean l) {
        Game founded = collectionOfGames.find(Filters.eq("uniqId", uniqId)).first();
        if (founded != null) {
            founded.setHpFull(l);
            collectionOfGames.deleteOne(Filters.eq("uniqId", uniqId));
            collectionOfGames.insertOne(founded);
        }
    }
    public void letsFullHM(String str, long uniqId, String lang) throws Exception {
        Game founded = collectionOfGames.find(Filters.eq("uniqId", uniqId)).first();
        assert founded != null;
        HashMap<String, Integer> hashMap = new HashMap<>();
        String[] strings = str.split("\n");
        for (String s: strings) {
            String[] strings1 = s.split(" - ");
            hashMap.put(strings1[0], Integer.parseInt(strings1[1]));
        }
        int s = 0;
        ArrayList<Integer> val = new ArrayList(hashMap.values());
        for (int i = 0; i < hashMap.values().size(); i++) {
            s+= val.get(i);
        }
        if (founded.getQuantityOfUsers() == s) {
            founded.setRolesAndQuan(hashMap);
            collectionOfGames.deleteOne(Filters.eq("uniqId", uniqId));
            collectionOfGames.insertOne(founded);
        }
        else {
            if (lang.equals("Русский")) throw new Exception("Количество игроков не совпадает, попробуйте снова. ");
            if (lang.equals("English")) throw new Exception("Quantity of users is not equal, try again.");
        }
    }

    public List getUsersId(long uniqId) {
        Game founded = collectionOfGames.find(Filters.eq("uniqId", uniqId)).first();
        assert founded != null;
        return founded.getUsersId();
    }

    public HashMap getHashMap(long uniqId) {
        Game founded = collectionOfGames.find(Filters.eq("uniqId", uniqId)).first();
        assert founded != null;
        return founded.getRolesAndQuan();
    }

    public int getQuanOfUsers(long uniqId) {
        Game founded = collectionOfGames.find(Filters.eq("uniqId", uniqId)).first();
        assert founded != null;
        return collectionOfGames.find(Filters.eq("uniqId", uniqId)).first().getQuantityOfUsers();
    }

    public void deleteTheGame(long uniqId) {
        if (collectionOfGames.find(Filters.eq("uniqId", uniqId)).first() != null)
            collectionOfGames.deleteOne(Filters.eq("uniqId", uniqId));
    }
}