package com.yiaobang;

public class Test {
    public static void main(String[] args) {
        // = C#的AppDomain.CurrentDomain.BaseDirectory
        /*
        *   java.version			Java 运行时环境版本
            java.vendor			    Java 运行时环境供应商
            java.vendor.url			Java 供应商的 URL
            java.home			    Java 安装目录
            java.vm.specification.version	Java 虚拟机规范版本
            java.vm.specification.vendor	Java 虚拟机规范供应商
            java.vm.specification.name	Java 虚拟机规范名称
            java.vm.version			Java 虚拟机实现版本
            java.vm.vendor			Java 虚拟机实现供应商
            java.vm.name			Java 虚拟机实现名称
            java.specification.version	Java 运行时环境规范版本
            java.specification.vendor	Java 运行时环境规范供应商
            java.specification.name		Java 运行时环境规范名称
            java.class.version		Java 类格式版本号
            java.class.path			Java 类路径
            java.library.path		加载库时搜索的路径列表
            java.io.tmpdir			默认的临时文件路径
            java.compiler			要使用的 JIT 编译器的名称
            java.ext.dirs			一个或多个扩展目录的路径
            os.name			操作系统的名称
            os.arch				操作系统的架构
            os.version			操作系统的版本
            file.separator			文件分隔符（在 UNIX 系统中是“/”）
            path.separator			路径分隔符（在 UNIX 系统中是“:”）
            line.separator			行分隔符（在 UNIX 系统中是“/n”）
            user.name			用户的账户名称
            user.home			用户的主目录
            user.dir				用户的当前工作目录
        *
        * */
       info();
    }
    public static void info(){
        add("Java 运行时环境版本","java.version");
        add("Java 运行时环境供应商","java.vendor");
        add("Java 供应商的 URL","java.vendor.url");
        add("Java 安装目录","java.home");
        add("Java 虚拟机规范版本","java.vm.specification.version");
        add("Java 虚拟机规范供应商","java.vm.specification.vendor");
        add("Java 虚拟机规范名称","java.vm.specification.name");
        add("Java 虚拟机实现版本","java.vm.version");
        add("Java 虚拟机实现供应商","java.vm.vendor");
        add("Java 虚拟机实现名称","java.vm.name");
        add("Java 运行时环境规范版本","java.specification.version");
        add("Java 运行时环境规范供应商","java.specification.vendor");
        add("Java 运行时环境规范名称","java.specification.name");
        add("Java 类格式版本号","java.class.version");
        add("Java 类路径","java.class.path");
        add("加载库时搜索的路径列表","java.library.path");
        add("默认的临时文件路径","java.io.tmpdir");
        add("要使用的 JIT 编译器的名称","java.compiler");
        add("一个或多个扩展目录的路径","java.ext.dirs");
        add("操作系统的名称","os.name");
        add("操作系统的架构","os.arch");
        add("操作系统的版本","os.version");
        add("文件分隔符","file.separator");
        add("路径分隔符","path.separator");
        add("行分隔符","line.separator");
        add("用户的账户名称","user.name");
        add("用户的主目录","user.home");
        add("用户的当前工作目录","user.dir");
    }
    public static void add(String name,String value){
        System.out.println(name + ": " + System.getProperty(value)+"\n");
    }
}
