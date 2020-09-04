package DBs;
import Parents.User;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

public class DataBaseUsers {

    private final MongoCollection<User> collectionOfUsers = new MongoClient(new MongoClientURI("mongodb+srv://JeanBernad:password@weatherbot-wlpyo.mongodb.net/test?authSource=admin&replicaSet=weatherBot-shard-0&readPreference=primary&appname=MongoDB%20Compass&ssl=true"))
            .getDatabase("telegramBot")
            .withCodecRegistry(CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()
                    )))
            .getCollection("mafiaBotUsers", User.class);

    User user;

    public DataBaseUsers(long tgId, String userName) {
        user = new User(tgId, userName, collectionOfUsers.count());
    }

    public String writeNewUser() {
        user.setLanguage("-");
        user.setCountGames(0);
        user.setMakeNewGame(false);
        user.setReadyToJoinGame(false);
        collectionOfUsers.insertOne(user);
        return "Новый пользователь!\n" + user.toString();
    }

    public void setLanguage(String language) {
        User founded = collectionOfUsers.find(Filters.eq("tgId", user.getTgId())).first();
        if (founded != null) {
            founded.setLanguage(language);
            collectionOfUsers.deleteOne(Filters.eq("tgId", user.getTgId()));
            collectionOfUsers.insertOne(founded);
        }
    }

    public String giveMeLanguage() {
        User founded = collectionOfUsers.find(Filters.eq("tgId", user.getTgId())).first();
        assert founded != null;
        return founded.getLanguage();
    }
    public boolean existanceOfUser() {
        User founded = collectionOfUsers.find(Filters.eq("tgId", user.getTgId())).first();
        return founded != null;
    }

    public User getUser(long tgId) {
        return collectionOfUsers.find(Filters.eq("tgId", user.getTgId())).first();
    }

    public void makeNewGame(boolean b) {
        User founded = collectionOfUsers.find(Filters.eq("tgId", user.getTgId())).first();
        assert founded != null;
        founded.setMakeNewGame(b);
        collectionOfUsers.deleteOne(Filters.eq("tgId", user.getTgId()));
        collectionOfUsers.insertOne(founded);
    }
    public boolean isReadyToNewGame() {
        User founded = collectionOfUsers.find(Filters.eq("tgId", user.getTgId())).first();
        assert founded != null;
        return founded.isMakeNewGame();
    }

    public void readyToJoinGame(boolean j) {
        User founded = collectionOfUsers.find(Filters.eq("tgId", user.getTgId())).first();
        assert founded != null;
        founded.setReadyToJoinGame(j);
        collectionOfUsers.deleteOne(Filters.eq("tgId", user.getTgId()));
        collectionOfUsers.insertOne(founded);
    }

    public boolean isReadyToJoinGame() {
        User founded = collectionOfUsers.find(Filters.eq("tgId", user.getTgId())).first();
        assert founded != null;
        return founded.isReadyToJoinGame();
    }

    public String giveMeLanguageByUniq(long uniqId) {
        User founded = collectionOfUsers.find(Filters.eq("tgId", uniqId)).first();
        assert founded != null;
        return founded.getLanguage();
    }
}