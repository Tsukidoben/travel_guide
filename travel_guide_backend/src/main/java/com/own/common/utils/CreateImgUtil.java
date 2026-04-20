package com.own.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.y8e.common.exception.BaseException;
import com.own.common.config.CustomYmlProperties;
import com.own.service.FileUploadRecordsService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
* 头像生成工具类
*/
public class CreateImgUtil {

    public static String createImgByName(String userName) {
        try {
            return createImgByName(userName, 2, "back");
        } catch (Exception e) {
            throw new BaseException("生成图片失败");
        }
    }

    public static String createImgByName(String userName, Integer length) {
        try {
            return createImgByName(userName, length, "back");
        } catch (Exception e) {
            throw new BaseException("生成图片失败");
        }
    }

    public static String createImgByName(String userName, String frontOrBack) {
        try {
            return createImgByName(userName, 2, frontOrBack);
        } catch (Exception e) {
            throw new BaseException("生成图片失败");
        }
    }

    public static String createImgByName(String userName, Integer length, String frontOrBack) {
        try {
            MockMultipartFile mockMultipartFile = generateImg(userName, length, frontOrBack);

            return upload(List.of(mockMultipartFile),"head");
        } catch (Exception e) {
            throw new BaseException("生成图片失败");
        }
    }

    public static MockMultipartFile generateImg(String name, Integer length, String frontOrBack) throws IOException {
        if (ObjectUtil.isEmpty(name)) {
            throw new BaseException("没有图片");
        }

        int width = 100; // 图片宽度
        int height = 100; // 图片高度
        String nameWritten = getName(name, length, frontOrBack); // 根据规则取字

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 设置背景颜色
        g2.setBackground(getRandomColor());
        g2.clearRect(0, 0, width, height);

        // 设置文字颜色
        g2.setPaint(Color.WHITE);

        // 动态计算字体大小
        int fontSize = Math.min(width / nameWritten.length() - 5, height / 2 - 10); // 基于文字长度和图片大小计算字体
        Font font = new Font("微软雅黑", Font.PLAIN, fontSize);
        g2.setFont(font);

        // 获取文字的宽度和高度
        FontMetrics fm = g2.getFontMetrics(font);
        int textWidth = fm.stringWidth(nameWritten);
        int textHeight = fm.getAscent(); // 从基线到顶部的高度
        int descent = fm.getDescent(); // 从基线到底部的距离

        // 计算居中坐标
        int x = (width - textWidth) / 2;
        int y = (height - (textHeight + descent)) / 2 + textHeight;

        // 绘制文字
        g2.drawString(nameWritten, x, y);

        // 生成圆角图像
        // BufferedImage rounded = makeRoundedCorner(bi, 99);
        // ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // ImageIO.write(rounded, "png", byteArrayOutputStream);

        // 输出图片
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", os);
            return new MockMultipartFile(
                    name,
                    name + "." + "png",
                    "image/" + "png",
                    os.toByteArray());
        } catch (IOException e) {
            throw new BaseException("图片输出失败");
        }
    }

    private static String getName(String name, Integer length, String frontOrBack) {
        String resp = "";
        if (name.length() <= length) {
            return name;
        }
        if ("back".equals(frontOrBack)) {
            // 取后面的
            return name.substring(name.length() - length);
        } else {
            // 取前面
            return name.substring(0,length);
        }
    }

    /**
     * 获得随机颜色
     *
     * @return
     */
    public static Color getRandomColor() {
        String[] beautifulColors = new String[]{
                "255,99,71", "255,127,80", "255,140,0", "255,165,0", "240,128,128",
                "220,20,60", "255,69,0", "255,105,180", "199,21,133", "255,20,147",
                "255,0,255", "186,85,211", "147,112,219", "138,43,226", "153,50,204",
                "148,0,211", "75,0,130", "72,61,139", "106,90,205", "123,104,238",
                "0,191,255", "30,144,255", "0,0,255", "65,105,225", "25,25,112",
                "0,0,139", "0,0,205", "0,128,128", "32,178,170", "0,255,255",
                "64,224,208", "0,206,209", "127,255,212", "102,205,170", "50,205,50",
                "144,238,144", "34,139,34", "46,139,87", "60,179,113", "85,107,47",
                "154,205,50", "107,142,35", "124,252,0", "173,255,47", "0,255,0",
                "0,250,154", "152,251,152", "143,188,143", "127,255,0", "0,128,0",
                "46,139,87", "34,139,34", "128,128,0", "189,183,107", "240,230,140",
                "238,232,170", "255,250,205", "255,255,0", "255,215,0", "255,223,0",
                "184,134,11", "218,165,32", "210,105,30", "139,69,19", "244,164,96",
                "222,184,135", "210,180,140", "205,133,63", "139,101,8", "244,164,96",
                "233,150,122", "250,128,114", "255,160,122", "255,165,79", "210,105,30",
                "255,140,0", "255,99,71", "255,69,0", "255,127,80", "250,128,114",
                "255,105,180", "199,21,133", "255,20,147", "255,0,255", "218,112,214",
                "186,85,211", "147,112,219", "138,43,226", "153,50,204", "148,0,211",
                "75,0,130", "106,90,205", "123,104,238", "72,61,139", "30,144,255",
                "0,0,255", "0,0,139", "0,0,205", "70,130,180", "95,158,160",
                "100,149,237", "173,216,230", "176,224,230", "135,206,250", "135,206,235",
                "0,191,255", "30,144,255", "0,0,255", "65,105,225", "25,25,112",
                "127,255,0", "124,252,0", "173,255,47", "0,255,127", "0,250,154",
                "144,238,144", "152,251,152", "143,188,143", "50,205,50", "34,139,34",
                "0,128,0", "0,100,0", "46,139,87", "102,205,170", "60,179,113",
                "32,178,170", "64,224,208", "72,209,204", "0,206,209", "0,255,255",
                "127,255,212", "175,238,238", "176,224,230", "95,158,160", "70,130,180",
                "100,149,237", "30,144,255", "135,206,250", "0,191,255", "173,216,230"
        };

        int len = beautifulColors.length;
        Random random = new Random();
        String[] color = beautifulColors[random.nextInt(len)].split(",");
        return new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]),
                Integer.parseInt(color[2]));
    }

    /**
     * 图片做圆角处理
     *
     * @param image
     * @param cornerRadius
     * @return
     */
    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return output;
    }

    /**
     * 上传方法
     */
    public static String upload(List<MultipartFile> file, String prefix) {
        FileUploadRecordsService fileUploadRecordsService = SpringUtil.getBean(FileUploadRecordsService.class);
        return CommonUtil.toJson(fileUploadRecordsService.upload(file, prefix));
    }
}
