package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
class EcommerceEpicerieApplicationTests {

    @Autowired
    private EmailService emailService;

    @Test
    public void testSendPasswordResetEmail() {
        String testEmail = "niyo.roger@hotmail.com";
        String resetUrl = "http://localhost:8080/reset-password?token=testToken";

        emailService.sendPasswordResetEmail(testEmail, resetUrl);
    }

    @Test
    void contextLoads() {
    }

}

