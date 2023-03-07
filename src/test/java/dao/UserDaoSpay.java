package dao;

import junet.dao.UserDAO;

import java.util.HashMap;
import java.util.Map;

public class UserDaoSpay extends UserDAO {
    private final UserDAO userDAO;
    Map<Integer, Boolean> answer = new HashMap<>();

    public UserDaoSpay(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public boolean delete(Integer userId) {
        return answer.getOrDefault(userId, userDAO.delete(userId));
    }
}
