package com.techtask.martianrobots;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MartianRobotsApplicationTest {

    @Test
    public void applicationStarts() {
        MartianRobotsApplication.main(new String[] {});
    }

}