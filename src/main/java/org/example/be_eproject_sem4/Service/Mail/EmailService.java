package org.example.be_eproject_sem4.Service.Mail;

import org.example.be_eproject_sem4.Config.MailConfig;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final MailConfig mailSender;

    public void sendVerificationEmail(String to, String token, Long userId) {
        String verificationUrl = "https://api.mystictarots.xyz/api/auth/public/verify?token=" + token + "&userId=" + userId;
        String subject = "✨ [Mystictarot] Chạm vào định mệnh - Xác thực tài khoản của bạn";

        String content = """
                <div style="background-color: #0f172a; padding: 40px 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
                    <div style="max-width: 600px; margin: auto; background: #1e293b; border: 1px solid #334155; border-radius: 24px; overflow: hidden; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);">

                        <div style="background: linear-gradient(135deg, #6366f1 0%%, #a855f7 100%%); padding: 40px 20px; text-align: center;">
                            <div style="font-size: 40px; margin-bottom: 10px;">✨</div>
                            <h1 style="color: white; margin: 0; font-size: 28px; letter-spacing: 2px; text-transform: uppercase;">Mystic Tarot</h1>
                        </div>

                        <div style="padding: 40px; color: #f1f5f9; line-height: 1.6;">
                            <h2 style="color: #818cf8; text-align: center;">Khởi đầu hành trình mới</h2>
                            <p style="font-size: 16px; text-align: center;">
                                Chào bạn, những lá bài đang chờ đợi bạn khám phá. <br>
                                Vui lòng xác thực email để mở cánh cửa bước vào thế giới huyền học của Mystictarot.
                            </p>

                            <div style="text-align: center; margin: 40px 0;">
                                <a href="%s" style="background: linear-gradient(135deg, #818cf8 0%%, #c084fc 100%%); color: white; padding: 16px 35px; text-decoration: none; border-radius: 12px; font-weight: bold; font-size: 16px; box-shadow: 0 10px 15px -3px rgba(99, 102, 241, 0.4); display: inline-block;">
                                    KÍCH HOẠT ĐỊNH MỆNH
                                </a>
                            </div>

                            <p style="font-size: 13px; color: #94a3b8; text-align: center; font-style: italic;">
                                Link xác thực có hiệu lực trong vòng 24 giờ. <br>
                                Nếu không phải bạn đăng ký, hãy cứ để lá bài này trôi vào quên lãng.
                            </p>
                        </div>

                        <div style="padding: 20px; background: #0f172a; text-align: center; border-top: 1px solid #334155;">
                            <p style="color: #64748b; font-size: 12px; margin: 0;">
                                © 2026 Mystictarot Team. All rights reserved. <br>
                                Hanoi, Vietnam
                            </p>
                        </div>
                    </div>
                </div>
                """
                .formatted(verificationUrl);

        sendHtmlEmail(to, subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String content) {
        MimeMessage message = mailSender.javaMailSender().createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true = HTML
            mailSender.javaMailSender().send(message);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage());
        }
    }
}