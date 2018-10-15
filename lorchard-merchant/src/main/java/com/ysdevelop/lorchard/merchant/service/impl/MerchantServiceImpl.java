package com.ysdevelop.lorchard.merchant.service.impl;
/**
 * @author oldHuang
 * 
 * @Package com.ysdevelop.loarchard.merchant.controller
 * 
 * */
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysdevelop.lorchard.common.exception.WebServiceException;
import com.ysdevelop.lorchard.common.result.CodeMsg;
import com.ysdevelop.lorchard.common.utils.ApiConstant;
import com.ysdevelop.lorchard.common.utils.Constant;
import com.ysdevelop.lorchard.merchant.entity.Merchant;
import com.ysdevelop.lorchard.merchant.mapper.MerchantDao;
import com.ysdevelop.lorchard.shiro.core.helper.PasswordHelper;
import com.ysdevelop.lorchard.shiro.entity.BaseAuth;
import com.ysdevelop.lorchard.shiro.service.UserService;
import com.ysdevelop.lorchard.shiro.token.TokenManager;
import com.ysdevelop.lorchard.shiro.vo.LoginVo;

@Service
public class MerchantServiceImpl implements UserService {

	@Autowired
	private PasswordHelper passwordHelper;

	@Autowired
	private MerchantDao merchantDao;
	
	
	@Override
	public BaseAuth getUserByName(String name) {
		if (name == null) {
			throw new WebServiceException(CodeMsg.SERVER_ERROR);
		}
		System.out.println("实现类name--->" + name);
		return merchantDao.getUserByName(name);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void register(LoginVo loginVo, HttpSession session) {
		if (loginVo == null) {
			throw new WebServiceException(CodeMsg.SERVER_ERROR);
		}

		// 判断两次密码是否一致
		if (!loginVo.getPassword().equals(loginVo.getSurePassword())) {
			throw new WebServiceException(CodeMsg.MERCHANT_SUREPASSWORD_WRONG);
		}

		if (merchantDao.getUserByName(loginVo.getMobile()) != null) {
			throw new WebServiceException(CodeMsg.MERCHANT_EXISTS);
		}

		Merchant merchant = new Merchant();
		merchant.setLoginName(loginVo.getName());
		merchant.setPassword(loginVo.getPassword());
		merchant.setMobile(loginVo.getMobile());
		passwordHelper.encryptPassword(merchant);
		Integer changeCount = merchantDao.add(merchant);
		if (changeCount != Constant.DEFALULT_ONE) {
			throw new WebServiceException(CodeMsg.SERVER_ERROR);
		}

	}

	@Override
	public void login(LoginVo loginVo) {
		// 校验二维码
		if (!loginVo.getVerifyCode().equals(TokenManager.getSession().getAttribute(Constant.KAPTCHA_SESSION_KEY))) {
			throw new WebServiceException(CodeMsg.MERCHANT_VERIFYCODE_WRONG);
		}
		TokenManager.login(loginVo);
		BaseAuth baseAuth = TokenManager.getToken();
		if (baseAuth.getStatus() == ApiConstant.DEFALULT_ONE) {
			throw new WebServiceException(CodeMsg.MERCHANT_UNOPEN);
		}

	}

	@Override
    @Transactional(rollbackFor=Exception.class)
	public void updateStatusById(Long userId) {
		if(userId == null){
			throw new WebServiceException(CodeMsg.SERVER_ERROR);
		}
		Integer changeCount = merchantDao.updateStatusById(userId);
		if(changeCount != Constant.DEFALULT_ONE){
			throw new WebServiceException(CodeMsg.SERVER_ERROR);
		}
	}

	//店铺打烊，0代表营业，1代表打烊
		@Override
		@Transactional(rollbackFor=Exception.class)
		public void updateBusinessStautsToOne(Long userId,Long businessStauts) {
			
			if(userId == null){
				throw new WebServiceException(CodeMsg.SERVER_ERROR);
			}
			  if (businessStauts == ApiConstant.DEFALULT_ZERO) {
			  Integer changeCount = merchantDao.updateBusinessStautsToOne(userId);
			     if(changeCount != Constant.DEFALULT_ONE){
				 throw new WebServiceException(CodeMsg.SERVER_ERROR);
			    }
		     }else {
					throw new WebServiceException(CodeMsg.SHOP_CLOSE);
				}
		}
		//获取店铺状态
		@Override
		public Long getBusinessStauts(Long userId) {
			Long businessStauts=merchantDao.getBusinessStauts(userId);
			
			return businessStauts;
		}
		//店铺营业，0代表营业，1代表打烊
		@Override
		@Transactional(rollbackFor=Exception.class)
		public void updateBusinessStautsToZero(Long userId, Long businessStauts) {
			if(userId == null){
				throw new WebServiceException(CodeMsg.SERVER_ERROR);
			}
			  if (businessStauts == ApiConstant.DEFALULT_ONE) {
			  Integer changeCount = merchantDao.updateBusinessStautsToZero(userId);
			     if(changeCount != Constant.DEFALULT_ONE){
				 throw new WebServiceException(CodeMsg.SERVER_ERROR);
			    }
		     }else {
					throw new WebServiceException(CodeMsg.SHOP_OPEN);
				}
			
		}

		
	}


