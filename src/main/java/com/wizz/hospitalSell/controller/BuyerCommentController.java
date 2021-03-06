package com.wizz.hospitalSell.controller;

import com.wizz.hospitalSell.VO.ResultVO;
import com.wizz.hospitalSell.domain.CommentInfo;
import com.wizz.hospitalSell.enums.ResultEnum;
import com.wizz.hospitalSell.exception.SellException;
import com.wizz.hospitalSell.form.CommentForm;
import com.wizz.hospitalSell.service.CommentService;
import com.wizz.hospitalSell.service.ProductInfoService;
import com.wizz.hospitalSell.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 买家端评价
 * Created By Cx On 2018/8/3 22:42
 */
@RestController
@RequestMapping("/buyer/comment")
@Slf4j
public class BuyerCommentController {

    @Autowired
    CommentService commentService;
    @Autowired
    ProductInfoService productInfoService;

    @GetMapping("/{productId}")
    public ResultVO findByProductId(@PathVariable String productId) {
        if (productInfoService.findOne(productId) == null) {
            log.error("[商品评价]商品id错误，商品id不存在，productId={}", productId);
            throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        return ResultUtil.success(commentService.findInfosByProductId(productId));
    }

    /**
     * 通过orderId查询所属评论
     * data 包含 orderId
     */
    @GetMapping("/list")
    public ResultVO findByOrderId(String orderId) {
        return ResultUtil.success(commentService.findByOrderId(orderId));
    }

    //注意，作为接收Json的类（CommentForm），必须要有Get/Set方法
    @PostMapping
    public ResultVO create(@Valid @RequestBody List<CommentForm> commentForms, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //表单校验有误
            log.error("[创建订单]参数不正确，commentForm={}", commentForms);
            throw new SellException(bindingResult.getFieldError() == null ? "参数不正确" : bindingResult.getFieldError().getDefaultMessage(),
                    ResultEnum.PARAM_ERROR.getCode());
        }
        List<CommentInfo> commentInfos = new ArrayList<>();
        for (CommentForm commentForm : commentForms){
            CommentInfo commentInfo = new CommentInfo();
            BeanUtils.copyProperties(commentForm, commentInfo);
            commentInfos.add(commentInfo);
        }
        commentService.createAll(commentInfos);
        return ResultUtil.success();
    }
}
