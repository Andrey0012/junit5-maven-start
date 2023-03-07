package dao;

import junet.dao.UserDAO;


public class UserDaoMock extends UserDAO {
    @Override
    public boolean delete(Integer userId) {
        return false;
    }
}
