package Parents;

public class User {
    private long tgId;
    private String userName;
    private String language;
    private int countGames;
    private long number;
    private boolean makeNewGame;
    private boolean readyToJoinGame;

    public User(long tgId, String userName, long number) {
        this.tgId = tgId;
        this.userName = userName;
        this.number = number;
    }

    public User() {}

    public boolean isReadyToJoinGame() {
        return readyToJoinGame;
    }

    public void setReadyToJoinGame(boolean readyToJoinGame) {
        this.readyToJoinGame = readyToJoinGame;
    }

    public boolean isMakeNewGame() {
        return makeNewGame;
    }

    public void setMakeNewGame(boolean makeNewGame) {
        this.makeNewGame = makeNewGame;
    }

    public long getTgId() {
        return tgId;
    }

    public void setTgId(long tgId) {
        this.tgId = tgId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getCountGames() {
        return countGames;
    }

    public void setCountGames(int countGames) {
        this.countGames = countGames;
    }

    public long getNumber() { return number; }

    public void setNumber(long number) { this.number = number; }

    @Override
    public String toString() {
        return "ID: " + getTgId() + "\n" +
                "UserName: " + getUserName() + "\n";
    }
}

