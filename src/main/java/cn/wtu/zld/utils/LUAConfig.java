package cn.wtu.zld.utils;

public class LUAConfig {
    //存放编写的LUA脚本，如果想读的话，请看utils包下的LUA.text文件
    private static String LUAcontent ;
    static {
        LUAcontent = "local userId = KEYS[1];\n" +
                "local comId = KEYS[2];\n" +
                "local comKey = \"Concurrency:iphone:\"..comId;\n" +
                "local userKey = \"Concurrency:user\"..comId;\n" +
                "local userExists = redis.call(\"sismember\",userKey,userId);\n" +
                "if tonumber(userExists) == 1 then\n" +
                "    return 2;\n" +
                "end\n" +
                "local num = redis.call(\"get\",comKey);\n" +
                "if tonumber(num) <= 0 then\n" +
                "    return 0;\n" +
                "else\n" +
                "    redis.call(\"decr\",comKey);\n" +
                "    redis.call(\"sadd\",userKey,userId);\n" +
                "end\n" +
                "return 1;";
    }
    public static String getLua(){
        return LUAcontent;
    }
}
