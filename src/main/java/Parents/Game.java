package Parents;

import java.util.HashMap;
import java.util.List;

public class Game {
    private List<Long> usersId;
    private HashMap<String, Integer> rolesAndQuan;
    private long uniqId;
    private int quantityOfUsers;
    private boolean hpFull;

    public Game() {
    }

    public boolean isHpFull() {
        return hpFull;
    }

    public void setHpFull(boolean hpFull) {
        this.hpFull = hpFull;
    }

    public HashMap<String, Integer> getRolesAndQuan() {
        return rolesAndQuan;
    }

    public void setRolesAndQuan(HashMap<String, Integer> rolesAndQuan) {
        this.rolesAndQuan = rolesAndQuan;
    }

    public int getQuantityOfUsers() {
        return quantityOfUsers;
    }

    public void setQuantityOfUsers(int quantityOfUsers) {
        this.quantityOfUsers = quantityOfUsers;
    }

    public List getUsersId() {
        return usersId;
    }

    public void setUsersId(List usersId) {
        this.usersId = usersId;
    }

    public long getUniqId() {
        return uniqId;
    }

    public void setUniqId(long uniqId) {
        this.uniqId = uniqId;
    }

    @Override
    public String toString() {
        return "UniqID: " + getUniqId() + "\n" +
                "UsersID: " + getUsersId().toString() + "\n";
    }
}