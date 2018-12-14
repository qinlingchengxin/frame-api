package net.ys.utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class EmailUtil {

    private String host;
    private String username;
    private String password;
    private Session session;
    private MimeMessage message;
    private Transport transport;

    public EmailUtil(String host, String username, String password) {
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
     * @param content     邮件内容
     * @param receiveUser 收件人地址
     */
    public void sendCommonEmail(String subject, String content, String receiveUser) {
        try {
            InternetAddress from = new InternetAddress(username);
            message.setFrom(from);
            InternetAddress to = new InternetAddress(receiveUser);
            message.setRecipient(Message.RecipientType.TO, to);//还可以有CC、BCC
            message.setSubject(subject, "UTF-8");
            message.setContent(content, "text/html;charset=UTF-8");
            message.saveChanges();
            transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    LogUtil.error(e);
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
            LogUtil.error(e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    LogUtil.error(e);
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
            LogUtil.error(e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    LogUtil.error(e);
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
            LogUtil.error(e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    LogUtil.error(e);
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
                byte[] attachBytes = out.toByteArray();
                inputStream.close();
                out.close();

                DataSource source = new ByteArrayDataSource(attachBytes, connection.getContentType());
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
            LogUtil.error(e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    LogUtil.error(e);
                }
            }
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        EmailUtil emailUtil = new EmailUtil("smtp.qq.com", "xxxxx@qq.com", "djvzaksvnarrbgec");

        emailUtil.sendCommonEmail("xxxx", String.format("【数据共享中心】尊敬的管理员%s，数据共享中心监控到与%s的对接中断，请尽快进行排查！", "南美鹰", "内蒙古监督平台"), "1290821453@qq.com");
    }
}
