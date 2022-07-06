package kelceoglu.beyazit.radio.utils;

import org.ini4j.Ini;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class MailConfig {

    private Ini ini;

    @Bean
    public JavaMailSender mailSender () {
        try {
            this.ini = new Ini (new File ("/srv/mail.ini"));
            JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl ();
            javaMailSender.setHost (this.ini.get ("mail", "host"));
            javaMailSender.setPort (Integer.valueOf (this.ini.get ("mail", "port")));
            javaMailSender.setUsername (this.ini.get ("mail", "username"));
            javaMailSender.setPassword (this.ini.get ("mail", "password"));
            Properties p = new Properties ();
            p.put("mail.smtp.starttls.enable", "true");
            p.put ("mail.smtp.auth", "true");
            p.put("mail.transport.protocol", "smtp");
            p.put("mail.debug","true");
            javaMailSender.setJavaMailProperties (p);
            return javaMailSender;
        } catch (IOException e) {
            throw new RuntimeException (e);
        }
    }
}
