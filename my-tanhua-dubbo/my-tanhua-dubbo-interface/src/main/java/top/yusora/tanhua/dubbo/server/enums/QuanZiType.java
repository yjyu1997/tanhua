package top.yusora.tanhua.dubbo.server.enums;

/**
 * @author heyu
 */

public enum QuanZiType {

    /**
     * @Description 动态
     */
    PUBLISH(1),
    /**
     * @Description 评论
     */
    COMMENT(2),
    /**
     * @Description 小视频
     */
    VIDEO(3);

    int type;

    QuanZiType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
