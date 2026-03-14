package Repository;

import Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepo {
    private Map<Long, User> userDB = new HashMap<>();

    public User save(User user) {
        if(user.getUserId() == null){
            Long id= (long) (userDB.size() + 1);
            user.setUserId(id);
            userDB.put(id, user);
        }else {
            //update
            userDB.put(user.getUserId(), user);
        }
        return userDB.get(user.getUserId());
    }

    public User findById(Long id) {
        return userDB.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(userDB.values());
    }
}
