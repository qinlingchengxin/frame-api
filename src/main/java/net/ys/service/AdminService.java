package net.ys.service;

import net.ys.bean.SysAdmin;
import net.ys.dao.SysAdminDao;
import net.ys.util.Tools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AdminService {
    @Resource
    private SysAdminDao adminDao;

    public SysAdmin queryAdmin(String username, String password) {
        String pass = Tools.genMD5(password);
        return adminDao.queryAdmin(username, pass);
    }
}
