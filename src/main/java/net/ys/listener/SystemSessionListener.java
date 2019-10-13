package net.ys.listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SystemSessionListener implements HttpSessionListener {
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        String id = session.getId();
        System.out.println("--- sessionCreated ---" + id);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        String id = session.getId();
        System.out.println("--- sessionDestroyed ---" + id);
    }
}