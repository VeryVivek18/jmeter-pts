import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

public class SimpleMail {

    public static void main(String[] args) throws MessagingException {
        
        String filename;
        try {
            filename = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No file Name Present in args");
            return;
        }
        
        // expecting all follwing parameter from enviroment
        String SMTP_HOST_NAME = System.getenv("SMTP_HOST_NAME");
        String SMTP_AUTH_USER = System.getenv("SMTP_AUTH_USER");
        String SMTP_AUTH_PWD = System.getenv("SMTP_AUTH_PWD");
        String FROM = System.getenv("FROM");
        String TO = System.getenv("TO");
        String ZIP_FILENAME = System.getenv("ZIP_FILENAME");

        int SMTP_HOST_PORT = 25;
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "true");

        Session mailSession = Session.getDefaultInstance(props);
        mailSession.setDebug(true);

        Transport transport = mailSession.getTransport("smtp");

        MimeMessage message = new MimeMessage(mailSession);

        message.setSubject("Jmeter Run Result");

        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Now set the actual message
        messageBodyPart.setText("Check build at localhost:8080");

        // Create a multipar message
        Multipart multipart = new MimeMultipart();

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(ZIP_FILENAME);
        multipart.addBodyPart(messageBodyPart);

        // Send the complete message parts
        message.setContent(multipart);
        message.setSentDate(new Date());
        message.setFrom(new InternetAddress(FROM));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));

        transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
        System.out.println("email sent successfully........");
    }
}
