package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersistingAnUser() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");

        Assertions.assertEquals(0, user.getId());
        em.persist(user);
        Assertions.assertNotEquals(0, user.getId());
    }

    @Test
    void verifyRepositoryByPersistingAnUser() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");

        Assertions.assertEquals(0, user.getId());
        repository.save(user);
        Assertions.assertNotEquals(0, user.getId());
    }
}
