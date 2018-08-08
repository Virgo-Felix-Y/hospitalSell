package com.wizz.hospitalSell.service.impl;

import com.wizz.hospitalSell.VO.CommentVO;
import com.wizz.hospitalSell.dao.CommentInfoDao;
import com.wizz.hospitalSell.dao.CommentInfoRepository;
import com.wizz.hospitalSell.dao.ProductInfoDao;
import com.wizz.hospitalSell.dao.UserInfoDao;
import com.wizz.hospitalSell.domain.CommentInfo;
import com.wizz.hospitalSell.domain.ProductInfo;
import com.wizz.hospitalSell.domain.UserInfo;
import com.wizz.hospitalSell.dto.ProductCommentDto;
import com.wizz.hospitalSell.enums.ResultEnum;
import com.wizz.hospitalSell.exception.SellException;
import com.wizz.hospitalSell.service.CommentService;
import com.wizz.hospitalSell.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created By Cx On 2018/8/2 9:49
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CommentServiceImpl implements CommentService{

    @Autowired
    CommentInfoRepository commentInfoRepository;
    @Autowired
    ProductInfoDao productInfoDao;
    @Autowired
    CommentInfoDao commentInfoDao;
    @Autowired
    UserInfoDao userInfoDao;

    public List<ProductCommentDto> findDtosByProductInfos(List<ProductInfo> productInfos){
        List<ProductCommentDto> productCommentDtos = new ArrayList<>();
        for (ProductInfo productInfo : productInfos){
            //star为总星数，num为总评价数，result=star/num
            ProductCommentDto productCommentDto = new ProductCommentDto();
            //设置productCommentDto属性
            productCommentDto.setProductId(productInfo.getProductId());
            productCommentDto.setProductName(productInfo.getProductName());
            Map<String,Object> m = commentInfoDao.findScoreMapByProductId(productInfo.getProductId());
            productCommentDto.setPackingScore((Map<String, Integer>) m.get("packingScore"));
            productCommentDto.setQualityScore((Map<String, Integer>) m.get("qualityScore"));
            productCommentDto.setTasteScore((Map<String, Integer>) m.get("tasteScore"));
            productCommentDto.setResult((Double) m.get("result"));
            //添加进列表
            productCommentDtos.add(productCommentDto);
        }
        return productCommentDtos;
    }

    @Override
    public List<CommentVO> findInfosByProductId(String productId) {
        List<CommentInfo> commentInfos = commentInfoRepository.findAllByProductIdOrderByCreateTime(productId);
        List<CommentVO> commentVOs = new ArrayList<>();
        for (CommentInfo commentInfo : commentInfos){
            CommentVO commentVO = new CommentVO();
            //通过openid查询用户信息
            UserInfo userInfo = userInfoDao.findByUserOpenid(commentInfo.getUserOpenid());
            if (userInfo == null){
                //如果某条评论的用户信息缺失，直接跳过，即不添加该评论
                continue;
            }
            //将评论信息赋值给VO
            BeanUtils.copyProperties(commentInfo,commentVO);
            //将用户信息赋值给VO
            BeanUtils.copyProperties(userInfo,commentVO);
            //添加进返回列表
            commentVOs.add(commentVO);
        }
        return commentVOs;
    }

    @Override
    public List<ProductCommentDto> findAllDtos() {
        List<ProductInfo> productInfos = productInfoDao.findAll();
        return findDtosByProductInfos(productInfos);
    }

    @Override
    public List<ProductCommentDto> findDtosByProductName(String productName) {
        //使用模糊查询
        productName = "%".concat(productName).concat("%");
        List<ProductInfo> productInfos = productInfoDao.findByProductNameLike(productName);
        return findDtosByProductInfos(productInfos);
    }

    @Override
    public CommentInfo create(CommentInfo commentInfo) {
        if (!productInfoDao.existsById(commentInfo.getProductId())){
            log.error("[商品评价]商品不存在，productId={}",commentInfo.getProductId());
            throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        //设置主键
        commentInfo.setCommentId(KeyUtil.genUniqueKey());
        return commentInfoRepository.save(commentInfo);
    }

    @Override
    public Integer findResultByProductId(String productId) {
        return commentInfoDao.findResultByProductId(productId);
    }
}
