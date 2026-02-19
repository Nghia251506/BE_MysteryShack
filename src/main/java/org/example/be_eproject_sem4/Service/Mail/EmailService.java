package org.example.be_eproject_sem4.Service.Mail;

import org.example.be_eproject_sem4.Config.MailConfig;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

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

    public void sendForgotPasswordEmail(String to) {
        // Link dẫn về Front-end của ông (localhost:3000)
        String resetUrl = "http://localhost:3000/change-password?email=" + to;
        String subject = "🔑 [Mystictarot] Khôi phục mật chú - Tìm lại lối vào cõi Mystic";

        String content = """
                <div style="background-color: #0a0510; padding: 40px 0; font-family: 'Segoe UI', Arial, sans-serif;">
                    <div style="max-width: 550px; margin: auto; background: #160e2a; border: 1px solid #4338ca; border-radius: 30px; overflow: hidden; box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);">
                        
                        <div style="background: linear-gradient(135deg, #4338ca 0%%, #7c3aed 100%%); padding: 35px 20px; text-align: center;">
                            <div style="font-size: 45px; margin-bottom: 10px;">🔮</div>
                            <h1 style="color: #ffffff; margin: 0; font-size: 26px; letter-spacing: 3px; text-transform: uppercase; font-weight: 900;">Mystic Tarot</h1>
                        </div>

                        <div style="padding: 40px; color: #e2e8f0; line-height: 1.8; text-align: center;">
                            <h2 style="color: #fbbf24; font-size: 22px; margin-bottom: 20px;">Lấy lại mật mã định mệnh</h2>
                            <p style="font-size: 15px;">
                                Những vì sao cho thấy bạn đang gặp khó khăn khi quay trở lại với chúng tôi. <br>
                                Đừng lo lắng, hãy nhấn vào nút bên dưới để thiết lập lại mật mã mới và tiếp tục hành trình khám phá vận mệnh.
                            </p>

                            <div style="margin: 40px 0;">
                                <a href="%s" style="background: linear-gradient(135deg, #f59e0b 0%%, #d97706 100%%); color: #ffffff; padding: 18px 40px; text-decoration: none; border-radius: 15px; font-weight: bold; font-size: 16px; box-shadow: 0 10px 20px rgba(245, 158, 11, 0.3); display: inline-block; text-transform: uppercase; letter-spacing: 1px;">
                                    ĐẶT LẠI MẬT CHÚ
                                </a>
                            </div>

                            <p style="font-size: 13px; color: #94a3b8; margin-top: 30px;">
                                <strong style="color: #ef4444;">Lưu ý:</strong> Liên kết này chỉ tồn tại trong vòng 15 phút. <br>
                                Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.
                            </p>
                        </div>

                        <div style="padding: 25px; background: #0a0510; text-align: center; border-top: 1px solid #2e1065;">
                            <p style="color: #6366f1; font-size: 12px; margin: 0; font-weight: bold;">
                                Mystic Tarot - Chạm tay vào tương lai
                            </p>
                            <p style="color: #475569; font-size: 11px; margin-top: 8px;">
                                © 2026 Mystictarot Team. Hanoi, Vietnam.
                            </p>
                        </div>
                    </div>
                </div>
                """
                .formatted(resetUrl);

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
