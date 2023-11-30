package com.jade.canopusapi.dao;

import com.jade.canopusapi.models.User;
import com.jade.canopusapi.models.utils.UserRole;
import com.jade.canopusapi.payload.response.MessageResponse;
import com.jade.canopusapi.repository.UserRepository;
import com.jade.canopusapi.security.services.UserDetailsImpl;
import com.jade.canopusapi.security.services.UserDetailsServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

@Service
public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${site.url}")
    private String siteURL;

    @Value("${api.url}")
    private String apiURL;

    public ResponseEntity<?> register(User user) throws MessagingException, UnsupportedEncodingException {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Já existe uma conta com este e-mail!"));
        }

        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Já existe uma conta com este telefone!"));
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        String randomCode = UUID.randomUUID().toString();
        user.setVerificationCode(randomCode);
        user.setRole(UserRole.VOLUNTARIO);
        user.setVerified(false);
        userRepository.save(user);
        sendVerificationEmail(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Usuário cadastrado com sucesso!"));
    }

    private void sendVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String senderName = "Canopus";
        String subject = "Verificação de conta Canopus";
        String content = "Caro [[name]],<br>"
                + "Por favor, clique no link abaixo para verificar sua inscrição:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFICAR</a></h3>"
                + "Obrigado,<br>"
                + "Canopus.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getFullName());
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        mailSender.send(message);
    }

    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);
        if (user == null || user.getVerified()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setVerified(true);
            userRepository.save(user);
            return true;
        }
    }

    public void verifyAdmin(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setVerificationCode(null);
        user.setVerified(true);
        userRepository.save(user);
    }

    public boolean resendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
        if (user != null && !user.getVerified()) {
            String randomCode = UUID.randomUUID().toString();
            user.setVerificationCode(randomCode);
            userRepository.save(user);
            sendVerificationEmail(user);
            return true;
        }
        return false;
    }

    public User findUser(String code) {
        return userRepository.findByVerificationCode(code);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public String saveImage(String avatarBase64) throws IOException {
        String[] parts = avatarBase64.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid base64 format");
        }
        String imageType = parts[0].split("/")[1].split(";")[0];
        logger.info(imageType);
        if (!imageType.equals("jpg") && !imageType.equals("jpeg") && !imageType.equals("png")) {
            throw new IllegalArgumentException("Invalid image type");
        }

        String folderPath = "media";
        byte[] decodedImage = Base64.getDecoder().decode(avatarBase64.split(",")[1].getBytes(StandardCharsets.UTF_8));
        String fileName = UUID.randomUUID().toString() + "." + imageType;

        Path imagePath = Paths.get(folderPath, fileName);
        Files.write(imagePath, decodedImage);

        return apiURL + "/media/" + fileName;

    }

    public ResponseEntity<?> registerMod(User user) throws MessagingException, UnsupportedEncodingException {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Já existe uma conta com este e-mail!"));
        }
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Já existe uma conta com este telefone!"));
        }
        String randomCode = UUID.randomUUID().toString();
        user.setVerificationCode(randomCode);
        user.setVerified(false);
        userRepository.save(user);
        sendAdminVerificationEmail(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Usuário cadastrado com sucesso!"));
    }

    private void sendAdminVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String senderName = "Canopus";
        String subject = "Convite para ser [[role]] Canopus";
        String content = "Caro [[name]],<br>"
                + "Por favor, clique no link abaixo para completar sua inscrição:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">COMPLETAR CADASTRO</a></h3>"
                + "Obrigado,<br>"
                + "Canopus.";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        subject = subject.replace("[[role]]", user.getRole().toString());
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getFullName());
        String verifyURL = siteURL + "/verify_create?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        mailSender.send(message);
    }
}
