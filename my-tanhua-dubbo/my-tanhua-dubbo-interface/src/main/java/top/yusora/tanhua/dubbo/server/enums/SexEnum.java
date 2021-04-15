package top.yusora.tanhua.dubbo.server.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 *
 */
public enum SexEnum implements IEnum<Integer> {
    /**
     * @Description 男性
     */
    MAN(1,"男"),
    /**
     * @Description 女性
     */
    WOMAN(2,"女"),
    /**
     * @Description 未设置
     */
    UNKNOWN(3,"未知");

    private int value;
    private String desc;

    SexEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
