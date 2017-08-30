package com.example.demo.controller;

import com.example.demo.util.ScheduleUtils;
import com.example.demo.util.ScheduleUtils.Job;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;


/**
 * 动态定时器界面工具类
 */
@Controller
@RequestMapping("/job")
public class JobController {

    private final static Logger logger = Logger.getLogger(JobController.class);
    //	private final static String JOBS_FILE = JobController.class.getResource("/").getPath() + "jobs.txt";
    private final static String JOBS_FILE = "jobs.txt";
    private final static String SEP = ":::"; // 字段分隔符
    private final static String TOKEN = "123456"; // 登录验证token

    private static Map<String, Job> jobs = Collections.synchronizedMap(new TreeMap<String, Job>()); // 定时任务集合

    // 初始化jobs
    static {
        try {
            System.out.println(JOBS_FILE);
            File file = new File(JOBS_FILE);
            // 定时任务配置文件存在，则加载
            if (file.exists()) {
                @SuppressWarnings("resource")
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                List<String> lines = new ArrayList<String>();
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                    if (line.trim().length() == 0) {
                        continue;
                    }
                    String[] fields = line.split(SEP);
                    Job job = new Job();
                    job.setJobName(fields[0]);
                    job.setClassName(fields[1]);
                    job.setMethodName(fields[2]);
                    job.setCron(fields[3]);
                    job.setStatus(Integer.valueOf(fields[4]));
                    jobs.put(job.getJobName(), job);
                }
            }
        } catch (IOException e) {
            logger.error("定时任务配置文件读取异常：", e);
        }
    }

    /**
     * @throws IOException
     * @Title: syncJobs
     * @Description: 持久化定时任务
     */
    private void syncJobs() throws IOException {
        File file = new File(JOBS_FILE);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, Job> entry : jobs.entrySet()) {
            Job job = entry.getValue();
            data.append(job.getJobName()).append(SEP);
            data.append(job.getClassName()).append(SEP);
            data.append(job.getMethodName()).append(SEP);
            data.append(job.getCron()).append(SEP);
            data.append(job.getStatus());
            data.append("\r\n");
        }
        writer.write(data.toString());
        writer.close();
    }

    /**
     * @Title: init
     * @Description: 初始化启动定时任务
     */
    @PostConstruct
    void init() {
        new Thread(() -> {
            for (Map.Entry<String, Job> entry : jobs.entrySet()) {
                Job job = entry.getValue();
                try {
                    if (job.getStatus() == 1) {
                        ScheduleUtils.add(job);
                    }
                } catch (Exception e) {
                    logger.error("定时任务初始化异常", e);
                }
            }
        }).start();
    }

    /**
     * @return
     * @throws IOException
     * @Title: index
     * @Description: 定时任务首页
     */
    @RequestMapping("/index.html")
    @ResponseBody
    void index(HttpSession session, HttpServletResponse response, String token) throws IOException {
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        if (TOKEN.equals(token)) { // 验证token
            session.setAttribute("login", true);
        } else {
            out.println("Authentication is not passed！");
            out.flush();
            return;
        }
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("    <meta charset=\'UTF-8\'>");
        out.println("    <title>定时任务管理界面</title>");
        out.println("    <link rel=\'stylesheet\' type=\'text/css\' href=\'https://www.jeasyui.com/easyui/themes/material/easyui.css\'>");
        out.println("    <link rel=\'stylesheet\' type=\'text/css\' href=\'https://www.jeasyui.com/easyui/themes/icon.css\'>");
        out.println("    <link rel=\'stylesheet\' type=\'text/css\' href=\'https://www.jeasyui.com/easyui/themes/color.css\'>");
        out.println("    <link rel=\'stylesheet\' type=\'text/css\' href=\'https://www.jeasyui.com/easyui/demo/demo.css\'>");
        out.println("    <script type=\'text/javascript\' src=\'https://code.jquery.com/jquery-1.9.1.min.js\'></script>");
        out.println("    <script type=\'text/javascript\' src=\'https://www.jeasyui.com/easyui/jquery.easyui.min.js\'></script>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h2>定时任务管理界面</h2>");
        out.println("    <p></p>");
        out.println("    ");
        out.println("    <table id=\'dg\' title=\'定时任务面板\' class=\'easyui-datagrid\' style=\'width:90%;height:600px\'");
        out.println("            url=\'/job/find.do\'");
        out.println("            toolbar=\'#toolbar\' pagination=\'false\'");
        out.println("            rownumbers=\'true\' fitColumns=\'true\' singleSelect=\'true\'>");
        out.println("        <thead>");
        out.println("            <tr>");
        out.println("                <th field=\'jobName\' width=\'200\'>任务名称</th>");
        out.println("                <th field=\'className\' width=\'360\'>类名</th>");
        out.println("                <th field=\'methodName\' width=\'150\'>方法名</th>");
        out.println("                <th field=\'cron\' width=\'100\'>cron表达式</th>");
        out.println("                <th field=\'status\' width=\'50\' formatter=\'statusFormatter\'>任务状态</th>");
        out.println("            </tr>");
        out.println("        </thead>");
        out.println("    </table>");
        out.println("    <div id=\'toolbar\'>");
        out.println("        <a href=\'javascript:void(0)\' class=\'easyui-linkbutton\' iconCls=\'icon-add\' plain=\'true\' onclick=\'add()\'>添加</a>");
        out.println("        <a href=\'javascript:void(0)\' class=\'easyui-linkbutton\' iconCls=\'icon-edit\' plain=\'true\' onclick=\'edit()\'>修改</a>");
        out.println("        <a href=\'javascript:void(0)\' class=\'easyui-linkbutton\' iconCls=\'icon-remove\' plain=\'true\' onclick=\'remove()\'>删除</a>");
        out.println("    </div>");
        out.println("    ");
        out.println("    <div id=\'dlg\' class=\'easyui-dialog\' style=\'width:400px\'");
        out.println("            closed=\'true\' buttons=\'#dlg-buttons\'>");
        out.println("        <form id=\'fm\' method=\'post\' novalidate style=\'margin:0;padding:20px 50px\'>");
        out.println("            <div style=\'margin-bottom:10px\'>");
        out.println("                <input name=\'jobName\' class=\'easyui-textbox\' required=\'true\' label=\'任务名称:\' style=\'width:100%\'>");
        out.println("            </div>");
        out.println("            <div style=\'margin-bottom:10px\'>");
        out.println("                <input name=\'className\' class=\'easyui-textbox\' required=\'true\' label=\'类名:\' style=\'width:100%\'>");
        out.println("            </div>");
        out.println("            <div style=\'margin-bottom:10px\'>");
        out.println("                <input name=\'methodName\' class=\'easyui-textbox\' required=\'true\' label=\'方法名:\' style=\'width:100%\'>");
        out.println("            </div>");
        out.println("            <div style=\'margin-bottom:10px\'>");
        out.println("                <input name=\'cron\' class=\'easyui-textbox\' required=\'true\' label=\'cron表达式:\' style=\'width:100%\'>");
        out.println("            </div>");
        out.println("            <div style=\'margin-bottom:10px\'>");
        out.println("                <select name=\'status\' class=\'easyui-combobox\' label=\'任务状态:\' style=\'width:100%\' editable=\'false\' value=\'1\'>");
        out.println("                  <option value=\'1\' selected>开启</option>");
        out.println("                  <option value=\'0\'>禁用</option>");
        out.println("                </select>");
        out.println("            </div>");
        out.println("        </form>");
        out.println("    </div>");
        out.println("    <div id=\'dlg-buttons\'>");
        out.println("        <a href=\'javascript:void(0)\' class=\'easyui-linkbutton c6\' iconCls=\'icon-ok\' onclick=\'save()\' style=\'width:90px\'>保存</a>");
        out.println("        <a href=\'javascript:void(0)\' class=\'easyui-linkbutton\' iconCls=\'icon-cancel\' onclick=\'closeDialog()\' style=\'width:90px\'>取消</a>");
        out.println("    </div>");
        out.println("    <script type=\'text/javascript\'>");
        out.println("        var url;");
        out.println("        function add(){");
        out.println("            $(\'#dlg\').dialog(\'open\').dialog(\'center\').dialog(\'setTitle\',\'添加定时任务\');");
        out.println("            $(\'#fm\').form(\'clear\');");
        out.println("            url = \'/job/addOrUpdate.do\';");
        out.println("        }");
        out.println("");
        out.println("        function edit(){");
        out.println("            var row = $(\'#dg\').datagrid(\'getSelected\');");
        out.println("            if (row){");
        out.println("                $(\'#dlg\').dialog(\'open\').dialog(\'center\').dialog(\'setTitle\',\'修改定时任务\');");
        out.println("                $(\'#fm\').form(\'load\',row);");
        out.println("                url = \'/job/addOrUpdate.do\';");
        out.println("            } else {");
        out.println("              $.messager.alert(\'提示信息\', \'请选择要编辑的数据项\', \'warning\');");
        out.println("            }");
        out.println("        }");
        out.println("");
        out.println("        function closeDialog(){");
        out.println("          $(\'#dlg\').dialog(\'close\');");
        out.println("        }");
        out.println("");
        out.println("        function save(){");
        out.println("            $(\'#fm\').form(\'submit\',{");
        out.println("                url: url,");
        out.println("                onSubmit: function(){");
        out.println("                    return $(this).form(\'validate\');");
        out.println("                },");
        out.println("                success: function(data){");
        out.println("                    var data = eval(\'(\'+data+\')\');");
        out.println("                    if (200 == data.code){");
        out.println("                      $(\'#dlg\').dialog(\'close\');        // close the dialog");
        out.println("                      $(\'#dg\').datagrid(\'reload\');    // reload the user data");
        out.println("                      $.messager.show({    ");
        out.println("                                  title: \'提示信息\',");
        out.println("                                  msg: data.message");
        out.println("                              });");
        out.println("                    } else {");
        out.println("                      $.messager.alert(\'提示信息\', data.message, \'error\');");
        out.println("                    }");
        out.println("                }");
        out.println("            });");
        out.println("        }");
        out.println("");
        out.println("        function remove(){");
        out.println("            var row = $(\'#dg\').datagrid(\'getSelected\');");
        out.println("            if (row){");
        out.println("                $.messager.confirm(\'确认提示\',\'确定要删除所选数据?\',function(r){");
        out.println("                    if (r){");
        out.println("                        $.post(\'/job/remove.do\',{jobName:row.jobName},function(data){");
        out.println("                            if (200 == data.code){");
        out.println("                                $(\'#dg\').datagrid(\'reload\');    // reload the user data");
        out.println("                                $.messager.show({    ");
        out.println("                                    title: \'提示信息\',");
        out.println("                                    msg: data.message");
        out.println("                                });");
        out.println("                            } else {");
        out.println("                                $.messager.alert(\'提示信息\', data.message, \'error\');");
        out.println("                            }");
        out.println("                        },\'json\');");
        out.println("                    }");
        out.println("                });");
        out.println("            } else {");
        out.println("              $.messager.alert(\'提示信息\', \'请选择要删除的数据项\', \'warning\');");
        out.println("            }");
        out.println("        }");
        out.println("");
        out.println("        function statusFormatter(value){");
        out.println("          return value==1 ? \'开启\':\'禁用\'; ");
        out.println("        }");
        out.println("    </script>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
    }

    /**
     * @param job
     * @return
     * @Title: addOrUpdate
     * @Description: 添加或者修改定时任务
     */
    @RequestMapping("/addOrUpdate.do")
    @ResponseBody
    Object addOrUpdate(HttpSession session, Job job) {
        Boolean login = (Boolean) session.getAttribute("login");
        if (login == null || !login) { // 未验证通过
            return new ResultObject(ResultObject.ERROR, "身份验证未通过");
        }
        String key = job.getJobName();
        if (job.getStatus() == null) {
            job.setStatus(0);
        }
        try {
            jobs.put(key, job);
            ScheduleUtils.cancel(job);
            if (job.getStatus() == 1) {
                // 开始执行定时任务
                ScheduleUtils.add(job);
            }
            syncJobs(); // 持久化定时任务
            return new ResultObject(ResultObject.SUCCESS, "操作成功！");
        } catch (Exception e) {
            logger.error("操作失败：", e);
            return new ResultObject(ResultObject.ERROR, "操作失败，失败原因：" + e);
        }
    }

    /**
     * @param job
     * @return
     * @Title: remove
     * @Description: 删除定时任务
     */
    @RequestMapping("/remove.do")
    @ResponseBody
    Object remove(HttpSession session, Job job) {
        Boolean login = (Boolean) session.getAttribute("login");
        if (login == null || !login) { // 未验证通过
            return new ResultObject(ResultObject.ERROR, "身份验证未通过");
        }
        String key = job.getJobName();
        try {
            ScheduleUtils.cancel(job);
            if (key != null && jobs.containsKey(key)) {
                jobs.remove(job.getJobName());
            }
            syncJobs(); // 持久化定时任务
            return new ResultObject(ResultObject.SUCCESS, "删除成功！");
        } catch (Exception e) {
            logger.error("删除定时任务异常：", e);
            return new ResultObject(ResultObject.FAILED, "删除数据失败，失败原因：" + e);
        }
    }

    @RequestMapping("/find.do")
    @ResponseBody
    Object find(Job job) {
        Object[] rows = jobs.values().toArray();
        int total = rows.length;
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("rows", rows);
        data.put("total", total);
        return data;
    }


}

/************************************** 返回结果实体类 ********************************************/

/**
 * @author lixk
 * @version [1.0, 2017年8月14日]
 * @ClassName: ResultObject
 * @Description: TODO
 * @date 2017年8月14日 上午10:57:17
 * @since version 1.0
 */

class ResultObject {

    /**
     * 成功
     */
    public static final int SUCCESS = 200;
    /**
     * 失败
     */
    public static final int FAILED = 300;
    /**
     * 系统异常
     */
    public static final int ERROR = 500;

    private int code;// 返回码 200成功，500系统异常
    private String message;// 消息
    private Object data;// 数据

    public ResultObject() {
    }

    /**
     * @param code 返回码
     */
    public ResultObject(int code) {
        super();
        this.code = code;
    }

    /**
     * @param code    返回码
     * @param message 返回信息
     */
    public ResultObject(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    /**
     * @param code 返回码
     * @param data 返回数据
     */
    public ResultObject(int code, Object data) {
        super();
        this.code = code;
        this.data = data;
    }

    /**
     * @param code    返回码
     * @param message 返回信息
     * @param data    返回数据
     */
    public ResultObject(int code, String message, Object data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}