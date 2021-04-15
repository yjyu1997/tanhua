package top.yusora.tanhua.dubbo.server.enums;

/**
 * 评论类型：1-点赞，2-评论，3-喜欢
 * @author heyu
 */
public enum CommentType {

    /**
     * @Description 点赞
     */
    LIKE(1),
    /**
     * @Description 评论
     */
    COMMENT(2),
    /**
     * @Description 喜欢
     */
    LOVE(3);

    int type;

    CommentType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}