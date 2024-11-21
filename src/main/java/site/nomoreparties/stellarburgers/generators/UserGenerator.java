package site.nomoreparties.stellarburgers.generators;

import site.nomoreparties.stellarburgers.models.User;
import site.nomoreparties.stellarburgers.utils.RandomUtils;

public class UserGenerator {
        public static User randomUser() {
            return new User()
                    .withEmail(RandomUtils.randomEmail(10))
                    .withPassword(RandomUtils.randomPassword(10))
                    .withName(RandomUtils.cyrillic(10));
        }
}
