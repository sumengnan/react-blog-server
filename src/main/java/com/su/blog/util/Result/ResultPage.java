package com.su.blog.util.Result;

import lombok.Data;

/**
 * @ClassName: Result
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/8 11:09
 * @Version:
 */
@Data
public final class ResultPage {
    private int count;
    private Object rows;

    public ResultPage() {
    }

    public ResultPage(int count, Object rows) {
        this.count = count;
        this.rows = rows;
    }
}
