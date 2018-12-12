package net.ys.service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class EmailService {

    private String host;
    private String username;
    private String password;
    private Session session;
    private MimeMessage message;
    private Transport transport;

    public EmailService(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.sender.username", username);
        properties.setProperty("mail.sender.password", password);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        session = Session.getInstance(properties);
        session.setDebug(false);
        message = new MimeMessage(session);
    }

    /**
     * 发送普通邮件
     *
     * @param subject     邮件主题
     * @param sendHtml    邮件内容
     * @param receiveUser 收件人地址
     */
    public void sendCommonEmail(String subject, String sendHtml, String receiveUser) {
        try {
            InternetAddress from = new InternetAddress(username);
            message.setFrom(from);
            InternetAddress to = new InternetAddress(receiveUser);
            message.setRecipient(Message.RecipientType.TO, to);//还可以有CC、BCC
            message.setSubject(subject, "UTF-8");
            String content = sendHtml.toString();
            message.setContent(content, "text/html;charset=UTF-8");
            message.saveChanges();
            transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送邮件
     *
     * @param subject     邮件主题
     * @param sendHtml    邮件内容
     * @param receiveUser 收件人地址
     * @param attachment  附件
     */
    public void sendAttachmentEmail(String subject, String sendHtml, String receiveUser, File attachment) {
        try {
            // 发件人
            InternetAddress from = new InternetAddress(username);
            message.setFrom(from);

            // 收件人
            InternetAddress to = new InternetAddress(receiveUser);
            message.setRecipient(Message.RecipientType.TO, to);

            // 邮件主题
            message.setSubject(subject, "UTF-8");

            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();

            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(sendHtml, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);

            // 添加附件的内容
            if (attachment != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
                multipart.addBodyPart(attachmentBodyPart);
            }
            message.setContent(multipart);
            message.saveChanges();
            transport = session.getTransport("smtp");
            // smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(host, username, password);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 群发
     *
     * @param subject      邮件主题
     * @param sendHtml     邮件内容
     * @param receiveUsers 收件人列表地址
     */
    public void sendZoneEmail(String subject, String sendHtml, String receiveUsers) {
        try {
            InternetAddress from = new InternetAddress(username);
            message.setFrom(from);
            InternetAddress[] toList = new InternetAddress().parse(receiveUsers);
            message.setRecipients(Message.RecipientType.TO, toList);
            message.setSubject(subject, "UTF-8");
            String content = sendHtml.toString();
            message.setContent(content, "text/html;charset=UTF-8");
            message.saveChanges();
            transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送邮件群发
     *
     * @param subject      邮件主题
     * @param sendHtml     邮件内容
     * @param receiveUsers 收件人列表地址
     * @param attachment   附件
     */
    public void sendAttachmentZoneEmail(String subject, String sendHtml, String receiveUsers, File attachment) {
        try {
            // 发件人
            InternetAddress from = new InternetAddress(username);
            message.setFrom(from);

            // 收件人
            InternetAddress[] toList = new InternetAddress().parse(receiveUsers);
            message.setRecipients(Message.RecipientType.TO, toList);

            // 邮件主题
            message.setSubject(subject, "UTF-8");

            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();

            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(sendHtml, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);

            // 添加附件的内容
            if (attachment != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
                multipart.addBodyPart(attachmentBodyPart);
            }
            message.setContent(multipart);
            message.saveChanges();
            transport = session.getTransport("smtp");
            // smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(host, username, password);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送邮件群发
     *
     * @param subject        邮件主题
     * @param sendHtml       邮件内容
     * @param receiveUsers   收件人列表地址
     * @param fileUrlAddress 附件url地址
     */
    public void sendNetAttachmentZoneEmail(String subject, String sendHtml, String receiveUsers, String fileUrlAddress) {
        try {
            // 发件人
            InternetAddress from = new InternetAddress(username);
            message.setFrom(from);

            // 收件人
            InternetAddress[] toList = new InternetAddress().parse(receiveUsers);
            message.setRecipients(Message.RecipientType.TO, toList);

            // 邮件主题
            message.setSubject(subject, "UTF-8");

            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();

            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(sendHtml, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);

            // 添加附件的内容
            if (fileUrlAddress != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                URL url = new URL(fileUrlAddress);

                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int size;
                while ((size = inputStream.read(buff)) != -1) {
                    out.write(buff, 0, size);
                }
                byte[] attach_bytes = out.toByteArray();
                inputStream.close();
                out.close();

                DataSource source = new ByteArrayDataSource(attach_bytes, connection.getContentType());
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(MimeUtility.encodeText("优美.jpg", "utf-8", null));//解决中文乱码
                multipart.addBodyPart(attachmentBodyPart);
            }
            message.setContent(multipart);
            message.saveChanges();
            transport = session.getTransport("smtp");
            // smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(host, username, password);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        EmailService emailService = new EmailService("smtp.qq.com", "xxx@qq.com", "授权码");
        emailService.sendCommonEmail("test", "test", "xxx@qq.com");
    }
}
