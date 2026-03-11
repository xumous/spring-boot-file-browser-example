package com.demo_249050433.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//加上这句话，来自url的请求才能到这个地方去查找请求
@Controller
public class HaHaController {
    @RequestMapping("html_20251203_6bde99")
    public String html_20251203_6bde99() {
        return "html_20251203_6bde99";//templates下面的同名网页
    }

    @RequestMapping("train_query")
    public String train_query() {
        return "train_query";//templates下面的同名网页
    }

    @RequestMapping("seat_selection")
    public String seat_selection() {
        return "seat_selection";//templates下面的同名网页
    }

    @RequestMapping("order_payment")
    public String order_payment() {
        return "order_payment";//templates下面的同名网页
    }

    @RequestMapping("share_download_202512041331")
    public String share_download_202512041331() {
        return "share_download_202512041331";//templates下面的同名网页
    }
}