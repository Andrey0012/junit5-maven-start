package dao;

import junet.dao.UserDAO;

import java.util.HashMap;
import java.util.Map;


public class UserDaoMock extends UserDAO {
    Map<Integer, Boolean> answer = new HashMap<>();
    @Override
    public boolean delete(Integer userId) {

        return answer.getOrDefault(userId, false);
    }
}
