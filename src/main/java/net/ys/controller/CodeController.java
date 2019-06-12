package net.ys.controller;

import net.ys.constant.GenResult;
import net.ys.util.LogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping(value = "validate/code", produces = {"application/json;charset=utf-8"})
@ApiIgnore
public class CodeController {

    public static final String VALIDATE_CODE = "validateCode";
    private int w = 70;
    private int h = 26;

    @RequestMapping(method = RequestMethod.POST)
    public void genCode(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            createBackground(g);//生成背景
            String s = createCharacter(g);//生成字符
            g.dispose();

            request.getSession().setAttribute(VALIDATE_CODE, s);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.close();
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> validCode(HttpServletRequest request, @RequestParam(required = true, value = "validate_code", defaultValue = "") String validateCode) {
        try {
            String code = (String) request.getSession().getAttribute(VALIDATE_CODE);
            if (validateCode != null && !"".equals(validateCode) && validateCode.toUpperCase().equals(code)) {
                return GenResult.SUCCESS.genResult("true");
            }
            return GenResult.SUCCESS.genResult("false");
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return GenResult.UNKNOWN_ERROR.genResult();
    }

    private Color getRandColor(int fc, int bc) {
        int f = fc;
        int b = bc;
        Random random = new Random();
        if (f > 255) {
            f = 255;
        }
        if (b > 255) {
            b = 255;
        }
        return new Color(f + random.nextInt(b - f), f + random.nextInt(b - f), f + random.nextInt(b - f));
    }

    private void createBackground(Graphics g) {
        g.setColor(getRandColor(220, 250));
        g.fillRect(0, 0, w, h);
        for (int i = 0; i < 8; i++) {
            g.setColor(getRandColor(40, 150));
            Random random = new Random();
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            int x1 = random.nextInt(w);
            int y1 = random.nextInt(h);
            g.drawLine(x, y, x1, y1);
        }
    }

    private String createCharacter(Graphics g) {
        char[] codeSeq = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};
        String[] fontTypes = {"Arial", "Arial Black", "AvantGarde Bk BT", "Calibri"};
        Random random = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String r = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);//random.nextInt(10));
            g.setColor(new Color(50 + random.nextInt(100), 50 + random.nextInt(100), 50 + random.nextInt(100)));
            g.setFont(new Font(fontTypes[random.nextInt(fontTypes.length)], Font.BOLD, 26));
            g.drawString(r, 15 * i + 5, 19 + random.nextInt(8));
            s.append(r);
        }
        return s.toString();
    }
}
