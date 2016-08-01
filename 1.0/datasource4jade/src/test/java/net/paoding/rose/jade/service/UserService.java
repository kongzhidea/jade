package net.paoding.rose.jade.service;

import net.paoding.rose.jade.dao.UserDAO;
import net.paoding.rose.jade.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    public User getUser(int id) {
        return userDAO.getUser(id);
    }
}
