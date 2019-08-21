package com.maven.startserver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Server {
	private Log log = LogFactory.getLog(Server.class);

	private boolean isRunning = false;

    private String getDeviceDir(String projectName) {
        String deviceLocate = System.getProperty("device.locate");
        String deviceDir = null;
        if (deviceLocate == null) {
            // eclipse环境下
            deviceDir = new File(System.getProperty("user.dir")).getParent() + File.separator
                    + projectName;
        } else {
            // realse环境下
            String workDir = System.getProperty("user.dir");
            deviceDir = workDir + File.separator + deviceLocate + File.separator + projectName;
        }
        System.out.println(deviceDir +  "    deviceDir");
        return deviceDir;
    }

    private String getClassPath(String projectName) {
        String classPath = "";
        String deviceLocate = System.getProperty("device.locate");
        String workDir = null;
        String deviceDir = null;
        if (deviceLocate == null) {
            // eclipse环境下
            workDir = System.getProperty("user.dir");
            workDir = new File(workDir).getParent();
            deviceDir = workDir + File.separator + projectName;
            classPath += deviceDir + File.separator + "bin" + File.pathSeparator;
            String[] rely = {
                "CMSAnalysis", "CMSCommon", "CMSCommunication", "CMSConfiguration", "CMSEntity",
                "CMSXDR", "CMSProject", "bobi"};
            for (String string : rely) {
                classPath += workDir + File.separator + string + File.separator + "bin"
                        + File.pathSeparator;
            }
            String libDir = workDir + File.separator + "CMSLib" + File.separator + "lib";
            for (File file : new File(libDir).listFiles()) {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    classPath += file.getPath() + File.pathSeparator;
                }
            }
        } else {
            // realse环境下
            workDir = System.getProperty("user.dir");
            deviceDir = workDir + File.separator + deviceLocate + File.separator + projectName;
            classPath += deviceDir + File.separator + "app-conf" + File.pathSeparator + deviceDir
                    + File.separator + "proj-conf" + File.pathSeparator;
            String bin = deviceDir + File.separator + "bin";
            File file = new File(bin);
            String[] list = file.list();
            for (String string : list) {
                classPath += bin + File.separator + string + File.pathSeparator;
            }
            File[] libFiles = new File(workDir + File.separator + "lib").listFiles();
            if (libFiles != null)
                for (File libFile : libFiles) {
                    if (libFile.isFile() && libFile.getName().endsWith(".jar")) {
                        classPath += libFile.getPath() + File.pathSeparator;
                    }
                }
        }
        return classPath;
    }

    public void deviceStart(String deviceDir, String classPath, String mainApp) throws IOException {
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File(deviceDir));
        Map<String, String> condi = new HashMap<String, String>(System.getenv());
        condi.put("CLASSPATH", classPath);
        String encoding = " -Dfile.encoding=UTF-8 ";
        String commandLine = "java" + encoding + mainApp;
        CommandLine line = CommandLine.parse(commandLine);
        log.error(classPath);
        executor.execute(line, condi);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void reload(Task task, Map<Integer, String> jpsMap) {
        String mainClass = task.getMainApp();
        boolean isRunning = false;
        for (int pid : jpsMap.keySet()) {
            String className = jpsMap.get(pid);
            if (className.equals(mainClass)) {
                isRunning = true;
                log.debug(task.getProjectName() + "正常运行中……，进程ID：" + pid);
                break;
            }
        }
        if (!isRunning) {
            final String projectName = task.getProjectName();
            final String mainApp = task.getMainApp();
            final String deviceDir = getDeviceDir(projectName);
            final String classPath = getClassPath(projectName);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        deviceStart(deviceDir, classPath, mainApp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void main(String[] args) {
		new Server().test();
	}

	private void test() {
		Task task = new Task();
		task.setProjectName("CMSLib");
		task.setMainApp("com.bobi.test.Test");
		reload(task, new HashMap<Integer, String>());
	}
}
