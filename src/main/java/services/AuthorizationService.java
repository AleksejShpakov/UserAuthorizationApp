package services;

import entity.User;

import java.util.HashMap;
import java.util.Map;

public final class AuthorizationService {
    private static AuthorizationService instance = null;
    private Map<String, User> userBase = new HashMap<>();           //хранит всех зарегистрированных пользователей
    private Map<String, User> userSessionBase = new HashMap<>();    //хранит сессии пользователей

    private AuthorizationService(){}

    public static AuthorizationService getInstance(){
        if(instance == null) {
            instance = new AuthorizationService();

            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    public void addUser(User user){
        if(isExist(user.getMail())){
            return;
        }
        userBase.put(user.getMail(), user);
    }

    public boolean logOut(String sessionId){
        if(userSessionBase.containsKey(sessionId)){
            userSessionBase.remove(sessionId);
            return true;
        }
        return false;
    };

    public User getUserBySessionId(String id){
        return userSessionBase.get(id);
    }

    private void rememberSession(String id, User user){
        this.userSessionBase.put(id, user);
    }

    public boolean isAuthorized(String sessionId){
        return userSessionBase.containsKey(sessionId);
    }

    public boolean isExist(String email){
        return userBase.containsKey(email);
    }

    public boolean authorize(String email, String password, String sessionID){
        User user = null;

        if(!isExist(email)){
            return false;
        }

        user = userBase.get(email);
        if(!user.getPassword().equals(password)){
            return false;
        }

        rememberSession(sessionID, user);

        return true;
    }
}
