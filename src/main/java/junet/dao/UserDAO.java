package junet.dao;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;

public class UserDAO {
    @SneakyThrows
    public boolean delete(Integer userId) {
        try (Connection connection = DriverManager.getConnection("url", "username", "password")) {
            return true;
        }
    }
}
