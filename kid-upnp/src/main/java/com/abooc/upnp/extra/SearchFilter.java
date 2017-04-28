package com.abooc.upnp.extra;

import java.util.concurrent.Callable;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/13.
 */
public interface SearchFilter extends Callable<Boolean> {

    @Override
    Boolean call();
}
