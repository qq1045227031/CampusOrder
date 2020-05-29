package com.imooc.o2o.entity;

public enum UserProductMapStateEnum {
    SUCCESS(1, "操作成功"), INNER_ERROR(-1001, "操作失败"), NULL_USERPRODUCT_ID(-1002,
            "UserProductId为空"), NULL_USERPRODUCT_INFO(-1003, "传入了空的信息"),NULL_ID(-1004,"没有该Id的额订单"),ERROR_SHOP(-1005,"不是该店铺下的订单"),
      OVERED(-1006,"该订单已经完成"),ADDPOINTFALSE(-1007,"添加积分失败"),CREATEPORINTFALSE(-1008,"创建积分失败");

    private int state;

    private String stateInfo;

    private UserProductMapStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static UserProductMapStateEnum stateOf(int index) {
        for (UserProductMapStateEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }
}
