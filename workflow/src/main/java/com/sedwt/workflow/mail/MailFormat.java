package com.sedwt.workflow.mail;

/**
 * @author : yj zhang
 * @since : 2021/8/19 11:36
 */

public class MailFormat {
    public static String formatHtml(String title, String beginDate, String endDate, String participants,
                                    String roomAddress, String status, String content, String urls){
        StringBuilder sb = new StringBuilder();
        sb.append(
                "<html lang=\"en\">\n" ).append(
                "<head>\n" ).append(
                "    <meta charset=\"UTF-8\">\n" ).append(
                "    <title>Title</title>\n" ).append(
                "    <style>\n" ).append(
                "        .first {\n")
            .append(
                "            display: inline-block;\n" ).append(
                "            width: 300px;\n" ).append(
                "            text-align: right;\n" ).append(
                "        }\n")
            .append("        .second {\n").append("            display: inline-block;\n")
            .append("            width: 800px;\n").append("            word-break: break-all;\n")
            .append("            text-align: left;\n").append("            overflow: hidden;\n").append("        }")
            .append(
                "    </style>\n" ).append(
                "</head>\n" ).append(
                "<body>\n" ).append(
                "<div>\n" ).append(
                "    <div style=\"margin-left: 50px\">\n" ).append(
                "        <p><span class = 'first'><b>会议/评审标题：</b></span><span class = 'second'>")
            .append(title).append("</span></p>\n")
            .append("        <p><span class = 'first'><b>会议/评审日期：</b></span><span class = 'second'>").append(beginDate)
            .append("  -  ").append(endDate)
                                                                                       .append("</span></p>\n" ).append(
                "        <p><span class = 'first'><b>参与人员：</b></span><span class = 'second'>")
            .append(participants).append("</span></p>\n")
            .append("        <p><span class = 'first'><b>会议/评审地点：</b></span><span class = 'second'>")
            .append(roomAddress).append("</span></p>\n")
            .append("        <p><span class = 'first'><b>状态：</b></span><span class = 'second'>").append(status)
            .append("</span></p>\n")
            .append("        <p><span class = 'first'><b>会议/评审内容：</b></span><span class = 'second'>").append(content)
            .append("</span></p>\n").append("        <p><span class = 'first'><b>会议/评审会议链接：</b></span><a href=\"url\">")
            .append(urls).append("</a></p>\n")
            .append(
                "    </div>\n" ).append(
                "</div>\n" ).append(
                "</body>\n" ).append(
                "</html>");
        return sb.toString();
    }
}
